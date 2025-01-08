package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.utility.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.utility.faker.TransferFaker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RtIufClassifierTest {
	private final PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();
	private final TransferDTO transferDTO = TransferFaker.buildTransferDTO();

	LabelClassifier classifier = new RtIufClassifier();

	@Test
	void whenDefineThenSuccess() {
		//Arrange
		transferDTO.setAmount(100L);
		paymentsReportingDTO.setAmountPaidCents(100L);
		// Act
		ClassificationsEnum result = classifier.define(transferDTO, paymentsReportingDTO, null);
		// Assert
		assertEquals(ClassificationsEnum.RT_IUF, result);
	}

	@Test
	void whenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.define(null, paymentsReportingDTO, null);
		// Assert
		assertNull(result);
	}
}