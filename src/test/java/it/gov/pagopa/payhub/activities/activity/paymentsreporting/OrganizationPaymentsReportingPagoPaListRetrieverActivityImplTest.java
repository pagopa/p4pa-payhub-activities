package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.PaymentsReportingPagoPaService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class OrganizationPaymentsReportingPagoPaListRetrieverActivityImplTest {
	@Mock
	private PaymentsReportingPagoPaService paymentsReportingPagoPaServiceMock;
	@Mock
	private IngestionFlowFileService ingestionFlowFileServiceMock;

	private OrganizationPaymentsReportingPagoPaListRetrieverActivity activity;

	@BeforeEach
	void setUp() {
		activity = new OrganizationPaymentsReportingPagoPaListRetrieverActivityImpl(
			paymentsReportingPagoPaServiceMock,
			ingestionFlowFileServiceMock
		);
	}

	@Test
	void whenRetrieveNotImportedPagoPaPaymentsReportingIdsThenReturnList() {
		// Given
		Long organizationId = 1L;
		String idFlow1 = "flow-123";
		String idFlow2 = "flow-456";
		OffsetDateTime now = OffsetDateTime.now();
		OffsetDateTime yesterday = now.minusDays(1);
		OffsetDateTime theDayBeforeYesterday = now.minusDays(2);
		PaymentsReportingIdDTO theDayBeforeYesterdayPaymentsReportingId = PaymentsReportingIdDTO.builder()
			.pagopaPaymentsReportingId(idFlow1)
			.flowDateTime(theDayBeforeYesterday)
			.paymentsReportingFileName(idFlow1 + theDayBeforeYesterday +".xml")
			.build();
		PaymentsReportingIdDTO yesterdayPaymentsReportingId = PaymentsReportingIdDTO.builder()
			.pagopaPaymentsReportingId(idFlow2)
			.flowDateTime(yesterday)
			.paymentsReportingFileName(idFlow2 + yesterday +".xml")
			.build();
		IngestionFlowFile ingestionFlowFile = IngestionFlowFileFaker.buildIngestionFlowFile();
		ingestionFlowFile.setFileName(idFlow2 + now +".xml");

		doReturn(List.of(theDayBeforeYesterdayPaymentsReportingId, yesterdayPaymentsReportingId))
			.when(paymentsReportingPagoPaServiceMock).getPaymentsReportingList(organizationId);
		lenient().doReturn(List.of(ingestionFlowFile)).when(ingestionFlowFileServiceMock)
			.findByOrganizationIdFlowTypeCreateDate(organizationId, IngestionFlowFileTypeEnum.PAYMENTS_REPORTING_PAGOPA, theDayBeforeYesterday);

		// When
		List<PaymentsReportingIdDTO> result = activity.retrieveNotImportedPagoPaPaymentsReportingIds(organizationId);

		// Then
		assertEquals(2, result.size());
	}

	@Test
	void whenRetrieveNotImportedPagoPaPaymentsReportingIdsThenReturnEmptyList() {
		// Given
		Long organizationId = 1L;
		doReturn(List.of()).when(paymentsReportingPagoPaServiceMock).getPaymentsReportingList(organizationId);

		// When
		List<PaymentsReportingIdDTO> result = activity.retrieveNotImportedPagoPaPaymentsReportingIds(organizationId);

		// Then
		assertTrue(result.isEmpty());
	}
}
