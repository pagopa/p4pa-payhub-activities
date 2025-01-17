package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.classification.dto.generated.CollectionModelPaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;

import java.util.List;


/**
 * This interface provides a method for sending notification messages to the microservice IO Notification.
 */
public interface PaymentsReportingService {
    Integer saveAll(List<PaymentsReporting> dtos);
    CollectionModelPaymentsReporting getByOrganizationIdAndIuf(Long organizationId, String iuf);
    CollectionModelPaymentsReporting getBySemanticKey(TransferSemanticKeyDTO tSKDTO);









}
