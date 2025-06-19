package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrgRequestBody;
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

    /**
     * Get the debt position type org by installment id.
     *
     * @param installmentId the identifier of the installment.
     * @return the {@link DebtPositionTypeOrg} object.
     */
    DebtPositionTypeOrg getDebtPositionTypeOrgByInstallmentId(Long installmentId);

    /**
     * Get the debt position type org by organization id and code.
     *
     * @param organizationId the identifier of the organization.
     * @param code the code of the debt position type.
     * @return the {@link DebtPositionTypeOrg} object.
     */
    DebtPositionTypeOrg getDebtPositionTypeOrgByOrganizationIdAndCode(Long organizationId, String code);

    /**
     * Creates a new debt position type org.
     *
     * @param debtPositionTypeOrgRequestBody the request body for creation.
     * @return the newly created {@link DebtPositionTypeOrg} object.
     */
    DebtPositionTypeOrg createDebtPositionTypeOrg(DebtPositionTypeOrgRequestBody debtPositionTypeOrgRequestBody);
}
