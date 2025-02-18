package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;

public class InstallmentSynchronizeDTOFaker {

    public static InstallmentSynchronizeDTO buildInstallmentSynchronizeDTO() {
        return TestUtils.getPodamFactory().manufacturePojo(InstallmentSynchronizeDTO.class);
    }
}
