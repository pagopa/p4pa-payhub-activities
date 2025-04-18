package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker.buildTreasuryIuf;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IudRtIufTesClassifierTest {
	private IudRtIufTesClassifier classifier = new IudRtIufTesClassifier();

	@ParameterizedTest
	@MethodSource("provideClassifierScenarios")
	void classificationCoversAllCombinations(Transfer transfer,
	                                         PaymentNotificationNoPII notification,
	                                         PaymentsReporting reporting,
	                                         TreasuryIuf treasury,
	                                         ClassificationsEnum expected) {
		ClassificationsEnum result = classifier.classify(transfer, notification, reporting, treasury);
		assertEquals(expected, result);
	}

	private static Stream<Arguments> provideClassifierScenarios() {
		Transfer matchingTransfer = new Transfer().amountCents(100L);
		PaymentNotificationNoPII matchingNotification = new PaymentNotificationNoPII().amountPaidCents(100L);
		PaymentsReporting matchingReporting = new PaymentsReporting().amountPaidCents(100L);
		TreasuryIuf matchingTreasury = buildTreasuryIuf();
		matchingTreasury.setBillAmountCents(100L);

		return Stream.of(
			Arguments.of(null, null, null, null, null),
			Arguments.of(matchingTransfer, null, null, null, null),
			Arguments.of(null, matchingNotification, null, null, null),
			Arguments.of(null, null, matchingReporting, null, null),
			Arguments.of(null, null, null, matchingTreasury, null),
			Arguments.of(matchingTransfer, matchingNotification, matchingReporting, matchingTreasury, ClassificationsEnum.IUD_RT_IUF_TES),
			Arguments.of(new Transfer().amountCents(99L), matchingNotification, matchingReporting, matchingTreasury, null),
			Arguments.of(matchingTransfer, new PaymentNotificationNoPII().amountPaidCents(99L), matchingReporting, matchingTreasury, null),
			Arguments.of(matchingTransfer, matchingNotification, new PaymentsReporting().amountPaidCents(99L), matchingTreasury, null),
			Arguments.of(matchingTransfer, matchingNotification, matchingReporting, matchingTreasury.toBuilder().billAmountCents(99L).build(), null)
		);
	}
}