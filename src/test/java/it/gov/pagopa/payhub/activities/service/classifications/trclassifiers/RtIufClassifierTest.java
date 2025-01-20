package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RtIufClassifierTest {
	private final TransferDTO transferDTO = TransferFaker.buildTransferDTO();
	private final PaymentsReporting paymentsReportingDTO = PaymentsReportingFaker.buildPaymentsReporting();

	TransferClassifier classifier = new RtIufClassifier();

	@Test
	void givenMatchedConditionWhenDefineThenSuccess() {
		//Arrange
		transferDTO.setAmountCents(100L);
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
		transferDTO.setAmountCents(100L);
		paymentsReportingDTO.setAmountPaidCents(1000L);
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, paymentsReportingDTO, null);
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedPaymentsReportingWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, null, null);
		// Assert
		assertNull(result);
	}
}
