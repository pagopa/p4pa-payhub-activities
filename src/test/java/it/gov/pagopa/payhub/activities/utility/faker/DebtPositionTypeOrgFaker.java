package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionTypeOrgDTO;

import static it.gov.pagopa.payhub.activities.utility.faker.DebtPositionTypeFaker.buildDebtPositionType;
import static it.gov.pagopa.payhub.activities.utility.faker.OrganizationFaker.buildOrganizationDTO;

public class DebtPositionTypeOrgFaker {

    public static DebtPositionTypeOrgDTO buildDebtPositionTypeOrgDTO() {
        return DebtPositionTypeOrgDTO.builder()
                .debtPositionTypeOrgId(1L)
                .org(buildOrganizationDTO())
                .debtPositionType(buildDebtPositionType())
                .balance("balance")
                .code("code")
                .description("description")
                .postalIban("postalIban")
                .iban("iban")
                .postalAccountCode("1234567890")
                .xsdDefinitionRef("xsdDefinitionRef")
                .amount(100L)
                .externalPaymentUrl("externalPaymentUrl")
                .balanceDefaultDesc("balanceDefaultDesc")
                .flagAnonymousFiscalCode(false)
                .flagMandatoryDueDate(false)
                .holderPostalCC("holderPostalCC")
                .orgSector("orgSector")
                .flagNotifyIO(true)
                .flagNotifyOutcomePush(false)
                .maxAttemptForwardingOutcome(3)
                .orgSilId(2L)
                .flagActive(true)
                .taxonomyCode("taxonomyCode")
                .amountActualizationUrl("amountActualizationUrl")
                .amountActualizationUser("amountActualizationUser")
                .amountActualizationPwd("amountActualizationPwd")
                .urlNotifyActualizationPnd("urlNotifyActualizationPnd")
                .flagDisablePrintNotice(true)
                .build();
    }
}
