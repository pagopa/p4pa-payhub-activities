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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RtNoIufClassifierTest {
	private final PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();
	private final TransferDTO transferDTO = TransferFaker.buildTransferDTO();
	private final TreasuryDTO treasuryDTO = TreasuryFaker.buildTreasuryDTO();

	TransferClassifier classifier = new RtNoIufClassifier();

	@Test
	void givenOnlyTransferDTOWhenDefineThenSuccess() {
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, null, null);
		// Assert
		assertEquals(ClassificationsEnum.RT_NO_IUF, result);
	}

	@Test
	void givenNullTreasuryDTOAndEqualsAmountWhenDefineThenSuccess() {
		// Arrange
		transferDTO.setAmount(100L);
		paymentsReportingDTO.setAmountPaidCents(1000L);
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, paymentsReportingDTO, null);
		// Assert
		assertEquals(ClassificationsEnum.RT_NO_IUF, result);
	}

	@Test
	void givenNullPaymentsReportingDTOAndEqualsAmountWhenDefineThenSuccess() {
		// Arrange
		transferDTO.setAmount(100L);
		treasuryDTO.setBillIpNumber(BigDecimal.valueOf(100.00D));
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, null, treasuryDTO);
		// Assert
		assertEquals(ClassificationsEnum.RT_NO_IUF, result);
	}

	@Test
	void givenUnmatchedConditionWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, paymentsReportingDTO, treasuryDTO);
		// Assert
		assertNull(result);
	}
}
