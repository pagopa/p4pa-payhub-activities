package it.gov.pagopa.payhub.activities.activity.ingestionflow.paymentsreporting;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.paymentsreporting.OrganizationPaymentsReportingPagoPaRetrieverActivity;
import it.gov.pagopa.payhub.activities.activity.ingestionflow.paymentsreporting.OrganizationPaymentsReportingPagoPaRetrieverActivityImpl;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.PaymentsReportingPagoPaService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.FlowFileTypeEnum.PAYMENTS_REPORTING_PAGOPA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class OrganizationPaymentsReportingPagoPaRetrieverActivityTest {
	@Mock
	private PaymentsReportingPagoPaService paymentsReportingPagoPaServiceMock;
	@Mock
	private IngestionFlowFileService ingestionFlowFileServiceMock;

	private OrganizationPaymentsReportingPagoPaRetrieverActivity activity;

	@BeforeEach
	void setUp() {
		activity = new OrganizationPaymentsReportingPagoPaRetrieverActivityImpl(paymentsReportingPagoPaServiceMock, ingestionFlowFileServiceMock);
	}

	@Test
	void whenFetchThenReturnIngestionFlowFileIds() {
		// Given
		Long organizationId = 1L;
		String idFlow = "flow-123";
		OffsetDateTime now = OffsetDateTime.now();
		String filename = idFlow + now +".xml";
		PaymentsReportingIdDTO dto = PaymentsReportingIdDTO.builder()
			.pagopaPaymentsReportingId(idFlow)
			.flowDateTime(now)
			.paymentsReportingFileName(filename)
			.build();
		IngestionFlowFile ingestionFlowFile = IngestionFlowFileFaker.buildIngestionFlowFile();
		Long expectedIngestionFlowFileId = 123L;

		doReturn(List.of(ingestionFlowFile)).when(ingestionFlowFileServiceMock)
			.findByOrganizationIdFlowTypeFilename(organizationId, PAYMENTS_REPORTING_PAGOPA, dto.getPaymentsReportingFileName());
		doReturn(expectedIngestionFlowFileId).when(paymentsReportingPagoPaServiceMock).fetchPaymentReporting(organizationId, idFlow, filename);

		// When
		List<Long> result = activity.fetch(organizationId, List.of(dto));

		// When Then
		assertEquals(List.of(expectedIngestionFlowFileId), result);
	}

	@Test
	void whenFetchThenReturnEmptyList() {
		// Given
		Long organizationId = 1L;
		List<PaymentsReportingIdDTO> paymentsReportingIds = Collections.emptyList();

		// When
		List<Long> result = activity.fetch(organizationId, paymentsReportingIds);

		// Then
		assertTrue(result.isEmpty());
	}
}