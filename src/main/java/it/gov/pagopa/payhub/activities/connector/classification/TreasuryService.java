package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;


/**
 * This interface provides a method for sending notification messages to the microservice IO Notification.
 */
public interface TreasuryService {
    TreasuryIuf getByOrganizationIdAndIuf(Long organizationId, String iuf);
    Treasury insert(Treasury treasury);
    Long deleteByOrganizationIdAndBillCodeAndBillYearAndOrgBtCodeAndOrgIstatCode(Long organizationId, String billCode, String billYear, String orgBtCode, String orgIstatCode);
    Treasury getById(String treasuryId);
}
