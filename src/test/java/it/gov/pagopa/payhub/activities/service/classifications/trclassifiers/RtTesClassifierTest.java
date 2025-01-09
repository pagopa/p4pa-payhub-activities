package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RtTesClassifierTest {
	private final PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();
	private final TransferDTO transferDTO = TransferFaker.buildTransferDTO();
	private final TreasuryDTO treasuryDTO = TreasuryFaker.buildTreasuryDTO();

	TransferClassifier classifier = new RtTesClassifier();

	@Test
	void givenMatchedConditionWhenDefineThenSuccess() {
		// Arrange
		transferDTO.setAmount(100L);
		treasuryDTO.setBillIpNumber(BigDecimal.valueOf(1.00D));
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, null, treasuryDTO);
		// Assert
		assertEquals(ClassificationsEnum.RT_TES, result);
	}

	@Test
	void givenUnmatchedPaymentsReportingDTOWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, paymentsReportingDTO, treasuryDTO);
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedAmountWhenDefineThenReturnNull() {
		// Arrange
		transferDTO.setAmount(100L);
		treasuryDTO.setBillIpNumber(BigDecimal.valueOf(100.00D));
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, paymentsReportingDTO, treasuryDTO);
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedTreasuryDTOWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, paymentsReportingDTO, null);
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedTransferDTOWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(null, paymentsReportingDTO, treasuryDTO);
		// Assert
		assertNull(result);
	}
}
