package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RtNoIufClassifierTest {
	private final PaymentsReporting paymentsReportingDTO = PaymentsReportingFaker.buildPaymentsReporting();
	private final Transfer transferDTO = TransferFaker.buildTransfer();
	private final TreasuryIuf treasuryIUF = TreasuryFaker.buildTreasuryIuf();

	private final TransferClassifier classifier = new RtNoIufClassifier();

	@Test
	void givenOnlyTransferWhenDefineThenSuccess() {
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, null, null, null, treasuryIUF);
		// Assert
		assertEquals(ClassificationsEnum.RT_NO_IUF, result);
	}

	@Test
	void givenNullTreasuryDTOAndUnmatchedAmountWhenDefineThenSuccess() {
		// Arrange
		transferDTO.setAmountCents(100L);
		paymentsReportingDTO.setAmountPaidCents(1000L);
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, null, null, paymentsReportingDTO, treasuryIUF);
		// Assert
		assertEquals(ClassificationsEnum.RT_NO_IUF, result);
	}

	@Test
	void givenNullPaymentsReportingAndUnmatchedAmountWhenDefineThenSuccess() {
		// Arrange
		transferDTO.setAmountCents(100L);
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, null, null,null, treasuryIUF);
		// Assert
		assertEquals(ClassificationsEnum.RT_NO_IUF, result);
	}


	@Test
	void givenEqualsAmountWhenDefineThenReturnNull() {
		// Arrange
		transferDTO.setAmountCents(1000L);
		paymentsReportingDTO.setAmountPaidCents(1000L);
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, null, null, paymentsReportingDTO, treasuryIUF);
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedTransferWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(null, null, null, null, null);
		// Assert
		assertNull(result);
	}
}
