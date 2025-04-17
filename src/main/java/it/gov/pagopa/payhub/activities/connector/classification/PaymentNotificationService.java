package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;


/**
 * Interface for managing payment notifications.
 * Provides methods to create payment notifications.
 */
public interface PaymentNotificationService {
    /**
     * Creates a new payment notification.
     *
     * @param dto the {@link PaymentNotificationDTO} object containing the details of the payment notification
     * @return the created {@link PaymentNotificationDTO} object
     */
    PaymentNotificationDTO createPaymentNotification(PaymentNotificationDTO dto);

    PaymentNotificationNoPII getByOrgIdAndIud(Long organizationId, String iud);
}
