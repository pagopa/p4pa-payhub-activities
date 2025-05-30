package it.gov.pagopa.payhub.activities.service.ingestionflow.receipt;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt.ReceiptMapper;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Lazy
@Slf4j
public class ReceiptProcessingService extends IngestionFlowProcessingService<ReceiptIngestionFlowFileDTO, ReceiptIngestionFlowFileResult, ReceiptErrorDTO> {

    private final ReceiptService receiptService;
    private final ReceiptMapper receiptMapper;

    public ReceiptProcessingService(ReceiptMapper receiptMapper,
                                    ReceiptErrorsArchiverService receiptErrorsArchiverService,
                                    ReceiptService receiptService) {
        super(receiptErrorsArchiverService);
        this.receiptService = receiptService;
        this.receiptMapper = receiptMapper;
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
            ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = receiptMapper.map(ingestionFlowFile, receipt);
            receiptService.createReceipt(receiptWithAdditionalNodeDataDTO);
            return true;
        } catch (Exception e) {
            log.error("Error processing receipt with codIuv {}: {}", receipt.getCodIuv(), e.getMessage());
            ReceiptErrorDTO error = ReceiptErrorDTO.builder()
                    .fileName(ingestionFlowFile.getFileName())
                    .codIuv(receipt.getCodIuv())
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

}
