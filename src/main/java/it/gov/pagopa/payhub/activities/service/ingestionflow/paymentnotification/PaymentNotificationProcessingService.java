package it.gov.pagopa.payhub.activities.service.ingestionflow.paymentnotification;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentNotificationService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.paymentnotification.PaymentNotificationMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
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

@Service
@Lazy
@Slf4j
public class PaymentNotificationProcessingService extends IngestionFlowProcessingService<PaymentNotificationIngestionFlowFileDTO, PaymentNotificationIngestionFlowFileResult, PaymentNotificationErrorDTO> {

    private final PaymentNotificationMapper paymentNotificationMapper;
    private final PaymentNotificationService paymentNotificationService;

    public PaymentNotificationProcessingService(
            @Value("${ingestion-flow-files.payment-notifications.max-concurrent-processing-rows}") int maxConcurrentProcessingRows,

            PaymentNotificationMapper paymentNotificationMapper,
            PaymentNotificationErrorsArchiverService paymentNotificationErrorsArchiverService,
            PaymentNotificationService paymentNotificationService,
            OrganizationService organizationService, FileExceptionHandlerService fileExceptionHandlerService) {
        super(maxConcurrentProcessingRows, paymentNotificationErrorsArchiverService, organizationService, fileExceptionHandlerService);
        this.paymentNotificationMapper = paymentNotificationMapper;
        this.paymentNotificationService = paymentNotificationService;
    }

    public PaymentNotificationIngestionFlowFileResult processPaymentNotification(
            Iterator<PaymentNotificationIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        List<PaymentNotificationErrorDTO> errorList = new ArrayList<>();
        PaymentNotificationIngestionFlowFileResult ingestionFlowFileResult = new PaymentNotificationIngestionFlowFileResult();
        ingestionFlowFileResult.setIudList(new ArrayList<>());

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected String getSequencingId(PaymentNotificationIngestionFlowFileDTO row) {
        return row.getIud();
    }

    @Override
    protected List<PaymentNotificationErrorDTO> consumeRow(long lineNumber,
                                                           PaymentNotificationIngestionFlowFileDTO paymentNotificationDTO,
                                                           PaymentNotificationIngestionFlowFileResult ingestionFlowFileResult,
                                                           IngestionFlowFile ingestionFlowFile) {
        PaymentNotificationDTO paymentNotificationCreated = paymentNotificationService.createPaymentNotification(
                paymentNotificationMapper.map(paymentNotificationDTO, ingestionFlowFile));

        ingestionFlowFileResult.getIudList().add(paymentNotificationCreated.getIud());
        return Collections.emptyList();
    }

    @Override
    protected PaymentNotificationErrorDTO buildErrorDto(IngestionFlowFile ingestionFlowFile, long lineNumber, PaymentNotificationIngestionFlowFileDTO row, String errorCode, String message) {
        PaymentNotificationErrorDTO errorDTO = PaymentNotificationErrorDTO.builder()
                .fileName(ingestionFlowFile.getFileName())
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
        if (row != null) {
            errorDTO.setIuv(row.getIuv());
            errorDTO.setIud(row.getIud());
        }
        return errorDTO;
    }
}

