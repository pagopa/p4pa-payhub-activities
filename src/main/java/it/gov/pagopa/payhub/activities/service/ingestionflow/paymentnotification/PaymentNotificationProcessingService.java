package it.gov.pagopa.payhub.activities.service.ingestionflow.paymentnotification;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentNotificationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.paymentnotification.PaymentNotificationMapper;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
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
public class PaymentNotificationProcessingService extends IngestionFlowProcessingService<PaymentNotificationIngestionFlowFileDTO, PaymentNotificationIngestionFlowFileResult, PaymentNotificationErrorDTO> {

    private final PaymentNotificationMapper paymentNotificationMapper;
    private final PaymentNotificationService paymentNotificationService;

    public PaymentNotificationProcessingService(
            PaymentNotificationMapper paymentNotificationMapper,
            PaymentNotificationErrorsArchiverService paymentNotificationErrorsArchiverService,
            PaymentNotificationService paymentNotificationService) {
        super(paymentNotificationErrorsArchiverService);
        this.paymentNotificationMapper = paymentNotificationMapper;
        this.paymentNotificationService = paymentNotificationService;
    }


    public PaymentNotificationIngestionFlowFileResult processPaymentNotification(
            Iterator<PaymentNotificationIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        List<PaymentNotificationErrorDTO> errorList = new ArrayList<>();
        PaymentNotificationIngestionFlowFileResult ingestionFlowFileResult = new PaymentNotificationIngestionFlowFileResult();
        ingestionFlowFileResult.setOrganizationId(ingestionFlowFile.getOrganizationId());
        ingestionFlowFileResult.setIudList(new ArrayList<>());

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected boolean consumeRow(long lineNumber, PaymentNotificationIngestionFlowFileDTO paymentNotificationDTO, PaymentNotificationIngestionFlowFileResult ingestionFlowFileResult, List<PaymentNotificationErrorDTO> errorList, IngestionFlowFile ingestionFlowFile) {
        try {
            PaymentNotificationDTO paymentNotificationCreated = paymentNotificationService.createPaymentNotification(
                    paymentNotificationMapper.map(paymentNotificationDTO, ingestionFlowFile));

            ingestionFlowFileResult.getIudList().add(paymentNotificationCreated.getIud());
            return true;
        } catch (Exception e) {
            log.error("Error processing payment notice with iud {} and iuv {}: {}",
                    paymentNotificationDTO.getIud(), paymentNotificationDTO.getIuv(),
                    e.getMessage());
            PaymentNotificationErrorDTO error = new PaymentNotificationErrorDTO(
                    ingestionFlowFile.getFileName(), paymentNotificationDTO.getIuv(),
                    paymentNotificationDTO.getIud(), lineNumber, "PROCESS_EXCEPTION", e.getMessage());
            errorList.add(error);
            log.info("Current error list size after handleProcessingError: {}", errorList.size());
            return false;
        }
    }

    @Override
    protected PaymentNotificationErrorDTO buildErrorDto(String fileName, long lineNumber, String errorCode, String message) {
        return PaymentNotificationErrorDTO.builder()
                .fileName(fileName)
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
    }
}

