package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;


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
}
