package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
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
    void classifyAllCombinations(Transfer transfer,
                                 PaymentNotificationNoPII notification,
                                 ClassificationsEnum expected) {

        ClassificationsEnum result = classifier.classify(transfer, notification, null, null);
        assertEquals(expected, result);
    }

    private static Stream<Arguments> provideClassifierScenarios() {
        Transfer transferWith100Cents = new Transfer().amountCents(100L);
        PaymentNotificationNoPII notificationWith100Cents = new PaymentNotificationNoPII().amountPaidCents(100L);
        Transfer transferWith99Cents = new Transfer().amountCents(99L);
        PaymentNotificationNoPII notificationWith99Cents = new PaymentNotificationNoPII().amountPaidCents(99L);

        return Stream.of(
            Arguments.of(null, null, null),
            Arguments.of(transferWith100Cents, null, null),
            Arguments.of(null, notificationWith100Cents, null),
            Arguments.of(transferWith100Cents, notificationWith100Cents, null),
            Arguments.of(transferWith99Cents, notificationWith100Cents, ClassificationsEnum.IUD_NO_RT),
	        Arguments.of(transferWith100Cents, notificationWith99Cents, ClassificationsEnum.IUD_NO_RT)
        );
    }
}