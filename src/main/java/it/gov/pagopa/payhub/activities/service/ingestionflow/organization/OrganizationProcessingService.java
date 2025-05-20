package it.gov.pagopa.payhub.activities.service.ingestionflow.organization;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.organization.BrokerService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.organization.OrganizationMapper;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
@Slf4j
public class OrganizationProcessingService extends IngestionFlowProcessingService<OrganizationIngestionFlowFileDTO, OrganizationIngestionFlowFileResult, OrganizationErrorDTO> {

    private final OrganizationMapper organizationMapper;
    private final OrganizationService organizationService;
    private final BrokerService brokerService;


    public OrganizationProcessingService(
            OrganizationMapper organizationMapper,
            OrganizationErrorsArchiverService organizationErrorsArchiverService,
        OrganizationService organizationService, BrokerService brokerService) {
        super(organizationErrorsArchiverService);
        this.organizationMapper = organizationMapper;
        this.organizationService = organizationService;
        this.brokerService = brokerService;
    }


    public OrganizationIngestionFlowFileResult processOrganization(
            Iterator<OrganizationIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        List<OrganizationErrorDTO> errorList = new ArrayList<>();
        OrganizationIngestionFlowFileResult ingestionFlowFileResult = new OrganizationIngestionFlowFileResult();
        ingestionFlowFileResult.setOrganizationIpaCodeList(new ArrayList<>());

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected boolean consumeRow(long lineNumber, OrganizationIngestionFlowFileDTO organizationDTO, OrganizationIngestionFlowFileResult ingestionFlowFileResult, List<OrganizationErrorDTO> errorList, IngestionFlowFile ingestionFlowFile) {
        try {
            Broker broker = brokerService.getBrokerByFiscalCode(organizationDTO.getBrokerCf());
            if (broker == null) {
                log.error("Broker with fiscal code {} not found", organizationDTO.getBrokerCf());
                OrganizationErrorDTO error = new OrganizationErrorDTO(
                        ingestionFlowFile.getFileName(), organizationDTO.getIpaCode(),
                        lineNumber, "BROKER_NOT_FOUND", "Broker not found");
                errorList.add(error);
                return false;
            }
            Organization organizationCreated = organizationService.createOrganization(
                    organizationMapper.map(organizationDTO, ingestionFlowFile, broker.getBrokerId()));
            ingestionFlowFileResult.getOrganizationIpaCodeList().add(organizationCreated.getIpaCode());
            return true;
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
}

