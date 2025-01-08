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

class RtIufTesClassifierTest {
	private final PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();
	private final TransferDTO transferDTO = TransferFaker.buildTransferDTO();
	private final TreasuryDTO treasuryDTO = TreasuryFaker.buildTreasuryDTO();

	TransferClassifier classifier = new RtIufTesClassifier();

	@Test
	void givenMatchedConditionWhenDefineThenSuccess() {
		// Arrange
		transferDTO.setAmount(10000L);
		paymentsReportingDTO.setAmountPaidCents(10000L);
		treasuryDTO.setBillIpNumber(BigDecimal.valueOf(100.00D));
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, paymentsReportingDTO, treasuryDTO);
		// Assert
		assertEquals(ClassificationsEnum.RT_IUF_TES, result);
	}

	@Test
	void givenUnmatchedTreasuryDTOWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, paymentsReportingDTO, null);
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedAmountWhenDefineThenReturnNull() {
		// Arrange
		transferDTO.setAmount(100L);
		paymentsReportingDTO.setAmountPaidCents(10000L);
		treasuryDTO.setBillIpNumber(BigDecimal.valueOf(100.00D));
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, paymentsReportingDTO, treasuryDTO);
		// Assert
		assertNull(result);
	}
}
