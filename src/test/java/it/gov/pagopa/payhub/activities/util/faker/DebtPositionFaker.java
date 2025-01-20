package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.TestUtils.OFFSETDATETIME;
import static it.gov.pagopa.payhub.activities.util.faker.PaymentOptionFaker.buildPaymentOptionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.PaymentOptionFaker.buildPaymentsPaymentOptionDTO;

public class DebtPositionFaker {

    public static DebtPositionDTO buildDebtPositionDTO(){
        return DebtPositionDTO.builder()
                .debtPositionId(1L)
                .iupdOrg("codeIud")
                .status(DebtPositionDTO.StatusEnum.valueOf("statusCode"))
                .description("description")
                .status(DebtPositionDTO.StatusEnum.valueOf("UNPAID"))
                .ingestionFlowFileId(0L)
                .ingestionFlowFileLineNumber(1L)
                .organizationId(2L)
                .debtPositionTypeOrgId(3L)
                .notificationDate(OFFSETDATETIME)
                .validityDate(OFFSETDATETIME)
                .flagIuvVolatile(true)
                .creationDate(OFFSETDATETIME)
                .updateDate(OFFSETDATETIME)
                .paymentOptions(List.of(buildPaymentOptionDTO()))
                .build();
    }

    public static it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO buildPaymentsDebtPositionDTO(){
        return it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO.builder()
                .debtPositionId(1L)
                .iupdOrg("codeIud")
                .description("description")
                .status("UNPAID")
                .ingestionFlowFileId(0L)
                .ingestionFlowFileLineNumber(1L)
                .organizationId(2L)
                .debtPositionTypeOrgId(3L)
                .notificationDate(OFFSETDATETIME)
                .validityDate(OFFSETDATETIME)
                .flagIuvVolatile(true)
                .creationDate(OFFSETDATETIME)
                .updateDate(OFFSETDATETIME)
                .paymentOptions(List.of(buildPaymentsPaymentOptionDTO()))
                .build();
    }
}
