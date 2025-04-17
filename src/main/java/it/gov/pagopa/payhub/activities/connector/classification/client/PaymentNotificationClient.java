package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

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


    public PaymentNotificationNoPII getByOrgIdAndIud(Long organizationId, String iud, String accessToken) {
        try {
            return classificationApisHolder.getPaymentNotificationNoPiiSearchControllerApi(accessToken)
                .crudPaymentNotificationGetByOrganizationIdAndIud(organizationId, iud);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("PaymentNotification not found: organizationId: {}, iud: {}", organizationId, iud);
            return null;
        }


    }




}
