package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.csv;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csv.TreasuryCsvErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csv.TreasuryCsvIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.csv.TreasuryCsvMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.*;

import static it.gov.pagopa.payhub.activities.util.TreasuryUtils.generateTechnicalIuf;

@Service
@Lazy
@Slf4j
public class TreasuryCsvProcessingService extends IngestionFlowProcessingService<TreasuryCsvIngestionFlowFileDTO, TreasuryIufIngestionFlowFileResult, TreasuryCsvErrorDTO> {

    private final TreasuryCsvMapper treasuryCsvMapper;
    private final TreasuryService treasuryService;

    public TreasuryCsvProcessingService(
            @Value("${ingestion-flow-files.treasuries.max-concurrent-processing-rows}") int maxConcurrentProcessingRows,

            TreasuryCsvMapper treasuryCsvMapper,
            TreasuryCsvErrorsArchiverService treasuryCsvErrorsArchiverService,
            TreasuryService treasuryService,
            OrganizationService organizationService,
            FileExceptionHandlerService fileExceptionHandlerService) {
        super(maxConcurrentProcessingRows, treasuryCsvErrorsArchiverService, organizationService, fileExceptionHandlerService);
        this.treasuryCsvMapper = treasuryCsvMapper;
        this.treasuryService = treasuryService;
    }

    public TreasuryIufIngestionFlowFileResult processTreasuryCsv(
            Iterator<TreasuryCsvIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        List<TreasuryCsvErrorDTO> errorList = new ArrayList<>();
        TreasuryIufIngestionFlowFileResult ingestionFlowFileResult = new TreasuryIufIngestionFlowFileResult();
        ingestionFlowFileResult.setIuf2TreasuryIdMap(new HashMap<>());

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected String getSequencingId(TreasuryCsvIngestionFlowFileDTO row) {
        return row.getBillCode() + "-" + row.getBillYear();
    }

    @Override
    protected List<TreasuryCsvErrorDTO> consumeRow(long lineNumber,
                                                   TreasuryCsvIngestionFlowFileDTO row,
                                                   TreasuryIufIngestionFlowFileResult ingestionFlowFileResult,
                                                   IngestionFlowFile ingestionFlowFile) {
        String ipa = getIpaCodeByOrganizationId(ingestionFlowFile.getOrganizationId());
        String rowIuf = TreasuryUtils.getIdentificativo(row.getRemittanceDescription(), TreasuryUtils.IUF);

        if (rowIuf != null) {
            TreasuryIuf existingTreasury = treasuryService.getByOrganizationIdAndIuf(ingestionFlowFile.getOrganizationId(), rowIuf);

            if (existingTreasury != null) {
                boolean treasuryMatch = !existingTreasury.getBillCode().equals(row.getBillCode()) || !existingTreasury.getBillYear().equals(row.getBillYear());
                if (treasuryMatch) {
                    log.error("IUF {} already associated to another treasury for organization with IPA code {}", rowIuf, ipa);
                    TreasuryCsvErrorDTO error = buildErrorDto(
                            ingestionFlowFile, lineNumber, row,
                            FileErrorCode.IUF_ALREADY_ASSOCIATED.name(),
                            FileErrorCode.IUF_ALREADY_ASSOCIATED.format(rowIuf, ipa));
                    return List.of(error);
                }
            }
        }

        Treasury treasury = treasuryService.insert(
                treasuryCsvMapper.map(row, ingestionFlowFile));

        String treasuryId = treasury.getTreasuryId();
        ingestionFlowFileResult.getIuf2TreasuryIdMap().put(
                treasury.getIuf() == null ? generateTechnicalIuf(treasuryId) : treasury.getIuf(),
                treasuryId
        );

        return Collections.emptyList();
    }

    @Override
    protected TreasuryCsvErrorDTO buildErrorDto(IngestionFlowFile ingestionFlowFile, long lineNumber, TreasuryCsvIngestionFlowFileDTO row, String errorCode, String message) {
        TreasuryCsvErrorDTO errorDTO = TreasuryCsvErrorDTO.builder()
                .fileName(ingestionFlowFile.getFileName())
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
        if (row != null) {
            errorDTO.setIuf(TreasuryUtils.getIdentificativo(row.getRemittanceDescription(), TreasuryUtils.IUF));
        }
        return errorDTO;
    }
}
