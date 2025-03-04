package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;

/**
 * This interface provides methods that manage debt positions type org within the related microservice.
 */
public interface DebtPositionTypeOrgService {

    /**
     * Finalizes the update of the debt position status from the installments.
     *
     * @param debtPositionTypeOrgId the identifier of the debt position to be updated.
     * @param context the operation type
     * @return the updated {@link IONotificationDTO} object.
     */
    IONotificationDTO getIONotificationDetails(Long debtPositionTypeOrgId, String context);

}
