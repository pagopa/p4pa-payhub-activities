package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IuvNoRtClassifierTest {
	private final Transfer transferDTO = TransferFaker.buildTransfer();
	private final PaymentsReporting paymentsReportingDTO = PaymentsReportingFaker.buildPaymentsReporting();

	private final TransferClassifier classifier = new IuvNoRtClassifier();

	@Test
	void givenMatchedConditionWhenDefineThenSuccess() {
		// Act
		ClassificationsEnum result = classifier.classify(null, paymentsReportingDTO, null, null);
		// Assert
		assertEquals(ClassificationsEnum.IUV_NO_RT, result);
	}

	@Test
	void givenUnmatchedTransferDTOWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, paymentsReportingDTO, null, null);
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedPaymentsReportingWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(null, null, null, null);
		// Assert
		assertNull(result);
	}
}
