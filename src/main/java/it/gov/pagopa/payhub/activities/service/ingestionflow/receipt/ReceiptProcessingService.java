package it.gov.pagopa.payhub.activities.service.ingestionflow.receipt;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.payhub.activities.service.receipt.RtFileHandlerService;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static it.gov.pagopa.payhub.activities.util.Utilities.bigDecimalEuroToLongCentsAmount;

@Service
@Lazy
@Slf4j
public class ReceiptProcessingService extends IngestionFlowProcessingService<ReceiptIngestionFlowFileDTO, ReceiptIngestionFlowFileResult, ReceiptErrorDTO> {

    private final RtFileHandlerService rtFileHandlerService;
    private final DebtPositionService debtPositionService;

    public ReceiptProcessingService(ReceiptErrorsArchiverService receiptErrorsArchiverService,
                                    RtFileHandlerService rtFileHandlerService, DebtPositionService debtPositionService) {
        super(receiptErrorsArchiverService);
        this.rtFileHandlerService = rtFileHandlerService;
        this.debtPositionService = debtPositionService;
    }

    /**
     * Processes a stream of InstallmentIngestionFlowFileDTO and synchronizes each installment.
     *
     * @param iterator          Stream of installment ingestion flow file DTOs to be processed.
     * @param readerExceptions  A list which will collect the exceptions thrown during iterator processing
     * @param ingestionFlowFile Metadata of the ingestion file containing details about the ingestion process.
     * @param workingDirectory  The directory where error files will be written if processing fails.
     * @return An {@link ReceiptIngestionFlowFileResult} containing details about the processed rows, errors, and archived files.
     */
    public ReceiptIngestionFlowFileResult processReceipts(Iterator<ReceiptIngestionFlowFileDTO> iterator,
                                                          List<CsvException> readerExceptions,
                                                          IngestionFlowFile ingestionFlowFile,
                                                          Path workingDirectory) {
        List<ReceiptErrorDTO> errorList = new ArrayList<>();
        ReceiptIngestionFlowFileResult result = new ReceiptIngestionFlowFileResult();
        process(iterator, readerExceptions, result, ingestionFlowFile, errorList, workingDirectory);
        result.setFileVersion(ingestionFlowFile.getFileVersion());
        result.setOrganizationId(ingestionFlowFile.getOrganizationId());
        return result;
    }

    @Override
    protected boolean consumeRow(long lineNumber,
                                 ReceiptIngestionFlowFileDTO receipt,
                                 ReceiptIngestionFlowFileResult ingestionFlowFileResult,
                                 List<ReceiptErrorDTO> errorList,
                                 IngestionFlowFile ingestionFlowFile) {
        try {
            rtFileHandlerService.store(ingestionFlowFile.getOrganizationId(), receipt.getRt(), ingestionFlowFile.getFileName());
            return true;
        } catch (Exception e) {
            log.error("Error processing receipt {}: {}", receipt.getCodIud(), e.getMessage());
            ReceiptErrorDTO error = ReceiptErrorDTO.builder()
                    .fileName(ingestionFlowFile.getFileName())
                    .rowNumber(lineNumber)
                    .errorCode("PROCESS_EXCEPTION")
                    .errorMessage(e.getMessage())
                    .build();

            errorList.add(error);
            log.info("Current error list size after handleProcessingError: {}", errorList.size());
            return false;
        }
    }

    @Override
    protected ReceiptErrorDTO buildErrorDto(String fileName, long lineNumber, String errorCode, String message) {
        return ReceiptErrorDTO.builder()
                .fileName(fileName)
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
    }

    private ReceiptWithAdditionalNodeDataDTO buildReceiptDataDTO(ReceiptIngestionFlowFileDTO receipt, IngestionFlowFile ingestionFlowFile) {
        PersonDTO payer = PersonDTO.builder()
                .entityType(receipt.getSoggVersTipoIdentificativoUnivoco())
                .fiscalCode(receipt.getSoggVersCodiceIdentificativoUnivoco())
                .fullName(receipt.getAnagraficaVersante())
                .address(receipt.getIndirizzoVersante())
                .civic(receipt.getCivicoVersante())
                .postalCode(receipt.getCapVersante())
                .location(receipt.getLocalitaVersante())
                .province(receipt.getProvinciaVersante())
                .nation(receipt.getNazioneVersante())
                .email(receipt.getEmailVersante())
                .build();
        PersonDTO debtor = PersonDTO.builder()
                .entityType(receipt.getSoggPagTipoIdentificativoUnivoco())
                .fiscalCode(receipt.getSoggPagCodiceIdentificativoUnivoco())
                .fullName(receipt.getAnagraficaPagatore())
                .address(receipt.getIndirizzoPagatore())
                .civic(receipt.getCivicoPagatore())
                .postalCode(receipt.getCapPagatore())
                .location(receipt.getLocalitaPagatore())
                .province(receipt.getProvinciaPagatore())
                .nation(receipt.getNazionePagatore())
                .email(receipt.getEmailPagatore())
                .build();

        return ReceiptWithAdditionalNodeDataDTO.builder()
                .ingestionFlowFileId(ingestionFlowFile.getIngestionFlowFileId())
                .iud(receipt.getCodIud())
                .noticeNumber(receipt.getCodIuv())
                .orgFiscalCode(receipt.getIdentificativoDominio())
                .paymentReceiptId(receipt.getIdentificativoMessaggioRicevuta())
                .paymentDateTime(OffsetDateTime.from(receipt.getDataOraMessaggioRicevuta()))
                .idPsp(receipt.getCodiceIdentificativoUnivoco())
                .pspCompanyName(receipt.getDenominazioneAttestante())
                .companyName(receipt.getDenominazioneBeneficiario())
                .payer(payer)
                .debtor(debtor)
                .outcome(receipt.getCodiceEsitoPagamento())
                .paymentAmountCents(receipt.getImportoTotalePagato())
                .creditorReferenceId(receipt.getIdentificativoUnivocoVersamento())
                .description(receipt.getCausaleVersamento())
                .paymentNote(receipt.getDatiSpecificiRiscossione())
                .debtPositionTypeOrgCode(receipt.getTipoDovuto())
                .feeCents(bigDecimalEuroToLongCentsAmount(receipt.getNumRtDatiPagDatiSingPagCommissioniApplicatePsp()))
                .balance(receipt.getBilancio())
                .transfers(new ArrayList<>())
                .build();
    }

}
