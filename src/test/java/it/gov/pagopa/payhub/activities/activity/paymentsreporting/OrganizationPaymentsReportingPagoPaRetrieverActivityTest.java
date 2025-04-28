package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.PaymentsReportingPagoPaService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum.PAYMENTS_REPORTING_PAGOPA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class OrganizationPaymentsReportingPagoPaRetrieverActivityTest {

	@Mock
	private OrganizationService organizationServiceMock;
	@Mock
	private PaymentsReportingPagoPaService paymentsReportingPagoPaServiceMock;
	@Mock
	private IngestionFlowFileService ingestionFlowFileServiceMock;

	private OrganizationPaymentsReportingPagoPaRetrieverActivity activity;

	@BeforeEach
	void setUp() {
		activity = new OrganizationPaymentsReportingPagoPaRetrieverActivityImpl(
				organizationServiceMock,
				paymentsReportingPagoPaServiceMock,
				ingestionFlowFileServiceMock);
	}

	@AfterEach
	void verifyNoMoreInteractions(){
		Mockito.verifyNoMoreInteractions(
				organizationServiceMock,
				paymentsReportingPagoPaServiceMock,
				ingestionFlowFileServiceMock
		);
	}

	@Test
	void whenFetchPagoPaPaymentsReportingFilesThenReturnIngestionFlowFileIds() {
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
		ingestionFlowFile.setOrganizationId(organizationId);
		Long expectedIngestionFlowFileId = 123L;
		Organization organization = new Organization();

		Mockito.when(organizationServiceMock.getOrganizationById(organizationId))
				.thenReturn(Optional.of(organization));
		Mockito.when(ingestionFlowFileServiceMock.findByOrganizationIdFlowTypeFilename(organizationId, PAYMENTS_REPORTING_PAGOPA, dto.getPaymentsReportingFileName()))
				.thenReturn(List.of(ingestionFlowFile));
		Mockito.when(paymentsReportingPagoPaServiceMock.fetchPaymentReporting(organization, idFlow, filename))
				.thenReturn(expectedIngestionFlowFileId);

		// When
		List<Long> result = activity.fetchPagoPaPaymentsReportingFiles(organizationId, List.of(dto));

		// When Then
		assertEquals(List.of(expectedIngestionFlowFileId), result);
	}

	@Test
	void givenNotExistentOrganizationWhenFetchPagoPaPaymentsReportingFilesThenIllegalArgumentException() {
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
		List<PaymentsReportingIdDTO> idDtos = List.of(dto);
		IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();

		Mockito.when(ingestionFlowFileServiceMock.findByOrganizationIdFlowTypeFilename(organizationId, PAYMENTS_REPORTING_PAGOPA, dto.getPaymentsReportingFileName()))
				.thenReturn(List.of(ingestionFlowFile));
		Mockito.when(organizationServiceMock.getOrganizationById(organizationId))
				.thenReturn(Optional.empty());

		// When, Then
		Assertions.assertThrows(IllegalArgumentException.class, () -> activity.fetchPagoPaPaymentsReportingFiles(organizationId, idDtos));
	}

	@Test
	void whenFetchPagoPaPaymentsReportingFilesThenReturnEmptyList() {
		// Given
		Long organizationId = 1L;
		List<PaymentsReportingIdDTO> paymentsReportingIds = Collections.emptyList();

		// When
		List<Long> result = activity.fetchPagoPaPaymentsReportingFiles(organizationId, paymentsReportingIds);

		// Then
		assertTrue(result.isEmpty());
	}
}