package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.csv;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csv.TreasuryCsvErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csv.TreasuryCsvIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.csv.TreasuryCsvMapper;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Service
@Lazy
@Slf4j
public class TreasuryCsvProcessingService extends IngestionFlowProcessingService<TreasuryCsvIngestionFlowFileDTO, TreasuryIufIngestionFlowFileResult, TreasuryCsvErrorDTO> {

    private final TreasuryCsvMapper treasuryCsvMapper;
    private final TreasuryService treasuryService;

    public TreasuryCsvProcessingService(
            TreasuryCsvMapper treasuryCsvMapper,
            TreasuryCsvErrorsArchiverService treasuryCsvErrorsArchiverService,
            TreasuryService treasuryService,
            OrganizationService organizationService) {
        super(treasuryCsvErrorsArchiverService, organizationService);
        this.treasuryCsvMapper = treasuryCsvMapper;
        this.treasuryService = treasuryService;
    }

    public TreasuryIufIngestionFlowFileResult processTreasuryCsv(
            Iterator<TreasuryCsvIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        List<TreasuryCsvErrorDTO> errorList = new ArrayList<>();
        TreasuryIufIngestionFlowFileResult ingestionFlowFileResult = new TreasuryIufIngestionFlowFileResult();
        ingestionFlowFileResult.setOrganizationId(ingestionFlowFile.getOrganizationId());
        ingestionFlowFileResult.setFileVersion(ingestionFlowFile.getFileVersion());
        ingestionFlowFileResult.setIuf2TreasuryIdMap(new HashMap<>());

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected boolean consumeRow(long lineNumber, TreasuryCsvIngestionFlowFileDTO row, TreasuryIufIngestionFlowFileResult ingestionFlowFileResult, List<TreasuryCsvErrorDTO> errorList, IngestionFlowFile ingestionFlowFile) {
        String ipa = getIpaCodeByOrganizationId(ingestionFlowFile.getOrganizationId());
        String rowIuf = TreasuryUtils.getIdentificativo(row.getRemittanceDescription(), TreasuryUtils.IUF);

        try {
            if (rowIuf != null) {
                TreasuryIuf existingTreasury = treasuryService.getByOrganizationIdAndIuf(ingestionFlowFileResult.getOrganizationId(), rowIuf);

                if (existingTreasury != null) {
                    boolean treasuryMatch = !existingTreasury.getBillCode().equals(row.getBillCode()) || !existingTreasury.getBillYear().equals(row.getBillYear());
                    if (treasuryMatch) {
                        String errorMessage = String.format(
                                "IUF %s already associated to another treasury for organization with IPA code %s",
                                rowIuf, ipa);
                        log.error(errorMessage);
                        TreasuryCsvErrorDTO error = new TreasuryCsvErrorDTO(
                                ingestionFlowFile.getFileName(),
                                rowIuf,
                                lineNumber, "IUF_ALREADY_ASSOCIATED", errorMessage);
                        errorList.add(error);
                        return false;
                    }
                }
            }

            Treasury treasury = treasuryService.insert(
                    treasuryCsvMapper.map(row, ingestionFlowFile));

            if (treasury.getIuf() != null) {
                ingestionFlowFileResult.getIuf2TreasuryIdMap().put(treasury.getIuf(), treasury.getTreasuryId());
            }

            return true;
        } catch (Exception e) {
            log.error("Error processing treasury csv with iuf {}: {}",
                    rowIuf,
                    e.getMessage());
            TreasuryCsvErrorDTO error = new TreasuryCsvErrorDTO(
                    ingestionFlowFile.getFileName(),
                    rowIuf,
                    lineNumber, "PROCESS_EXCEPTION", e.getMessage());
            errorList.add(error);
            log.info("Current error list size after handleProcessingError: {}", errorList.size());
            return false;
        }
    }

    @Override
    protected TreasuryCsvErrorDTO buildErrorDto(String fileName, long lineNumber, String errorCode, String message) {
        return TreasuryCsvErrorDTO.builder()
                .fileName(fileName)
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
    }
}
