package it.gov.pagopa.payhub.activities.service.paymentsreporting;

import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.exception.PaymentsReportingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
		List<PaymentsReportingDTO> dtoList = List.of(new PaymentsReportingDTO());
		when(paymentsReportingDaoMock.saveAll(dtoList)).thenReturn(dtoList);

		assertDoesNotThrow(() -> service.savePaymentsReporting(dtoList), "Error occurred while saving");
	}

	@Test
	void givenPaymentsReportingThenException() {
		List<PaymentsReportingDTO> dtoList = List.of(new PaymentsReportingDTO());
		when(paymentsReportingDaoMock.saveAll(dtoList)).thenThrow(PaymentsReportingException.class);

		assertThrows(PaymentsReportingException.class,
			() -> service.savePaymentsReporting(dtoList), "Error occurred while saving payment reports");
	}
}