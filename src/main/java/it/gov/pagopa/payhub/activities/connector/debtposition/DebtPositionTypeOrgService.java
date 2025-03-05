package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;

/**
 * This interface provides methods that manage debt positions type org within the related microservice.
 */
public interface DebtPositionTypeOrgService {

    /**
     * Get the serviceId, subject e markdown from debt position type org to send IO Notification.
     *
     * @param debtPositionTypeOrgId the identifier of the debt position.
     * @param context the operation type
     * @return the updated {@link IONotificationDTO} object.
     */
    IONotificationDTO getIONotificationDetails(Long debtPositionTypeOrgId, String context);

}
