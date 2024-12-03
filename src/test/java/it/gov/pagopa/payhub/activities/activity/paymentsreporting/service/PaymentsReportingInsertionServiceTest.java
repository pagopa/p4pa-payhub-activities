package it.gov.pagopa.payhub.activities.activity.paymentsreporting.service;

import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dto.reportingflow.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.service.ingestionflow.PaymentsReportingInsertionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentsReportingInsertionServiceTest {

	@Mock
	private PaymentsReportingDao paymentsReportingDaoMock;

	private PaymentsReportingInsertionService service;

	@BeforeEach
	void init() {
		service = new PaymentsReportingInsertionService(paymentsReportingDaoMock);
	}

	@Test
	void givenPaymentsReportingThenSuccess() {
		PaymentsReportingDTO paymentsReportingDTO = new PaymentsReportingDTO();
		int insertedId = -1;
		when(paymentsReportingDaoMock.insertPaymentsReporting(paymentsReportingDTO)).thenReturn(insertedId);

		long insert = service.insertPaymentsReporting(paymentsReportingDTO);

		assertEquals(1, insert);
	}
}