package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.utility.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.utility.faker.TransferFaker;
import it.gov.pagopa.payhub.activities.utility.faker.TreasuryFaker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class IufTesDivImpClassifierTest {
	private final TransferDTO transferDTO = TransferFaker.buildTransferDTO();
	private final PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();
	private final TreasuryDTO treasuryDTO = TreasuryFaker.buildTreasuryDTO();

	TransferClassifier classifier = new IufTesDivImpClassifier();

	@Test
	void givenMatchedConditionWhenDefineThenSuccess() {
		// Arrange
		paymentsReportingDTO.setTotalAmountCents(100L);
		treasuryDTO.setBillIpNumber(BigDecimal.valueOf(100.00D));
		// Act
		ClassificationsEnum result = classifier.classify(null, paymentsReportingDTO, treasuryDTO);
		// Assert
		assertEquals(ClassificationsEnum.IUF_TES_DIV_IMP, result);
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
