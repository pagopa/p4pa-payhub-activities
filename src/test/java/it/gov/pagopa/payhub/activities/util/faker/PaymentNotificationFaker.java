package it.gov.pagopa.payhub.activities.util.faker;


import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
import java.time.LocalDate;

public class PaymentNotificationFaker {

    public static PaymentNotificationNoPII buildPaymentNotificationNoPII() {
        return TestUtils.getPodamFactory().manufacturePojo(PaymentNotificationNoPII.class)
                .paymentNotificationId("paymentNotificationId")
                .ingestionFlowFileId(1L)
                .organizationId(1L)
                .debtPositionTypeOrgCode("DPTOC")
                .iud("IUD")
                .iuv("IUV")
                .amountPaidCents(100L)
                .paymentExecutionDate(LocalDate.now());
    }
}
