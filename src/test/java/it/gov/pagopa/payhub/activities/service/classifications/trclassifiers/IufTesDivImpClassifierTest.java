package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IufTesDivImpClassifierTest {
	private final PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();
	private final Treasury treasuryDTO = TreasuryFaker.buildTreasuryDTO();

	TransferClassifier classifier = new IufTesDivImpClassifier();

	@Test
	void givenMatchedConditionWhenDefineThenSuccess() {
		// Arrange
		paymentsReportingDTO.setAmountPaidCents(100L);
		treasuryDTO.setBillAmountCents(10000L);
		// Act
		ClassificationsEnum result = classifier.classify(null, paymentsReportingDTO, treasuryDTO);
		// Assert
		assertEquals(ClassificationsEnum.IUF_TES_DIV_IMP, result);
	}

	@Test
	void givenUnmatchedTreasuryDTOWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(null, paymentsReportingDTO, null);
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedAmountWhenDefineThenReturnNull() {
		// Arrange
		paymentsReportingDTO.setAmountPaidCents(100L);
		treasuryDTO.setBillAmountCents(100L);
		// Act
		ClassificationsEnum result = classifier.classify(null, paymentsReportingDTO, treasuryDTO);
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedPaymentsReportingDTOWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(null, null, treasuryDTO);
		// Assert
		assertNull(result);
	}
}
