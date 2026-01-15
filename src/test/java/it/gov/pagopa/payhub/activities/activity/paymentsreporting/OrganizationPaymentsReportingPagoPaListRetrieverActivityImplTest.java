package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.PaymentsReportingPagoPaService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
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
                .revision(1)
                .paymentsReportingFileName(idFlow1 + theDayBeforeYesterday + ".xml")
                .build();
        PaymentsReportingIdDTO yesterdayPaymentsReportingId = PaymentsReportingIdDTO.builder()
                .pagopaPaymentsReportingId(idFlow2)
                .flowDateTime(yesterday)
                .revision(1)
                .paymentsReportingFileName(idFlow2 + yesterday + ".xml")
                .build();
        IngestionFlowFile ingestionFlowFile = IngestionFlowFileFaker.buildIngestionFlowFile();
        ingestionFlowFile.setFileName(idFlow2 + now + ".xml");

        Mockito.when(paymentsReportingPagoPaServiceMock.getPaymentsReportingList(organizationId))
                .thenReturn(List.of(theDayBeforeYesterdayPaymentsReportingId, yesterdayPaymentsReportingId));
        Mockito.when(ingestionFlowFileServiceMock.findByOrganizationIdFlowTypeCreateDate(organizationId, IngestionFlowFileTypeEnum.PAYMENTS_REPORTING_PAGOPA, theDayBeforeYesterday))
                .thenReturn(List.of(ingestionFlowFile));

        // When
        List<PaymentsReportingIdDTO> result = activity.retrieveNotImportedPagoPaPaymentsReportingIds(organizationId);

        // Then
        assertEquals(2, result.size());
    }

    @Test
    void whenRetrieveNotImportedPagoPaPaymentsReportingIdsThenReturnEmptyList() {
        // Given
        Long organizationId = 1L;
        Mockito.when(paymentsReportingPagoPaServiceMock.getPaymentsReportingList(organizationId))
                .thenReturn(List.of());

        // When
        List<PaymentsReportingIdDTO> result = activity.retrieveNotImportedPagoPaPaymentsReportingIds(organizationId);

        // Then
        assertTrue(result.isEmpty());
    }
}
