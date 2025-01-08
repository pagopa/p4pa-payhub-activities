package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.utility.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.utility.faker.TransferFaker;
import it.gov.pagopa.payhub.activities.utility.faker.TreasuryFaker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class RtIufClassifierTest {
	private final TransferDTO transferDTO = TransferFaker.buildTransferDTO();
	private final PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();
	private final TreasuryDTO treasuryDTO = TreasuryFaker.buildTreasuryDTO();

	TransferClassifier classifier = new RtIufClassifier();

	@Test
	void givenMatchedConditionWhenDefineThenSuccess() {
		//Arrange
		transferDTO.setAmount(100L);
		paymentsReportingDTO.setAmountPaidCents(100L);
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, paymentsReportingDTO, null);
		// Assert
		assertEquals(ClassificationsEnum.RT_IUF, result);
	}

	@Test
	void givenUnmatchedConditionWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(null, paymentsReportingDTO, null);
		// Assert
		assertNull(result);
	}

	@Test
	void givenTransferDTOThenGetAmountCents() {
		assertDoesNotThrow(() -> classifier.getAmountCents(transferDTO));
	}

	@Test
	void givenPaymentsReportingDTOThenGetAmountCents() {
		assertDoesNotThrow(() -> classifier.getAmountCents(paymentsReportingDTO));
	}

	@Test
	void givenTreasuryDTOThengetAmountCents() {
		assertDoesNotThrow(() -> classifier.getAmountCents(treasuryDTO));
	}
}
