package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RtIufClassifierTest {
	private final TransferDTO transferDTO = TransferFaker.buildTransferDTO();
	private final PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();

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
	void givenUnmatchedTransferDTOWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(null, paymentsReportingDTO, null);
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedAmountWhenDefineThenReturnNull() {
		//Arrange
		transferDTO.setAmount(100L);
		paymentsReportingDTO.setAmountPaidCents(1000L);
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, paymentsReportingDTO, null);
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedPaymentsReportingDTOWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, null, null);
		// Assert
		assertNull(result);
	}
}
