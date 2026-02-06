package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.xls;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.xls.TreasuryXlsErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.xls.TreasuryXlsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.xls.TreasuryXlsMapper;
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
import java.time.LocalDate;
import java.util.*;

@Service
@Lazy
@Slf4j
public class TreasuryXlsProcessingService extends IngestionFlowProcessingService<TreasuryXlsIngestionFlowFileDTO, TreasuryIufIngestionFlowFileResult, TreasuryXlsErrorDTO> {

    private final TreasuryXlsMapper treasuryXlsMapper;
    private final TreasuryService treasuryService;

    public TreasuryXlsProcessingService(
            @Value("${ingestion-flow-files.treasuries.max-concurrent-processing-rows}") int maxConcurrentProcessingRows,

            TreasuryXlsMapper treasuryXlsMapper,
            TreasuryService treasuryService,
            TreasuryXlsErrorsArchiverService treasuryXlsErrorsArchiverService,
            OrganizationService organizationService, FileExceptionHandlerService fileExceptionHandlerService) {
        super(maxConcurrentProcessingRows, treasuryXlsErrorsArchiverService, organizationService, fileExceptionHandlerService);
        this.treasuryXlsMapper = treasuryXlsMapper;
        this.treasuryService = treasuryService;
    }

    @Override
    protected String getSequencingId(TreasuryXlsIngestionFlowFileDTO row) {
        return TreasuryUtils.getIdentificativo(row.getExtendedRemittanceDescription(), TreasuryUtils.IUF);
    }

    public TreasuryIufIngestionFlowFileResult processTreasuryXls(
            Iterator<TreasuryXlsIngestionFlowFileDTO> iterator,
            IngestionFlowFile ingestionFlowFile,
            Path workingDirectory) {
        List<TreasuryXlsErrorDTO> errorList = new ArrayList<>();
        TreasuryIufIngestionFlowFileResult ingestionFlowFileResult = new TreasuryIufIngestionFlowFileResult();
        ingestionFlowFileResult.setIuf2TreasuryIdMap(new HashMap<>());

        process(iterator, new ArrayList<>(), ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected List<TreasuryXlsErrorDTO> consumeRow(long lineNumber,
                                                   TreasuryXlsIngestionFlowFileDTO row,
                                                   TreasuryIufIngestionFlowFileResult ingestionFlowFileResult,
                                                   IngestionFlowFile ingestionFlowFile) {
        String ipa = getIpaCodeByOrganizationId(ingestionFlowFile.getOrganizationId());
        String rowIuf = TreasuryUtils.getIdentificativo(row.getExtendedRemittanceDescription(), TreasuryUtils.IUF);
        LocalDate billDate = row.getBillDate();

        TreasuryIuf existingTreasury = treasuryService.getByOrganizationIdAndIuf(ingestionFlowFile.getOrganizationId(), rowIuf);

        if (existingTreasury != null) {
            boolean treasuryNotMatch = !existingTreasury.getBillCode().equals(TreasuryUtils.generateBillCode(rowIuf)) || !existingTreasury.getBillYear().equals(String.valueOf(billDate.getYear()));
            if (treasuryNotMatch) {
                log.error("IUF {} already associated to another treasury for organization with IPA code {}", rowIuf, ipa);
                TreasuryXlsErrorDTO error = buildErrorDto(
                        ingestionFlowFile, lineNumber, row,
                        FileErrorCode.IUF_ALREADY_ASSOCIATED.name(),
                        FileErrorCode.IUF_ALREADY_ASSOCIATED.format(rowIuf, ipa));
                return List.of(error);
            }
        }

        Treasury treasury = treasuryService.insert(
                treasuryXlsMapper.map(row, ingestionFlowFile));

        ingestionFlowFileResult.getIuf2TreasuryIdMap().put(treasury.getIuf(), treasury.getTreasuryId());
        return Collections.emptyList();
    }

    @Override
    protected TreasuryXlsErrorDTO buildErrorDto(IngestionFlowFile ingestionFlowFile, long lineNumber, TreasuryXlsIngestionFlowFileDTO row, String errorCode, String message) {
        TreasuryXlsErrorDTO errorDTO = TreasuryXlsErrorDTO.builder()
                .fileName(ingestionFlowFile.getFileName())
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
        if (row != null) {
            errorDTO.setIuf(TreasuryUtils.getIdentificativo(row.getExtendedRemittanceDescription(), TreasuryUtils.IUF));
        }
        return errorDTO;
    }

}
