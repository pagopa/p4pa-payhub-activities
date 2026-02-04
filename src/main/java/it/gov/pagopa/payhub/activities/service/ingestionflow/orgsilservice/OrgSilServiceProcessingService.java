package it.gov.pagopa.payhub.activities.service.ingestionflow.orgsilservice;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.organization.OrgSilServiceService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.orgsilservice.OrgSilServiceMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceType;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
@Lazy
@Slf4j
public class OrgSilServiceProcessingService extends IngestionFlowProcessingService<OrgSilServiceIngestionFlowFileDTO, OrgSilServiceIngestionFlowFileResult, OrgSilServiceErrorDTO> {

    private final OrgSilServiceMapper orgSilServiceMapper;
    private final OrgSilServiceService orgSilServiceService;
    private final FileExceptionHandlerService fileExceptionHandlerService;

    public OrgSilServiceProcessingService(
            @Value("${ingestion-flow-files.org-sil-services.max-concurrent-processing-rows}") int maxConcurrentProcessingRows,

            OrgSilServiceMapper orgSilServiceMapper,
            OrgSilServiceErrorsArchiverService orgSilServiceErrorsArchiverService,
            OrganizationService organizationService,
            OrgSilServiceService orgSilServiceService, FileExceptionHandlerService fileExceptionHandlerService) {
        super(maxConcurrentProcessingRows, orgSilServiceErrorsArchiverService, organizationService, fileExceptionHandlerService);
        this.orgSilServiceMapper = orgSilServiceMapper;
        this.orgSilServiceService = orgSilServiceService;
        this.fileExceptionHandlerService = fileExceptionHandlerService;
    }

    public OrgSilServiceIngestionFlowFileResult processOrgSilService(
            Iterator<OrgSilServiceIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        List<OrgSilServiceErrorDTO> errorList = new ArrayList<>();
        OrgSilServiceIngestionFlowFileResult ingestionFlowFileResult = new OrgSilServiceIngestionFlowFileResult();

        ingestionFlowFileResult.setFileVersion(ingestionFlowFile.getFileVersion());
        ingestionFlowFileResult.setOrganizationId(ingestionFlowFile.getOrganizationId());

        String ipaCode = getIpaCodeByOrganizationId(ingestionFlowFile.getOrganizationId());
        ingestionFlowFileResult.setIpaCode(ipaCode);

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected String getSequencingId(OrgSilServiceIngestionFlowFileDTO row) {
        return row.getServiceType() + "-" + row.getApplicationName();
    }

    @Override
    protected List<OrgSilServiceErrorDTO> consumeRow(long lineNumber,
                                OrgSilServiceIngestionFlowFileDTO row,
                                OrgSilServiceIngestionFlowFileResult ingestionFlowFileResult,
                                IngestionFlowFile ingestionFlowFile) {

        try {
            String ipa = ingestionFlowFileResult.getIpaCode();
            if (!row.getIpaCode().equalsIgnoreCase(ipa)) {
                log.error("Organization IPA code {} does not match with the one in the ingestion flow file {}", row.getIpaCode(), ipa);
                OrgSilServiceErrorDTO error = new OrgSilServiceErrorDTO(
                        ingestionFlowFile.getFileName(), row.getIpaCode(), row.getApplicationName(),
                        lineNumber,
                        FileErrorCode.ORGANIZATION_IPA_MISMATCH.name(),
                        FileErrorCode.ORGANIZATION_IPA_MISMATCH.format(row.getIpaCode(), ipa));
                return List.of(error);

            }

            List<OrgSilService> existingServices = orgSilServiceService.getAllByOrganizationIdAndServiceType(
                    ingestionFlowFile.getOrganizationId(), OrgSilServiceType.valueOf(row.getServiceType()));
            OrgSilServiceDTO orgSilServiceMapped = orgSilServiceMapper.map(row, ingestionFlowFile.getOrganizationId());

            if (!existingServices.isEmpty()) {
                existingServices.stream()
                        .filter(s -> s.getApplicationName().equalsIgnoreCase(row.getApplicationName()))
                        .findFirst()
                        .ifPresent(s -> {
                            log.info("Found existing OrgSilService with same applicationName: {}. Updating orgSilServiceId.", row.getApplicationName());
                            orgSilServiceMapped.setOrgSilServiceId(s.getOrgSilServiceId());
                        });
            }

            orgSilServiceService.createOrUpdateOrgSilService(orgSilServiceMapped);
            return Collections.emptyList();

        } catch (Exception e) {
            log.error("Error processing org sil service with organization ipa code:{} and application name {}: {}", row.getIpaCode(), row.getApplicationName(), e.getMessage());
            FileExceptionHandlerService.ErrorDetails errorDetails = fileExceptionHandlerService.mapExceptionToErrorCodeAndMessage(e.getMessage());
            OrgSilServiceErrorDTO error = OrgSilServiceErrorDTO.builder()
                    .fileName(ingestionFlowFile.getFileName())
                    .ipaCode(row.getIpaCode())
                    .applicationName(row.getApplicationName())
                    .errorCode(errorDetails.getErrorCode())
                    .errorMessage(errorDetails.getErrorMessage())
                    .rowNumber(lineNumber)
                    .build();
            return List.of(error);
        }
    }

    @Override
    protected OrgSilServiceErrorDTO buildErrorDto(String fileName, long lineNumber, String errorCode, String message) {
        return OrgSilServiceErrorDTO.builder()
                .fileName(fileName)
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
    }
}
