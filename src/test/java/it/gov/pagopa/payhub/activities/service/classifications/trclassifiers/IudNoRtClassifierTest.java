package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IudNoRtClassifierTest {
    private IudNoRtClassifier classifier = new IudNoRtClassifier();

    @ParameterizedTest
    @MethodSource("provideClassifierScenarios")
    void classificationReflectsInstallmentAmount(Transfer transfer,
                                                 PaymentNotificationNoPII notification,
                                                 InstallmentNoPII mockInstallment,
                                                 ClassificationsEnum expected) {
        ClassificationsEnum result = classifier.classify(transfer, mockInstallment, notification, null, null);
        assertEquals(expected, result);
    }

    private static Stream<Arguments> provideClassifierScenarios() {
        Transfer transfer100 = new Transfer().installmentId(1L).amountCents(100L);
        PaymentNotificationNoPII notif100 = new PaymentNotificationNoPII().amountPaidCents(100L);
        InstallmentNoPII installment100 = new InstallmentNoPII().amountCents(100L);
        InstallmentNoPII installment99 = new InstallmentNoPII().amountCents(99L);

        return Stream.of(
            Arguments.of(null, null, null, null),
            Arguments.of(transfer100, null, installment100, null),
            Arguments.of(null, notif100, null, null),
            Arguments.of(transfer100, notif100, installment100, null),
            Arguments.of(transfer100, notif100, installment99, ClassificationsEnum.IUD_NO_RT)
        );
    }
}
