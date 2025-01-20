package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.pu.classification.dto.generated.Treasury;

import java.util.Optional;


/**
 * This interface provides a method for sending notification messages to the microservice IO Notification.
 */
public interface TreasuryService {
    Optional<Treasury> getByOrganizationIdAndIuf(Long organizationId, String iuf);
    Optional<Treasury> getBySemanticKey(Long organizationId, String billCode, String billYear);
    Optional<Treasury> insert(Treasury treasury);
    Long deleteByOrganizationIdAndBillCodeAndBillYear(Long organizationId, String billCode, String billYear);

}
