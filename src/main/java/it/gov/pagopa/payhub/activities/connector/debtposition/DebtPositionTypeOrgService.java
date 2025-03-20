package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;

/**
 * This interface provides methods that manage debt positions type org within the related microservice.
 */
public interface DebtPositionTypeOrgService {

    DebtPositionTypeOrg getById(Long debtPositionTypeOrgId);

    /**
     * Get the serviceId, subject e markdown from debt position type org to send IO Notification.
     *
     * @param debtPositionTypeOrgId the identifier of the debt position.
     * @param paymentEventType the payment event type
     * @return the updated {@link IONotificationDTO} object.
     */
    IONotificationDTO getDefaultIONotificationDetails(Long debtPositionTypeOrgId, PaymentEventType paymentEventType);
}
