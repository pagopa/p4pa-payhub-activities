package it.gov.pagopa.payhub.activities.service.paymentsreporting;

import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
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
		when(paymentsReportingDaoMock.save(paymentsReportingDTO)).thenReturn(paymentsReportingDTO);

		PaymentsReportingDTO actual = service.savePaymentsReporting(paymentsReportingDTO);

		assertEquals(paymentsReportingDTO, actual);
	}
}