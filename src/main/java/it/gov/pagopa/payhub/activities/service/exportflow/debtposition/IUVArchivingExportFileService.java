package it.gov.pagopa.payhub.activities.service.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.config.FoldersPathsConfig;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.IUVInstallmentsExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition.IUVInstallmentsExportFlowFileDTOMapper;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.util.FileShareUtils;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
@Lazy
@Slf4j
public class IUVArchivingExportFileService {

    private final CsvService csvService;
    private final FileArchiverService fileArchiverService;
    private final FoldersPathsConfig foldersPathsConfig;
    private final IUVInstallmentsExportFlowFileDTOMapper iuvMapper;
    private final IngestionFlowFileService ingestionFlowFileService;

    public IUVArchivingExportFileService(CsvService csvService,
                                         FileArchiverService fileArchiverService,
                                         FoldersPathsConfig foldersPathsConfig,
                                         IUVInstallmentsExportFlowFileDTOMapper iuvMapper,
                                         IngestionFlowFileService ingestionFlowFileService) {
        this.csvService = csvService;
        this.fileArchiverService = fileArchiverService;
        this.foldersPathsConfig = foldersPathsConfig;
        this.iuvMapper = iuvMapper;
        this.ingestionFlowFileService = ingestionFlowFileService;
    }

    public Path executeExport(List<DebtPositionDTO> debtPositions, Long ingestionFlowFileId) {
        IngestionFlowFile ingestionFlowFile = ingestionFlowFileService.findById(ingestionFlowFileId)
                .orElseThrow(() -> new IngestionFlowFileNotFoundException(String.format("IngestionFlowFile with id %s was not found", ingestionFlowFileId)));

        Path csvFilePath = resolveCsvFilePath(ingestionFlowFile);

        List<IUVInstallmentsExportFlowFileDTO> csvRows = filterAndMap(debtPositions, ingestionFlowFileId);

        final boolean[] alreadySupplied = {false};

        try {
            log.info("Creating iuv file with ingestionFlowFileId: {}", ingestionFlowFileId);
            csvService.createCsv(csvFilePath, IUVInstallmentsExportFlowFileDTO.class,
                    () -> {
                        if (!alreadySupplied[0]) {
                            alreadySupplied[0] = true;
                            return csvRows;
                        }
                        return List.of();
                    },
                    null);
        } catch (IOException e) {
            throw new IllegalStateException("Error writing to CSV file: " + e.getMessage(), e);
        }

        Path sharedTargetPath = FileShareUtils.buildOrganizationBasePath(foldersPathsConfig.getShared(), ingestionFlowFile.getOrganizationId())
                        .resolve(ingestionFlowFile.getFilePathName()).resolve(foldersPathsConfig.getProcessTargetSubFolders().getArchive());
        Path zipFilePath = resolveZipFilePath(csvFilePath);
        createZipArchive(csvFilePath, zipFilePath, sharedTargetPath);

        return zipFilePath;
    }

    private List<IUVInstallmentsExportFlowFileDTO> filterAndMap(List<DebtPositionDTO> debtPositions, Long ingestionFlowFileId) {
        return debtPositions.stream()
                .flatMap(dp -> dp.getPaymentOptions().stream()
                                .flatMap(po ->
                                        po.getInstallments().stream()
                                                .filter(inst -> ingestionFlowFileId.equals(inst.getIngestionFlowFileId()))
                                                .map(inst -> iuvMapper.map(inst, dp.getDebtPositionTypeOrgId()))
                                ))
                .toList();
    }

    private Path resolveCsvFilePath(IngestionFlowFile ingestionFlowFile) {
        return FileShareUtils.buildOrganizationBasePath(foldersPathsConfig.getTmp(), ingestionFlowFile.getOrganizationId())
                .resolve(ingestionFlowFile.getFilePathName())
                .resolve(ingestionFlowFile.getFileName().replace(".zip", "_iuv.csv"));
    }

    private Path resolveZipFilePath(Path csvFilePath) {
        return csvFilePath.getParent()
                .resolve(Utilities.replaceFileExtension(csvFilePath.getFileName().toString(), ".zip"));
    }

    private void createZipArchive(Path csvFilePath, Path tmpZipFilePath, Path sharedTargetPath) {
        try {
            fileArchiverService.compressAndArchive(List.of(csvFilePath), tmpZipFilePath, sharedTargetPath);
        } catch (IOException e) {
            throw new IllegalStateException("Error during compression and archiving: " + e.getMessage(), e);
        }
    }
}
