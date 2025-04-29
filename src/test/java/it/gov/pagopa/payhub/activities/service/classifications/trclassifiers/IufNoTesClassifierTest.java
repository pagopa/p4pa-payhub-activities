package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IufNoTesClassifierTest {
	private final PaymentsReporting paymentsReportingDTO = PaymentsReportingFaker.buildPaymentsReporting();
	private final TreasuryIuf treasuryIUF = TreasuryFaker.buildTreasuryIuf();

	private final TransferClassifier classifier = new IufNoTesClassifier();

	@Test
	void givenMatchedConditionWhenDefineThenSuccess() {
		// Act
		ClassificationsEnum result = classifier.classify(null, null,null, paymentsReportingDTO, null);
		// Assert
		assertEquals(ClassificationsEnum.IUF_NO_TES, result);
	}

	@Test
	void givenUnmatchedTreasuryDTOWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(null, null,null, paymentsReportingDTO, treasuryIUF);
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedPaymentsReportingWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(null,null,null, null, treasuryIUF);
		// Assert
		assertNull(result);
	}
}
