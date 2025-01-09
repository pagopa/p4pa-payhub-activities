package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IuvNoRtClassifierTest {
	private final TransferDTO transferDTO = TransferFaker.buildTransferDTO();
	private final PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();

	TransferClassifier classifier = new IuvNoRtClassifier();

	@Test
	void givenMatchedConditionWhenDefineThenSuccess() {
		// Act
		ClassificationsEnum result = classifier.classify(null, paymentsReportingDTO, null);
		// Assert
		assertEquals(ClassificationsEnum.IUV_NO_RT, result);
	}

	@Test
	void givenUnmatchedTransferDTOWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, paymentsReportingDTO, null);
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedPaymentsReportingDTOWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(null, null, null);
		// Assert
		assertNull(result);
	}
}
