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
class RtNoIudClassifierTest {
	private RtNoIudClassifier classifier = new RtNoIudClassifier();

	@ParameterizedTest
	@MethodSource("provideClassifierScenarios")
	void classificationCoversAllCombinations(Transfer transfer,
	                                         PaymentNotificationNoPII notification,
	                                         ClassificationsEnum expected) {
		ClassificationsEnum result = classifier.classify(transfer, notification, null, null);
		assertEquals(expected, result);
	}

	private static Stream<Arguments> provideClassifierScenarios() {
		return Stream.of(
			Arguments.of(null, null, null),
			Arguments.of(new Transfer(), new PaymentNotificationNoPII(), null),
			Arguments.of(null, new PaymentNotificationNoPII(), null),
			Arguments.of(new Transfer(), null, ClassificationsEnum.RT_NO_IUD)
		);
	}
}