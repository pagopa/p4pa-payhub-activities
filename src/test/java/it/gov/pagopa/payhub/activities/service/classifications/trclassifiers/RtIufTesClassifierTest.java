package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RtIufTesClassifierTest {
	private final PaymentsReporting paymentsReportingDTO = PaymentsReportingFaker.buildPaymentsReporting();
	private final Transfer transferDTO = TransferFaker.buildTransfer();
	private final TreasuryIuf treasuryIUF = TreasuryFaker.buildTreasuryIuf();

	private final TransferClassifier classifier = new RtIufTesClassifier();

	@Test
	void givenMatchedConditionWhenDefineThenSuccess() {
		// Arrange
		transferDTO.setAmountCents(10_00L);
		paymentsReportingDTO.setAmountPaidCents(10_00L);
		paymentsReportingDTO.setTotalAmountCents(100_00L);
		treasuryIUF.setBillAmountCents(100_00L);
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, null, paymentsReportingDTO, treasuryIUF, Optional.empty());
		// Assert
		assertEquals(ClassificationsEnum.RT_IUF_TES, result);
	}

	@Test
	void givenUnmatchedTreasuryDTOWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, null, paymentsReportingDTO, null, Optional.empty());
		// Assert
		assertNull(result);
	}

	@ParameterizedTest
	@CsvSource({
		"10000, 10000",
		"100, 10000",
		"1000, 100"
	})
	void givenUnmatchedIUFAmountsWhenDefineThenReturnNull(Long paymentsReportingAmount, Long treasuryAmount) {
		// Arrange
		transferDTO.setAmountCents(100L);
		paymentsReportingDTO.setAmountPaidCents(paymentsReportingAmount);
		treasuryIUF.setBillAmountCents(treasuryAmount);
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, null, paymentsReportingDTO, treasuryIUF, Optional.empty());
		// Assert
		assertNull(result);
	}

	@ParameterizedTest
	@CsvSource({
			"10000, 10000",
			"100, 10000",
			"1000, 100"
	})
	void givenUnmatchedIUVAmountsWhenDefineThenReturnNull(Long transferAmount, Long paymentsReportingAmount) {
		// Arrange
		transferDTO.setAmountCents(transferAmount);
		paymentsReportingDTO.setAmountPaidCents(paymentsReportingAmount);
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, null, paymentsReportingDTO, null, Optional.empty());
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedPaymentsReportingWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, null, null, treasuryIUF, Optional.empty());
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedTransferWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(null, null, paymentsReportingDTO, treasuryIUF, Optional.empty());
		// Assert
		assertNull(result);
	}
}
