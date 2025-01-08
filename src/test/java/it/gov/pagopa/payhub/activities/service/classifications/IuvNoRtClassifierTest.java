package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.utility.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.utility.faker.TransferFaker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IuvNoRtClassifierTest {
	private final PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();
	private final TransferDTO transferDTO = TransferFaker.buildTransferDTO();

	LabelClassifier classifier = new IuvNoRtClassifier();

	@Test
	void whenDefineThenSuccess() {
		// Act
		ClassificationsEnum result = classifier.define(null, paymentsReportingDTO, null);
		// Assert
		assertEquals(ClassificationsEnum.IUV_NO_RT, result);
	}

	@Test
	void whenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.define(transferDTO, paymentsReportingDTO, null);
		// Assert
		assertNull(result);
	}
}