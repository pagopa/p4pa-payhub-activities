package it.gov.pagopa.payhub.activities.service.ingestionflow.orgsilservice;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.organization.OrgSilServiceService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.orgsilservice.OrgSilServiceMapper;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceType;
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

        ingestionFlowFileResult.setFileVersion(ingestionFlowFile.getFileVersion());
        ingestionFlowFileResult.setOrganizationId(ingestionFlowFile.getOrganizationId());

        String ipaCode = getIpaCodeByOrganizationId(ingestionFlowFile.getOrganizationId());
        ingestionFlowFileResult.setIpaCode(ipaCode);

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected boolean consumeRow(long lineNumber, OrgSilServiceIngestionFlowFileDTO row, OrgSilServiceIngestionFlowFileResult ingestionFlowFileResult, List<OrgSilServiceErrorDTO> errorList, IngestionFlowFile ingestionFlowFile) {

        try {
            String ipa = ingestionFlowFileResult.getIpaCode();
            if (!row.getIpaCode().equalsIgnoreCase(ipa)) {
                String errorMessage = String.format(
                        "Organization IPA code %s does not match with the one in the ingestion flow file %s",
                        row.getIpaCode(), ipa);
                log.error(errorMessage);
                OrgSilServiceErrorDTO error = new OrgSilServiceErrorDTO(
                        ingestionFlowFile.getFileName(), row.getIpaCode(), row.getApplicationName(),
                        lineNumber, "ORGANIZATION_IPA_DOES_NOT_MATCH", errorMessage);
                errorList.add(error);
                return false;

            }
            Optional <Organization> optionalOrganization = organizationService.getOrganizationByIpaCode(row.getIpaCode());
            Organization organization = null;

            if(optionalOrganization.isEmpty()) {
                log.error("Organization with IPA code {} does not exist", row.getIpaCode());
                String errorMessage = String.format(
                        "Organization with IPA code %s does not exist", row.getIpaCode());
                OrgSilServiceErrorDTO error = new OrgSilServiceErrorDTO(
                        ingestionFlowFile.getFileName(), row.getIpaCode(), row.getApplicationName(),
                        lineNumber, "ORGANIZATION_IPA_DOES_NOT_EXISTS", errorMessage);
                errorList.add(error);
                return false;
            }
            else
                organization = optionalOrganization.get();

            List<OrgSilService> existingServices = orgSilServiceService.getAllByOrganizationIdAndServiceType(
                    organization.getOrganizationId(), OrgSilServiceType.valueOf(row.getServiceType()));
            OrgSilServiceDTO orgSilServiceMapped = orgSilServiceMapper.map(row, organization.getOrganizationId());

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
