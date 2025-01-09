package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionTypeOrgFaker.buildDebtPositionTypeOrgDTO;
import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFileDTO;
import static it.gov.pagopa.payhub.activities.util.faker.OrganizationFaker.buildOrganizationDTO;
import static it.gov.pagopa.payhub.activities.util.faker.PaymentOptionFaker.buildPaymentOptionDTO;

public class DebtPositionFaker {

    public static DebtPositionDTO buildDebtPositionDTO(){
        return DebtPositionDTO.builder()
                .debtPositionId(1L)
                .iupdOrg("codeIud")
                .iupdPagopa("gpdIupd")
                .status("statusCode")
                .ingestionFlowFile(buildIngestionFlowFileDTO())
                .ingestionFlowFileLineNumber(1L)
                .gpdStatus('G')
                .org(buildOrganizationDTO())
                .debtPositionTypeOrg(buildDebtPositionTypeOrgDTO())
                .paymentOptions(List.of(buildPaymentOptionDTO()))
                .build();
    }
}
