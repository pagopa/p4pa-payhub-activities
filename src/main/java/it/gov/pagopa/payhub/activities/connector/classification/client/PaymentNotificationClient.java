package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class PaymentNotificationClient {

    private final ClassificationApisHolder classificationApisHolder;

    public PaymentNotificationClient(ClassificationApisHolder classificationApisHolder) {
        this.classificationApisHolder = classificationApisHolder;
    }

    public PaymentNotificationDTO createPaymentNotification(PaymentNotificationDTO dtos, String accessToken) {
        return classificationApisHolder.getPaymentNotificationApi(accessToken)
                .createPaymentNotification(dtos);
    }





}
