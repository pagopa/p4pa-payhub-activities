package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.PaymentsReportingPagoPaService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationPaymentsReportingPagoPaRetrieverActivityImplTest {
	@Mock
	private PaymentsReportingPagoPaService paymentsReportingPagoPaServiceMock;
	@Mock
	private IngestionFlowFileService ingestionFlowFileServiceMock;

	private OrganizationPaymentsReportingPagoPaRetrieverActivity activity;

	@BeforeEach
	void setUp() {
		activity = new OrganizationPaymentsReportingPagoPaRetrieverActivityImpl(
			paymentsReportingPagoPaServiceMock,
			ingestionFlowFileServiceMock
		);
	}

	@AfterEach
	void tearDown() {
		Mockito.verifyNoMoreInteractions(paymentsReportingPagoPaServiceMock, ingestionFlowFileServiceMock);
	}

	@Test
	void retrieve() {
		// Given
		Long organizationId = 1L;
		String idFlow1 = "flow-123";
		String idFlow2 = "flow-456";
		OffsetDateTime now = OffsetDateTime.now();
		IngestionFlowFile.FlowFileTypeEnum flowFileType = IngestionFlowFile.FlowFileTypeEnum.PAYMENTS_REPORTING_PAGOPA;
		PaymentsReportingIdDTO paymentsReportingIdDTO1 = PaymentsReportingIdDTO.builder()
			.pagopaPaymentsReportingId(idFlow1)
			.flowDateTime(now)
			.paymentsReportingFileName(idFlow1 + now +".xml")
			.build();
		PaymentsReportingIdDTO paymentsReportingIdDTO2 = PaymentsReportingIdDTO.builder()
			.pagopaPaymentsReportingId(idFlow2)
			.flowDateTime(now)
			.paymentsReportingFileName(idFlow2 + now.plusDays(1) +".xml")
			.build();
		IngestionFlowFile ingestionFlowFile = IngestionFlowFile.builder().fileName(idFlow1 + now +".xml").build();

		when(paymentsReportingPagoPaServiceMock.getPaymentsReportingList(organizationId))
			.thenReturn(List.of(paymentsReportingIdDTO1, paymentsReportingIdDTO2));
		when(ingestionFlowFileServiceMock.findByOrganizationIdFlowTypeCreateDate(organizationId, flowFileType, paymentsReportingIdDTO1.getFlowDateTime(), OffsetDateTime.now()))
			.thenReturn(List.of(ingestionFlowFile));

		// When  Then
		assertDoesNotThrow(() -> activity.retrieve(organizationId));
	}
}
