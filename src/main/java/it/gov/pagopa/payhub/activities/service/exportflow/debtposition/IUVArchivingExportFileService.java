package it.gov.pagopa.payhub.activities.service.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.IUVInstallmentsExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition.IUVInstallmentsExportFlowFileDTOMapper;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Lazy
@Slf4j
public class IUVArchivingExportFileService {

    private final CsvService csvService;
    private final FileArchiverService fileArchiverService;
    private final String sharedFolder;
    private final String relativeFileFolder;
    private final Path workingDirectory;
    private final IUVInstallmentsExportFlowFileDTOMapper iuvMapper;
    private final IngestionFlowFileService ingestionFlowFileService;

    public IUVArchivingExportFileService(CsvService csvService,
                                         FileArchiverService fileArchiverService,
                                         @Value("${folders.shared}") String sharedFolder,
                                         @Value("${export-flow-files.paid.relative-file-folder}") String relativeFileFolder,
                                         @Value("${folders.tmp}") Path workingDirectory,
                                         IUVInstallmentsExportFlowFileDTOMapper iuvMapper,
                                         IngestionFlowFileService ingestionFlowFileService) {
        this.csvService = csvService;
        this.fileArchiverService = fileArchiverService;
        this.sharedFolder = sharedFolder;
        this.relativeFileFolder = relativeFileFolder;
        this.workingDirectory = workingDirectory;
        this.iuvMapper = iuvMapper;
        this.ingestionFlowFileService = ingestionFlowFileService;
    }

    public Path executeExport(List<DebtPositionDTO> debtPositions, Long ingestionFlowFileId) {
        Optional<IngestionFlowFile> ingestionFlowFile = ingestionFlowFileService.findById(ingestionFlowFileId);
        Path csvFilePath = null;
        if (ingestionFlowFile.isPresent()) {
            csvFilePath = workingDirectory.resolve(relativeFileFolder).resolve(ingestionFlowFile.get().getFilePathName());
        }


        List<IUVInstallmentsExportFlowFileDTO> csvRows = retrieveAndMap(debtPositions, ingestionFlowFileId);

        final boolean[] alreadySupplied = {false};

        try {
            log.info("Creating iuv file with ingestionFlowFileId: {}", ingestionFlowFileId);
            csvService.createCsv(Objects.requireNonNull(csvFilePath), IUVInstallmentsExportFlowFileDTO.class,
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

        Path sharedTargetPath = Path.of(sharedFolder).resolve(relativeFileFolder);
        Path zipFilePath = resolveZipFilePath(csvFilePath);
        createZipArchive(csvFilePath, zipFilePath, sharedTargetPath);

        return zipFilePath;
    }

    private List<IUVInstallmentsExportFlowFileDTO> retrieveAndMap(List<DebtPositionDTO> debtPositions, Long ingestionFlowFileId) {
        return debtPositions.stream()
                .flatMap(dp -> dp.getPaymentOptions().stream())
                .flatMap(po -> po.getInstallments().stream())
                .filter(inst -> ingestionFlowFileId.equals(inst.getIngestionFlowFileId()))
                .map(iuvMapper::map)
                .toList();
    }


    private Path resolveZipFilePath(Path csvFilePath) {
        return csvFilePath.getParent()
                .resolve(Utilities.replaceFileExtension(csvFilePath.getFileName().toString(), "_iuv.zip"));
    }

    private void createZipArchive(Path csvFilePath, Path tmpZipFilePath, Path sharedTargetPath) {
        try {
            fileArchiverService.compressAndArchive(List.of(csvFilePath), tmpZipFilePath, sharedTargetPath);
        } catch (IOException e) {
            throw new IllegalStateException("Error during compression and archiving: " + e.getMessage(), e);
        }
    }
}
