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
		String idFlow = "flow-123";
		OffsetDateTime now = OffsetDateTime.now();
		IngestionFlowFile.FlowFileTypeEnum flowFileType = IngestionFlowFile.FlowFileTypeEnum.PAYMENTS_REPORTING_PAGOPA;
		PaymentsReportingIdDTO paymentsReportingIdDTO = PaymentsReportingIdDTO.builder()
			.pagopaPaymentsReportingId(idFlow)
			.flowDateTime(now)
			.paymentsReportingFileName(idFlow + now +".xml")
			.build();
		IngestionFlowFile ingestionFlowFile = mock(IngestionFlowFile.class);
		when(paymentsReportingPagoPaServiceMock.getPaymentsReportingList(organizationId))
			.thenReturn(List.of(paymentsReportingIdDTO));
		when(ingestionFlowFileServiceMock.findByOrganizationIdFlowTypeCreateDate(organizationId, flowFileType, paymentsReportingIdDTO.getFlowDateTime()))
			.thenReturn(List.of(ingestionFlowFile));

		// When  Then
		assertDoesNotThrow(() -> activity.retrieve(organizationId));
	}
}