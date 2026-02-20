package it.gov.pagopa.payhub.activities.service.ingestionflow.receipt;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt.ReceiptMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static it.gov.pagopa.payhub.activities.service.ingestionflow.receipt.ReceiptIngestionFlowFileRequiredFieldsValidatorService.setDefaultValues;

@Service
@Lazy
@Slf4j
public class ReceiptProcessingService extends IngestionFlowProcessingService<ReceiptIngestionFlowFileDTO, ReceiptIngestionFlowFileResult, ReceiptErrorDTO> {

    private final ReceiptService receiptService;
    private final ReceiptMapper receiptMapper;
    private final ReceiptIngestionFlowFileRequiredFieldsValidatorService requiredFieldsValidatorService;

    public ReceiptProcessingService(
            @Value("${ingestion-flow-files.receipts.max-concurrent-processing-rows}") int maxConcurrentProcessingRows,

            ReceiptMapper receiptMapper,
            ReceiptErrorsArchiverService receiptErrorsArchiverService,
            ReceiptService receiptService,
            OrganizationService organizationService, FileExceptionHandlerService fileExceptionHandlerService,
            ReceiptIngestionFlowFileRequiredFieldsValidatorService requiredFieldsValidatorService) {
        super(maxConcurrentProcessingRows, receiptErrorsArchiverService, organizationService, fileExceptionHandlerService);
        this.receiptService = receiptService;
        this.receiptMapper = receiptMapper;
        this.requiredFieldsValidatorService = requiredFieldsValidatorService;
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
                                                          Path workingDirectory,
                                                          ReceiptIngestionFlowFileResult result) {
        List<ReceiptErrorDTO> errorList = new ArrayList<>();
        process(iterator, readerExceptions, result, ingestionFlowFile, errorList, workingDirectory);
        return result;
    }

    @Override
    protected String getSequencingId(ReceiptIngestionFlowFileDTO row) {
        return row.getIuv();
    }

    @Override
    protected List<ReceiptErrorDTO> consumeRow(long lineNumber,
                                               ReceiptIngestionFlowFileDTO receipt,
                                               ReceiptIngestionFlowFileResult ingestionFlowFileResult,
                                               IngestionFlowFile ingestionFlowFile) {
        requiredFieldsValidatorService.validateIngestionFile(ingestionFlowFile, receipt);
        setDefaultValues(receipt);
        ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = receiptMapper.map(ingestionFlowFile, receipt);
        receiptService.createReceipt(receiptWithAdditionalNodeDataDTO);
        return Collections.emptyList();
    }

    @Override
    protected ReceiptErrorDTO buildErrorDto(IngestionFlowFile ingestionFlowFile, long lineNumber, ReceiptIngestionFlowFileDTO row, String errorCode, String message) {
        return ReceiptErrorDTO.builder()
                .fileName(ingestionFlowFile.getFileName())
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
    }

}
