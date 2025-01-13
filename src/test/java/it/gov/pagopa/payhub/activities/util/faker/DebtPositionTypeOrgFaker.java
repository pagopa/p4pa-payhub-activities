package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;

public class DebtPositionTypeOrgFaker {

    public static DebtPositionTypeOrg buildDebtPositionTypeOrgDTO() {
        return DebtPositionTypeOrg.builder()
                .debtPositionTypeOrgId(1L)
                .organizationId(0L)
                .debtPositionTypeId(2L)
                .balance("balance")
                .code("code")
                .description("description")
                .postalIban("postalIban")
                .iban("iban")
                .postalAccountCode("1234567890")
                .xsdDefinitionRef("xsdDefinitionRef")
                .amountCents(100L)
                .externalPaymentUrl("externalPaymentUrl")
                .balance("balanceDefaultDesc")
                .flagAnonymousFiscalCode(false)
                .flagMandatoryDueDate(false)
                .holderPostalCc("holderPostalCC")
                .orgSector("orgSector")
                .flagNotifyIo(true)
                .flagNotifyOutcomePush(false)
                .flagActive(true)
                .code("taxonomyCode")
                .build();
    }
}
