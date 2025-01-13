package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.PaymentOptionFaker.buildPaymentOptionDTO;

public class DebtPositionFaker {

    public static DebtPositionDTO buildDebtPositionDTO(){
        return DebtPositionDTO.builder()
                .debtPositionId(1L)
                .iupdOrg("codeIud")
                .iupdPagopa("gpdIupd")
                .status("statusCode")
                .ingestionFlowFileId(0L)
                .ingestionFlowFileLineNumber(1L)
                .status("UNPAID")
                .organizationId(2L)
                .debtPositionTypeOrgId(3L)
                .paymentOptions(List.of(buildPaymentOptionDTO()))
                .build();
    }
}
