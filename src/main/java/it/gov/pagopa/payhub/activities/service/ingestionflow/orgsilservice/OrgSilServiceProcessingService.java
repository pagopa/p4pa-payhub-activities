package it.gov.pagopa.payhub.activities.service.ingestionflow.orgsilservice;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.organization.OrgSilServiceService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.orgsilservice.OrgSilServiceMapper;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Lazy
@Slf4j
public class OrgSilServiceProcessingService extends IngestionFlowProcessingService<OrgSilServiceIngestionFlowFileDTO, OrgSilServiceIngestionFlowFileResult, OrgSilServiceErrorDTO> {

    private final OrgSilServiceMapper orgSilServiceMapper;
    private final OrgSilServiceService orgSilServiceService;

    public OrgSilServiceProcessingService(
            OrgSilServiceMapper orgSilServiceMapper,
            OrgSilServiceErrorsArchiverService orgSilServiceErrorsArchiverService,
            OrganizationService organizationService,
            OrgSilServiceService orgSilServiceService) {
        super(orgSilServiceErrorsArchiverService, organizationService);
        this.orgSilServiceMapper = orgSilServiceMapper;
        this.orgSilServiceService = orgSilServiceService;
    }

    public OrgSilServiceIngestionFlowFileResult processOrgSilService(
            Iterator<OrgSilServiceIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        List<OrgSilServiceErrorDTO> errorList = new ArrayList<>();
        OrgSilServiceIngestionFlowFileResult ingestionFlowFileResult = new OrgSilServiceIngestionFlowFileResult();

        ingestionFlowFileResult.setFileVersion("1.0");
        ingestionFlowFileResult.setOrganizationId(ingestionFlowFile.getOrganizationId());

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected boolean consumeRow(long lineNumber, OrgSilServiceIngestionFlowFileDTO row, OrgSilServiceIngestionFlowFileResult ingestionFlowFileResult, List<OrgSilServiceErrorDTO> errorList, IngestionFlowFile ingestionFlowFile) {

        try {
            Optional <Organization> optionalOrganization = organizationService.getOrganizationByIpaCode(row.getIpaCode());
            Organization organization = null;

            if (optionalOrganization.isEmpty()) {
                OrgSilServiceErrorDTO error = OrgSilServiceErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .ipaCode(row.getIpaCode())
                        .applicationName(row.getApplicationName())
                        .rowNumber(lineNumber)
                        .errorCode("ORGANIZATION_NOT_FOUND")
                        .errorMessage("Organization not found for IPA code: " + row.getIpaCode())
                        .build();
                errorList.add(error);
                return false;
            }else {
                organization = optionalOrganization.get();
            }

            orgSilServiceService.createOrUpdateOrgSilService(orgSilServiceMapper.map(row, organization.getOrganizationId()));
            return true;

        } catch (Exception e) {
            log.error("Error processing org sil service with organization ipa code:{} and application name {}: {}", row.getIpaCode(), row.getApplicationName(), e.getMessage());
            OrgSilServiceErrorDTO error = OrgSilServiceErrorDTO.builder()
                    .fileName(ingestionFlowFile.getFileName())
                    .ipaCode(row.getIpaCode())
                    .applicationName(row.getApplicationName())
                    .errorCode("PROCESS_EXCEPTION")
                    .errorMessage(e.getMessage())
                    .rowNumber(lineNumber)
                    .build();
            errorList.add(error);
            log.info("Current error list size after handleProcessingError: {}", errorList.size());
            return false;
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

