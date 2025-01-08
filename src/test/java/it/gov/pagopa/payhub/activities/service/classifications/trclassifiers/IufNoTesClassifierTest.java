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

class IufNoTesClassifierTest {
	private final TransferDTO transferDTO = TransferFaker.buildTransferDTO();
	private final PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();
	private final TreasuryDTO treasuryDTO = TreasuryFaker.buildTreasuryDTO();

	TransferClassifier classifier = new IufNoTesClassifier();

	@Test
	void givenMatchedConditionWhenDefineThenSuccess() {
		// Act
		ClassificationsEnum result = classifier.classify(null, paymentsReportingDTO, null);
		// Assert
		assertEquals(ClassificationsEnum.IUF_NO_TES, result);
	}

	@Test
	void givenUnmatchedConditionWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(null, paymentsReportingDTO, treasuryDTO);
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
	void givenTreasuryDTOThenGetAmountCents() {
		assertDoesNotThrow(() -> classifier.getAmountCents(treasuryDTO));
	}
}
