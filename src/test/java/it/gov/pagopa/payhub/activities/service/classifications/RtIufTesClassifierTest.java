package it.gov.pagopa.payhub.activities.service.classifications;

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

class RtIufTesClassifierTest {
	private final PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();
	private final TransferDTO transferDTO = TransferFaker.buildTransferDTO();
	private final TreasuryDTO treasuryDTO = TreasuryFaker.buildTreasuryDTO();

	LabelClassifier classifier = new RtIufTesClassifier();

	@Test
	void whenDefineThenSuccess() {
		// Arrange
		transferDTO.setAmount(10000L);
		paymentsReportingDTO.setAmountPaidCents(10000L);
		treasuryDTO.setBillIpNumber(BigDecimal.valueOf(100.00D));
		// Act
		ClassificationsEnum result = classifier.define(transferDTO, paymentsReportingDTO, treasuryDTO);
		// Assert
		assertEquals(ClassificationsEnum.RT_IUF_TES, result);
	}

	@Test
	void whenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.define(transferDTO, null, null);
		// Assert
		assertNull(result);
	}
}