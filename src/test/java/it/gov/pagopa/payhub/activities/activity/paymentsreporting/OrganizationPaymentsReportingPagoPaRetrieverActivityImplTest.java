package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.PaymentsReportingPagoPaService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker;
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
import static org.mockito.Mockito.doReturn;
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
		OffsetDateTime yesterday = now.minusDays(1);
		OffsetDateTime theDayBeforeYesterday = now.minusDays(2);
		IngestionFlowFile.FlowFileTypeEnum flowFileType = IngestionFlowFile.FlowFileTypeEnum.PAYMENTS_REPORTING_PAGOPA;
		PaymentsReportingIdDTO paymentsReportingIdDTO1 = PaymentsReportingIdDTO.builder()
			.pagopaPaymentsReportingId(idFlow1)
			.flowDateTime(theDayBeforeYesterday)
			.paymentsReportingFileName(idFlow1 + theDayBeforeYesterday +".xml")
			.build();
		PaymentsReportingIdDTO paymentsReportingIdDTO2 = PaymentsReportingIdDTO.builder()
			.pagopaPaymentsReportingId(idFlow2)
			.flowDateTime(yesterday)
			.paymentsReportingFileName(idFlow2 + yesterday +".xml")
			.build();
		IngestionFlowFile ingestionFlowFile = IngestionFlowFileFaker.buildIngestionFlowFile();
		ingestionFlowFile.setFileName(idFlow1 + theDayBeforeYesterday +".xml");

		when(paymentsReportingPagoPaServiceMock.getPaymentsReportingList(organizationId))
			.thenReturn(List.of(paymentsReportingIdDTO1, paymentsReportingIdDTO2));
		doReturn(List.of(ingestionFlowFile)).when(ingestionFlowFileServiceMock)
			.findByOrganizationIdFlowTypeCreateDate(organizationId, flowFileType, theDayBeforeYesterday);

		// When  Then
		assertDoesNotThrow(() -> activity.retrieve(organizationId));
	}
}
