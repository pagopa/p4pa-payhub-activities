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

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Transfer validTransfer = new Transfer().installmentId(1L).amountCents(100L);
        PaymentNotificationNoPII validNotif = new PaymentNotificationNoPII().amountPaidCents(100L);
        PaymentNotificationNoPII diffNotif = new PaymentNotificationNoPII().amountPaidCents(110L);
        InstallmentNoPII matchingInstallment = new InstallmentNoPII().amountCents(100L);
        InstallmentNoPII diffInstallment = new InstallmentNoPII().amountCents(90L);

        return Stream.of(
            Arguments.of(null, null, null, null),
            Arguments.of(validTransfer, null, null, null),
            Arguments.of(null, validNotif, null, ClassificationsEnum.IUD_NO_RT),
            Arguments.of(null, null, matchingInstallment, null),
            Arguments.of(validTransfer, validNotif, null, null),
            Arguments.of(validTransfer, null, matchingInstallment, null),
            Arguments.of(null, validNotif, matchingInstallment, ClassificationsEnum.IUD_NO_RT),
            Arguments.of(validTransfer, validNotif, matchingInstallment, null),
            Arguments.of(validTransfer, diffNotif, diffInstallment, ClassificationsEnum.IUD_NO_RT));
    }
}
