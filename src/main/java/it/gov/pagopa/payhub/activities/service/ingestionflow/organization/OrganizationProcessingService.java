package it.gov.pagopa.payhub.activities.service.ingestionflow.organization;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationApiService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.organization.OrganizationMapper;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.organization.dto.generated.KeyTypeEnum;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
@Slf4j
public class OrganizationProcessingService extends IngestionFlowProcessingService<OrganizationIngestionFlowFileDTO, OrganizationIngestionFlowFileResult, OrganizationErrorDTO> {

    private final OrganizationMapper organizationMapper;
    private final OrganizationService organizationService;
    private final OrganizationApiService organizationApiService;


    public OrganizationProcessingService(
            OrganizationMapper organizationMapper,
            OrganizationErrorsArchiverService organizationErrorsArchiverService,
        OrganizationService organizationService, OrganizationApiService organizationApiService) {
        super(organizationErrorsArchiverService);
        this.organizationMapper = organizationMapper;
        this.organizationService = organizationService;
      this.organizationApiService = organizationApiService;
    }


    public OrganizationIngestionFlowFileResult processOrganization(
            Iterator<OrganizationIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        List<OrganizationErrorDTO> errorList = new ArrayList<>();
        OrganizationIngestionFlowFileResult ingestionFlowFileResult = new OrganizationIngestionFlowFileResult();
        ingestionFlowFileResult.setOrganizationIpaCodeList(new ArrayList<>());

        Optional<Organization> organizationBroker = organizationService.getOrganizationById(ingestionFlowFile.getOrganizationId());
        Long brokerId = organizationBroker.map(Organization::getBrokerId).orElse(null);
        if (brokerId == null) {
            log.error("Broker for organization id {} not found", ingestionFlowFile.getOrganizationId());
            ingestionFlowFileResult.setErrorDescription("Broker not found");
            return ingestionFlowFileResult;
        }
        ingestionFlowFileResult.setBrokerId(organizationBroker.get().getBrokerId());
        ingestionFlowFileResult.setBrokerFiscalCode(organizationBroker.get().getOrgFiscalCode());

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected boolean consumeRow(long lineNumber, OrganizationIngestionFlowFileDTO organizationDTO, OrganizationIngestionFlowFileResult ingestionFlowFileResult, List<OrganizationErrorDTO> errorList, IngestionFlowFile ingestionFlowFile) {
        try {
            if(ingestionFlowFileResult.getBrokerFiscalCode().equals(organizationDTO.getBrokerCf())){

            Organization organizationCreated = organizationService.createOrganization(
                    organizationMapper.map(organizationDTO, ingestionFlowFileResult.getBrokerId()));
            ingestionFlowFileResult.getOrganizationIpaCodeList().add(organizationCreated.getIpaCode());
            saveApiKeys(organizationCreated.getOrganizationId(), organizationDTO);
            return true;
            }
            else{
                log.error("Broker with fiscal code {} not master for organization whit fiscal code {}", organizationDTO.getBrokerCf(), organizationDTO.getOrgFiscalCode());
                OrganizationErrorDTO error = new OrganizationErrorDTO(
                        ingestionFlowFile.getFileName(), organizationDTO.getIpaCode(),
                        lineNumber, "BROKER_NOT_MATCHED", "Broker not matched");
                errorList.add(error);
                return false;
            }
        } catch (Exception e) {
            log.error("Error processing organization with ipa code {}: {}",
                    organizationDTO.getIpaCode(), e.getMessage());
            OrganizationErrorDTO error = new OrganizationErrorDTO(
                    ingestionFlowFile.getFileName(), organizationDTO.getIpaCode(),
                    lineNumber, "PROCESS_EXCEPTION", e.getMessage());
            errorList.add(error);
            log.info("Current error list size after handleProcessingError: {}", errorList.size());
            return false;
        }
    }

    @Override
    protected OrganizationErrorDTO buildErrorDto(String fileName, long lineNumber, String errorCode, String message) {
        return OrganizationErrorDTO.builder()
                .fileName(fileName)
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
    }

    private void saveApiKeys(Long organizationId, OrganizationIngestionFlowFileDTO organizationDTO) {
        saveApiKeyIfPresent(organizationId, KeyTypeEnum.IO, organizationDTO.getIoApiKey());
        saveApiKeyIfPresent(organizationId, KeyTypeEnum.SEND, organizationDTO.getSendApiKey());
    }

     private void saveApiKeyIfPresent(Long organizationId, KeyTypeEnum keyType, String apiKey) {
        if (!StringUtils.isEmpty(apiKey)) {
            OrganizationApiKeys apiKeys = OrganizationApiKeys.builder()
                .keyType(keyType)
                .apiKey(apiKey)
                .build();
            organizationApiService.encryptAndSaveApiKey(organizationId, apiKeys);
        }
    }

}

