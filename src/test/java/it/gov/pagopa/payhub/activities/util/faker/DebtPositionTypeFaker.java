package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionTypeDTO;

public class DebtPositionTypeFaker {

    public static DebtPositionTypeDTO buildDebtPositionType(){
        return DebtPositionTypeDTO.builder()
                .debtTypePositionId(1L)
                .brokerId(2L)
                .code("code")
                .taxonomyCode("taxonomyCode")
                .macroArea("macroArea")
                .serviceType("serviceType")
                .collectingReason("collectingReason")
                .flagPrintDueDate(true)
                .flagAnonymousFiscalCode(false)
                .flagMandatoryDueDate(true)
                .description("description")
                .build();
    }
}
