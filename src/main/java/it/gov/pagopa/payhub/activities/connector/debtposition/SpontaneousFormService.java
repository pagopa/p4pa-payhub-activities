package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.pu.debtpositions.dto.generated.SpontaneousForm;

public interface SpontaneousFormService {
    SpontaneousForm findByOrganizationIdAndCode(Long organizationId, String code);
    SpontaneousForm createSpontaneousForm(SpontaneousForm spontaneousForm);
    SpontaneousForm matchOrSaveSpontaneousForm(SpontaneousForm spontaneousForm);
}

