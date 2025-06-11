package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.csvcomplete;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete.TreasuryCsvCompleteErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete.TreasuryCsvCompleteIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.organization.OrganizationIpaCodeNotMatchException;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.csvcomplete.TreasuryCsvCompleteMapper;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.payhub.activities.util.OrganizationIpaCacheUtils;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Validated
@Service
@Lazy
@Slf4j
public class TreasuryCsvCompleteProcessingService extends IngestionFlowProcessingService<TreasuryCsvCompleteIngestionFlowFileDTO, TreasuryIufIngestionFlowFileResult, TreasuryCsvCompleteErrorDTO> {

    private final TreasuryCsvCompleteMapper treasuryCsvCompleteMapper;
    private final TreasuryService treasuryService;
    private final OrganizationIpaCacheUtils organizationIpaCacheUtils;

    public TreasuryCsvCompleteProcessingService(
            TreasuryCsvCompleteMapper treasuryCsvCompleteMapper,
            TreasuryCsvCompleteErrorsArchiverService treasuryCsvCompleteErrorsArchiverService,
            TreasuryService treasuryService, OrganizationIpaCacheUtils organizationIpaCacheUtils) {
        super(treasuryCsvCompleteErrorsArchiverService);
        this.treasuryCsvCompleteMapper = treasuryCsvCompleteMapper;
        this.treasuryService = treasuryService;
        this.organizationIpaCacheUtils = organizationIpaCacheUtils;
    }

    public TreasuryIufIngestionFlowFileResult processTreasuryCsvComplete(
            Iterator<TreasuryCsvCompleteIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        List<TreasuryCsvCompleteErrorDTO> errorList = new ArrayList<>();
        TreasuryIufIngestionFlowFileResult ingestionFlowFileResult = new TreasuryIufIngestionFlowFileResult();
        ingestionFlowFileResult.setOrganizationId(ingestionFlowFile.getOrganizationId());
        ingestionFlowFileResult.setIuf2TreasuryIdMap(new HashMap<>());

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected boolean consumeRow(long lineNumber, @Valid TreasuryCsvCompleteIngestionFlowFileDTO row, TreasuryIufIngestionFlowFileResult ingestionFlowFileResult, List<TreasuryCsvCompleteErrorDTO> errorList, IngestionFlowFile ingestionFlowFile) {
        try {
            String ipa = organizationIpaCacheUtils.getIpaById(ingestionFlowFile.getOrganizationId());
            if (!row.getOrganizationIpaCode().equalsIgnoreCase(ipa)) {
                String errorMessage = String.format(
                        "Organization IPA code %s does not match with the one in the ingestion flow file %s",
                        row.getOrganizationIpaCode(), ipa);
                log.error(errorMessage);
                throw new OrganizationIpaCodeNotMatchException(errorMessage);
            }
            Treasury treasury = treasuryService.insert(
                    treasuryCsvCompleteMapper.map(row, ingestionFlowFile));

            ingestionFlowFileResult.getIuf2TreasuryIdMap().put(treasury.getIuf(), treasury.getTreasuryId());
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

