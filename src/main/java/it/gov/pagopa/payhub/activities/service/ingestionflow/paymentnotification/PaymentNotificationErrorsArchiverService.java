package it.gov.pagopa.payhub.activities.service.ingestionflow.paymentnotification;

import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Lazy
@Service
public class PaymentNotificationErrorsArchiverService extends
    ErrorArchiverService<PaymentNotificationErrorDTO, PaymentNotificationIngestionFlowFileResult> {

    protected PaymentNotificationErrorsArchiverService(@Value("${folders.shared}") String sharedFolder,
                                               @Value("${folders.process-target-sub-folders.errors}") String errorFolder,
                                               FileArchiverService fileArchiverService,
                                               CsvService csvService) {
        super(sharedFolder, errorFolder, fileArchiverService, csvService);
    }

    @Override
    protected List<String[]> getHeaders(PaymentNotificationIngestionFlowFileResult result) {
        return Collections.singletonList(
                new String[]{"File Name", "IUV", "IUD", "Row Number", "Error Code", "Error Message"});
    }
}
