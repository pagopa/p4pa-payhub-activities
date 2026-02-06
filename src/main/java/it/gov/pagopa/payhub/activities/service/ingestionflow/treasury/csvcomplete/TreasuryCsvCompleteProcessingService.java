package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.csvcomplete;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete.TreasuryCsvCompleteErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete.TreasuryCsvCompleteIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete.TreasuryCsvCompleteIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.csvcomplete.TreasuryCsvCompleteMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
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
public class TreasuryCsvCompleteProcessingService extends IngestionFlowProcessingService<TreasuryCsvCompleteIngestionFlowFileDTO, TreasuryIufIngestionFlowFileResult, TreasuryCsvCompleteErrorDTO> {

    private final TreasuryCsvCompleteMapper treasuryCsvCompleteMapper;
    private final TreasuryService treasuryService;

    public TreasuryCsvCompleteProcessingService(
            @Value("${ingestion-flow-files.treasuries.max-concurrent-processing-rows}") int maxConcurrentProcessingRows,

            TreasuryCsvCompleteMapper treasuryCsvCompleteMapper,
            TreasuryCsvCompleteErrorsArchiverService treasuryCsvCompleteErrorsArchiverService,
            TreasuryService treasuryService,
            OrganizationService organizationService,
            FileExceptionHandlerService fileExceptionHandlerService) {
        super(maxConcurrentProcessingRows, treasuryCsvCompleteErrorsArchiverService, organizationService, fileExceptionHandlerService);
        this.treasuryCsvCompleteMapper = treasuryCsvCompleteMapper;
        this.treasuryService = treasuryService;
    }

    public TreasuryIufIngestionFlowFileResult processTreasuryCsvComplete(
            Iterator<TreasuryCsvCompleteIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        List<TreasuryCsvCompleteErrorDTO> errorList = new ArrayList<>();
        TreasuryCsvCompleteIngestionFlowFileResult ingestionFlowFileResult = new TreasuryCsvCompleteIngestionFlowFileResult();
        ingestionFlowFileResult.setIuf2TreasuryIdMap(new HashMap<>());

        String ipaCode = getIpaCodeByOrganizationId(ingestionFlowFile.getOrganizationId());
        ingestionFlowFileResult.setIpaCode(ipaCode);

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected String getSequencingId(TreasuryCsvCompleteIngestionFlowFileDTO row) {
        return row.getBillCode() + "-" + row.getBillYear();
    }

    @Override
    protected List<TreasuryCsvCompleteErrorDTO> consumeRow(long lineNumber,
                                                           TreasuryCsvCompleteIngestionFlowFileDTO row,
                                                           TreasuryIufIngestionFlowFileResult ingestionFlowFileResult,
                                                           IngestionFlowFile ingestionFlowFile) {
        TreasuryCsvCompleteIngestionFlowFileResult treasuryCsvCompleteIngestionFlowFileResult = (TreasuryCsvCompleteIngestionFlowFileResult) ingestionFlowFileResult;
        String ipa = treasuryCsvCompleteIngestionFlowFileResult.getIpaCode();

        if (!row.getOrganizationIpaCode().equalsIgnoreCase(ipa)) {
            log.error("Organization IPA code {} does not match with the one in the ingestion flow file {}", row.getOrganizationIpaCode(), ipa);
            TreasuryCsvCompleteErrorDTO error = buildErrorDto(
                    ingestionFlowFile, lineNumber, row,
                    FileErrorCode.ORGANIZATION_IPA_MISMATCH.name(),
                    FileErrorCode.ORGANIZATION_IPA_MISMATCH.format(row.getOrganizationIpaCode(), ipa));
            return List.of(error);
        }

        TreasuryIuf existingTreasury;
        if (row.getIuf() != null) {
            existingTreasury = treasuryService.getByOrganizationIdAndIuf(ingestionFlowFile.getOrganizationId(), row.getIuf());
            if (existingTreasury != null) {
                boolean treasuryMatch = !Objects.equals(existingTreasury.getBillCode(), row.getBillCode()) || !Objects.equals(existingTreasury.getBillYear(), row.getBillYear());
                if (treasuryMatch) {
                    log.error("IUF {} already associated to another treasury for organization with IPA code {}", row.getIuf(), ipa);
                    TreasuryCsvCompleteErrorDTO error = buildErrorDto(
                            ingestionFlowFile, lineNumber, row,
                            FileErrorCode.IUF_ALREADY_ASSOCIATED.name(),
                            FileErrorCode.IUF_ALREADY_ASSOCIATED.format(row.getIuf(), ipa));
                    return List.of(error);
                }
            }
        }

        Treasury treasury = treasuryService.insert(
                treasuryCsvCompleteMapper.map(row, ingestionFlowFile));

        String treasuryId = treasury.getTreasuryId();
        ingestionFlowFileResult.getIuf2TreasuryIdMap().put(
                treasury.getIuf() == null ? generateTechnicalIuf(treasuryId) : treasury.getIuf(),
                treasuryId
        );
        return Collections.emptyList();
    }

    @Override
    protected TreasuryCsvCompleteErrorDTO buildErrorDto(IngestionFlowFile ingestionFlowFile, long lineNumber, TreasuryCsvCompleteIngestionFlowFileDTO row, String errorCode, String message) {
        TreasuryCsvCompleteErrorDTO errorDTO = TreasuryCsvCompleteErrorDTO.builder()
                .fileName(ingestionFlowFile.getFileName())
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
        if (row != null) {
            errorDTO.setIuv(row.getIuv());
            errorDTO.setIuf(row.getIuf());
        }
        return errorDTO;
    }
}