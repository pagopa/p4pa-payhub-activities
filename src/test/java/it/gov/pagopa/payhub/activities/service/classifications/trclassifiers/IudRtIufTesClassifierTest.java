package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
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
	                                         InstallmentNoPII mockInstallment,
	                                         ClassificationsEnum expected) {

		ClassificationsEnum result = classifier.classify(transfer, mockInstallment, notification, reporting, treasury);
		assertEquals(expected, result);
	}

	private static Stream<Arguments> provideClassifierScenarios() {
		Transfer validTransfer = new Transfer().installmentId(1L).amountCents(100L);
		PaymentNotificationNoPII validNotif = new PaymentNotificationNoPII().amountPaidCents(100L);
		PaymentsReporting validReporting = new PaymentsReporting().amountPaidCents(100L);
		TreasuryIuf validTreasury = buildTreasuryIuf();
		validTreasury.setBillAmountCents(100L);
		InstallmentNoPII validInstallment = new InstallmentNoPII().amountCents(100L);
		InstallmentNoPII mismatchInstallment = new InstallmentNoPII().amountCents(99L);

		return Stream.of(
			Arguments.of(null, null, null, null, null, null),
			Arguments.of(validTransfer, null, validReporting, validTreasury, validInstallment, null),
			Arguments.of(validTransfer, validNotif, null, validTreasury, validInstallment, null),
			Arguments.of(validTransfer, validNotif, validReporting, null, validInstallment, null),
			Arguments.of(validTransfer, validNotif, validReporting, validTreasury, null, null),
			Arguments.of(validTransfer, validNotif, validReporting, validTreasury, mismatchInstallment, null),
			Arguments.of(validTransfer, validNotif, validReporting, validTreasury, validInstallment, ClassificationsEnum.IUD_RT_IUF_TES)
		);
	}
}
