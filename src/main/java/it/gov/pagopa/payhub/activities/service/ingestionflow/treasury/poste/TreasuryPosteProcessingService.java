package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.poste;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste.TreasuryPosteErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste.TreasuryPosteIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste.TreasuryPosteIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.poste.TreasuryPosteMapper;
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

import static it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.poste.TreasuryPosteMapper.POSTE_DATE_FORMAT;

@Service
@Lazy
@Slf4j
public class TreasuryPosteProcessingService extends IngestionFlowProcessingService<TreasuryPosteIngestionFlowFileDTO, TreasuryIufIngestionFlowFileResult, TreasuryPosteErrorDTO> {

    private final TreasuryPosteMapper treasuryPosteMapper;
    private final TreasuryService treasuryService;
    private final FileExceptionHandlerService fileExceptionHandlerService;

    public TreasuryPosteProcessingService(
            @Value("${ingestion-flow-files.treasuries.max-concurrent-processing-rows}") int maxConcurrentProcessingRows,

            TreasuryPosteMapper treasuryPosteMapper,
            TreasuryService treasuryService,
            TreasuryPosteErrorsArchiverService treasuryPosteErrorsArchiverService,
            OrganizationService organizationService,
            FileExceptionHandlerService fileExceptionHandlerService) {
        super(maxConcurrentProcessingRows, treasuryPosteErrorsArchiverService, organizationService, fileExceptionHandlerService);
        this.treasuryPosteMapper = treasuryPosteMapper;
        this.treasuryService = treasuryService;
        this.fileExceptionHandlerService = fileExceptionHandlerService;
    }

    public TreasuryIufIngestionFlowFileResult processTreasuryPoste(
            Iterator<TreasuryPosteIngestionFlowFileDTO> iterator,
            String iban,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        List<TreasuryPosteErrorDTO> errorList = new ArrayList<>();
        TreasuryPosteIngestionFlowFileResult ingestionFlowFileResult = new TreasuryPosteIngestionFlowFileResult();
        ingestionFlowFileResult.setOrganizationId(ingestionFlowFile.getOrganizationId());
        ingestionFlowFileResult.setIban(iban);
        ingestionFlowFileResult.setIuf2TreasuryIdMap(new HashMap<>());
        ingestionFlowFileResult.setFileVersion(ingestionFlowFile.getFileVersion());
        String ipa = getIpaCodeByOrganizationId(ingestionFlowFile.getOrganizationId());
        ingestionFlowFileResult.setIpaCode(ipa);

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected String getSequencingId(TreasuryPosteIngestionFlowFileDTO row) {
        return TreasuryUtils.getIdentificativo(row.getRemittanceDescription(), TreasuryUtils.IUF);
    }

    @Override
    protected List<TreasuryPosteErrorDTO> consumeRow(long lineNumber,
                                                     TreasuryPosteIngestionFlowFileDTO row,
                                                     TreasuryIufIngestionFlowFileResult ingestionFlowFileResult,
                                                     IngestionFlowFile ingestionFlowFile) {
        String iuf = TreasuryUtils.getIdentificativo(row.getRemittanceDescription(), TreasuryUtils.IUF);

        LocalDate billDate = LocalDate.parse(row.getBillDate(), POSTE_DATE_FORMAT);
        String billCode = TreasuryUtils.generateBillCode(iuf);
        String billYear = String.valueOf(billDate.getYear());

        TreasuryPosteIngestionFlowFileResult treasuryPosteIngestionFlowFileResult = (TreasuryPosteIngestionFlowFileResult) ingestionFlowFileResult;
        String iban = treasuryPosteIngestionFlowFileResult.getIban();
        String ipa = treasuryPosteIngestionFlowFileResult.getIpaCode();

        try {
            TreasuryIuf existingTreasury = treasuryService.getByOrganizationIdAndIuf(treasuryPosteIngestionFlowFileResult.getOrganizationId(), iuf);

            if (existingTreasury != null) {
                boolean treasuryMatch = !Objects.equals(existingTreasury.getBillCode(),billCode) || !Objects.equals(existingTreasury.getBillYear(), billYear);
                if (treasuryMatch) {
                    log.error("IUF {} already associated to another treasury for organization with IPA code {}", iuf, ipa);
                    TreasuryPosteErrorDTO error = new TreasuryPosteErrorDTO(
                            ingestionFlowFile.getFileName(), iuf,
                            lineNumber,
                            FileErrorCode.IUF_ALREADY_ASSOCIATED.name(),
                            FileErrorCode.IUF_ALREADY_ASSOCIATED.format(iuf, ipa));
                    return List.of(error);
                }
            }

            Treasury treasury = treasuryService.insert(
                    treasuryPosteMapper.map(row, iban, iuf, billCode, billDate, ingestionFlowFile));

            treasuryPosteIngestionFlowFileResult.getIuf2TreasuryIdMap().put(treasury.getIuf(), treasury.getTreasuryId());
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error processing treasury poste with iuf {}: {}",
                    iuf,
                    e.getMessage());
            FileExceptionHandlerService.ErrorDetails errorDetails = fileExceptionHandlerService.mapExceptionToErrorCodeAndMessage(e.getMessage());
            TreasuryPosteErrorDTO error = new TreasuryPosteErrorDTO(
                    ingestionFlowFile.getFileName(), iuf,
                    lineNumber, errorDetails.getErrorCode(), errorDetails.getErrorMessage());
            return List.of(error);
        }
    }

    @Override
    protected TreasuryPosteErrorDTO buildErrorDto(String fileName, long lineNumber, String errorCode, String message) {
        return TreasuryPosteErrorDTO.builder()
                .fileName(fileName)
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
    }
}