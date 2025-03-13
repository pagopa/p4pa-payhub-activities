package it.gov.pagopa.payhub.activities.service.assessments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentNoPIIService;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPIIResponse;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssessmentsServiceTest {


	@Mock
	private InstallmentNoPIIService installmentNoPIIServiceMock;


	private AssessmentsService service;


	@BeforeEach
	void init() {
		service = new AssessmentsService(installmentNoPIIServiceMock);
	}

	@AfterEach
	void verifyNoMoreInteractions() {
		Mockito.verifyNoMoreInteractions(installmentNoPIIServiceMock);
	}


	@Test
	void getInstallmentsByReceiptId_withValidReceiptId_returnsInstallments() {
		Long receiptId = 1L;
		List<InstallmentNoPIIResponse> expectedInstallments = List.of(new InstallmentNoPIIResponse());
		when(installmentNoPIIServiceMock.getByReceiptId(receiptId)).thenReturn(expectedInstallments);

		List<InstallmentNoPIIResponse> result = service.getInstallmentsByReceiptId(receiptId);

		assertEquals(expectedInstallments, result);
	}

	@Test
	void getInstallmentsByReceiptId_withInvalidReceiptId_returnsEmptyList() {
		Long receiptId = 2L;
		when(installmentNoPIIServiceMock.getByReceiptId(receiptId)).thenReturn(Collections.emptyList());

		List<InstallmentNoPIIResponse> result = service.getInstallmentsByReceiptId(receiptId);

		assertTrue(result.isEmpty());
	}

	@Test
	void getInstallmentsByReceiptId_withNullReceiptId_returnsEmptyList() {
		Long receiptId = null;
		when(installmentNoPIIServiceMock.getByReceiptId(receiptId)).thenReturn(Collections.emptyList());

		List<InstallmentNoPIIResponse> result = service.getInstallmentsByReceiptId(receiptId);

		assertTrue(result.isEmpty());
	}
}