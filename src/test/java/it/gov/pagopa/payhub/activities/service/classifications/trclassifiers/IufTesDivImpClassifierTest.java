package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuv;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IufTesDivImpClassifierTest {
	private final PaymentsReporting paymentsReportingDTO = PaymentsReportingFaker.buildPaymentsReporting();
	private final TreasuryIuf treasuryIUF = TreasuryFaker.buildTreasuryIuf();
	private final TreasuryIuv treasuryIUV = TreasuryFaker.buildTreasuryIuv();

	private final TransferClassifier classifier = new IufTesDivImpClassifier();

	@Test
	void givenMatchedConditionWhenDefineThenSuccess() {
		// Arrange
		paymentsReportingDTO.setAmountPaidCents(100L);
		treasuryIUF.setBillAmountCents(10000L);
		// Act
		ClassificationsEnum result = classifier.classify(null, paymentsReportingDTO, treasuryIUF, treasuryIUV);
		// Assert
		assertEquals(ClassificationsEnum.IUF_TES_DIV_IMP, result);
	}

	@Test
	void givenUnmatchedTreasuryDTOWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(null, paymentsReportingDTO, null, null);
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedAmountWhenDefineThenReturnNull() {
		// Arrange
		paymentsReportingDTO.setAmountPaidCents(100L);
		treasuryIUF.setBillAmountCents(100L);
		// Act
		ClassificationsEnum result = classifier.classify(null, paymentsReportingDTO, treasuryIUF, treasuryIUV);
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedPaymentsReportingWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(null, null, treasuryIUF, treasuryIUV);
		// Assert
		assertNull(result);
	}
}
