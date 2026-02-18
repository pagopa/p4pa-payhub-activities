package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.SpontaneousForm;

public interface SpontaneousFormService {
    SpontaneousForm findByOrganizationIdAndCode(Long organizationId, String code);
    SpontaneousForm createSpontaneousForm(SpontaneousForm spontaneousForm);
}

