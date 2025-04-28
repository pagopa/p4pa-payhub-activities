package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker.buildTreasuryIuf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class IudRtIufTesClassifierTest {
	@Mock
	private InstallmentService installmentServiceMock;

	private IudRtIufTesClassifier classifier;

	@BeforeEach
	void setUp() {
		classifier = new IudRtIufTesClassifier(installmentServiceMock);
	}

	@ParameterizedTest
	@MethodSource("provideClassifierScenarios")
	void classificationCoversAllCombinations(Transfer transfer,
	                                         PaymentNotificationNoPII notification,
	                                         PaymentsReporting reporting,
	                                         TreasuryIuf treasury,
	                                         InstallmentNoPII mockInstallment,
	                                         ClassificationsEnum expected) {
		if (transfer != null) {
			lenient().when(installmentServiceMock.getInstallmentById(transfer.getInstallmentId()))
				.thenReturn(Optional.ofNullable(mockInstallment));
		}
		ClassificationsEnum result = classifier.classify(transfer, notification, reporting, treasury);
		assertEquals(expected, result);
	}

	private static Stream<Arguments> provideClassifierScenarios() {
		Transfer validTransfer1 = new Transfer().installmentId(1L).amountCents(100L);
		PaymentNotificationNoPII validNotif1 = new PaymentNotificationNoPII().amountPaidCents(100L);
		PaymentsReporting validReporting1 = new PaymentsReporting().amountPaidCents(100L);
		TreasuryIuf validTreasury1 = buildTreasuryIuf();
		validTreasury1.setBillAmountCents(100L);
		InstallmentNoPII validInstallment1 = new InstallmentNoPII().amountCents(100L);
		InstallmentNoPII mismatchInstallment1 = new InstallmentNoPII().amountCents(99L);

		return Stream.of(
			Arguments.of(null, null, null, null, null, null),
			Arguments.of(validTransfer1, null, validReporting1, validTreasury1, validInstallment1, null),
			Arguments.of(validTransfer1, validNotif1, null, validTreasury1, validInstallment1, null),
			Arguments.of(validTransfer1, validNotif1, validReporting1, null, validInstallment1, null),
			Arguments.of(validTransfer1, validNotif1, validReporting1, validTreasury1, null, null),
			Arguments.of(validTransfer1, validNotif1, validReporting1, validTreasury1, mismatchInstallment1, null),
			Arguments.of(validTransfer1, validNotif1, validReporting1, validTreasury1, validInstallment1, ClassificationsEnum.IUD_RT_IUF_TES)
		);
	}
}
