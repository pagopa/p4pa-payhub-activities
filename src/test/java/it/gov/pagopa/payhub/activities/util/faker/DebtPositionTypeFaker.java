package it.gov.pagopa.payhub.activities.util.faker;


import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionType;

public class DebtPositionTypeFaker {

    public static DebtPositionType buildDebtPositionType(){
        return DebtPositionType.builder()
                .debtPositionTypeId(1L)
                .brokerId(2L)
                .code("code")
                .taxonomyCode("taxonomyCode")
                .macroArea("macroArea")
                .serviceType("serviceType")
                .collectingReason("collectingReason")
                .flagNotifyIo(true)
                .flagAnonymousFiscalCode(false)
                .flagMandatoryDueDate(true)
                .description("description")
                .build();
    }
}
