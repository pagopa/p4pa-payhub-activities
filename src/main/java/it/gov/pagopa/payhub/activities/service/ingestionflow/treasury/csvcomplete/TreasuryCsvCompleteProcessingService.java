package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.csvcomplete;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryCsvCompleteIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete.TreasuryCsvCompleteErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete.TreasuryCsvCompleteIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.organization.OrganizationIpaCodeNotMatchException;
import it.gov.pagopa.payhub.activities.exception.organization.OrganizationNotFoundException;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.csvcomplete.TreasuryCsvCompleteMapper;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;
import java.util.*;

@Validated
@Service
@Lazy
@Slf4j
public class TreasuryCsvCompleteProcessingService extends IngestionFlowProcessingService<TreasuryCsvCompleteIngestionFlowFileDTO, TreasuryIufIngestionFlowFileResult, TreasuryCsvCompleteErrorDTO> {

    private final TreasuryCsvCompleteMapper treasuryCsvCompleteMapper;
    private final TreasuryService treasuryService;
    private final OrganizationService organizationService;

    public TreasuryCsvCompleteProcessingService(
            TreasuryCsvCompleteMapper treasuryCsvCompleteMapper,
            TreasuryCsvCompleteErrorsArchiverService treasuryCsvCompleteErrorsArchiverService,
            TreasuryService treasuryService,
            OrganizationService organizationService) {
        super(treasuryCsvCompleteErrorsArchiverService);
        this.treasuryCsvCompleteMapper = treasuryCsvCompleteMapper;
        this.treasuryService = treasuryService;
        this.organizationService = organizationService;
    }

    public void loadIpaCode(Long organizationId, TreasuryCsvCompleteIngestionFlowFileResult ingestionFlowFileResult){
        Optional<Organization> organizationOptional =
                organizationService.getOrganizationById(organizationId);
        if (organizationOptional.isEmpty()) {
            String errorMessage = String.format("Organization with id %s not found", organizationId);
            log.error(errorMessage);
            throw new OrganizationNotFoundException(errorMessage);
        } else {
            ingestionFlowFileResult.setIpaCode(organizationOptional.get().getIpaCode());
        }
    }

    public TreasuryIufIngestionFlowFileResult processTreasuryCsvComplete(
            Iterator<TreasuryCsvCompleteIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        List<TreasuryCsvCompleteErrorDTO> errorList = new ArrayList<>();
        TreasuryCsvCompleteIngestionFlowFileResult ingestionFlowFileResult = new TreasuryCsvCompleteIngestionFlowFileResult();
        ingestionFlowFileResult.setOrganizationId(ingestionFlowFile.getOrganizationId());
        ingestionFlowFileResult.setIuf2TreasuryIdMap(new HashMap<>());
        loadIpaCode(ingestionFlowFile.getOrganizationId(), ingestionFlowFileResult);
        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected boolean consumeRow(long lineNumber, @Valid TreasuryCsvCompleteIngestionFlowFileDTO row, TreasuryIufIngestionFlowFileResult ingestionFlowFileResult, List<TreasuryCsvCompleteErrorDTO> errorList, IngestionFlowFile ingestionFlowFile) {
        TreasuryCsvCompleteIngestionFlowFileResult treasuryCsvCompleteIngestionFlowFileResult = (TreasuryCsvCompleteIngestionFlowFileResult) ingestionFlowFileResult;
        try {
            String ipa = treasuryCsvCompleteIngestionFlowFileResult.getIpaCode();
            if (!row.getOrganizationIpaCode().equalsIgnoreCase(ipa)) {
                String errorMessage = String.format(
                        "Organization IPA code %s does not match with the one in the ingestion flow file %s",
                        row.getOrganizationIpaCode(), ipa);
                log.error(errorMessage);
                throw new OrganizationIpaCodeNotMatchException(errorMessage);
            }
            Treasury treasury = treasuryService.insert(
                    treasuryCsvCompleteMapper.map(row, ingestionFlowFile));

            treasuryCsvCompleteIngestionFlowFileResult.getIuf2TreasuryIdMap().put(treasury.getIuf(), treasury.getTreasuryId());
            return true;
        } catch (Exception e) {
            log.error("Error processing treasury csv complete with iuf {} and iuv {}: {}",
                    row.getIuf(), row.getIuv(),
                    e.getMessage());
            TreasuryCsvCompleteErrorDTO error = new TreasuryCsvCompleteErrorDTO(
                    ingestionFlowFile.getFileName(), row.getIuv(),
                    null, lineNumber, "PROCESS_EXCEPTION", e.getMessage());
            errorList.add(error);
            log.info("Current error list size after handleProcessingError: {}", errorList.size());
            return false;
        }
    }

    @Override
    protected TreasuryCsvCompleteErrorDTO buildErrorDto(String fileName, long lineNumber, String errorCode, String message) {
        return TreasuryCsvCompleteErrorDTO.builder()
                .fileName(fileName)
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
    }
}

