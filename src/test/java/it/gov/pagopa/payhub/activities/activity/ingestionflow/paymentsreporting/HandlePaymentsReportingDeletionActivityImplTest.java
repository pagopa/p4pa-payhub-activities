package it.gov.pagopa.payhub.activities.activity.ingestionflow.paymentsreporting;

import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsReportingTransferDTO;
import it.gov.pagopa.payhub.activities.service.ingestionflow.paymentsreporting.PaymentsReportingMapperService;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker.buildPaymentsReporting;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class HandlePaymentsReportingDeletionActivityImplTest {

    @Mock
    private PaymentsReportingService paymentsReportingServiceMock;
    @Mock
    private PaymentsReportingMapperService paymentsReportingMapperServiceMock;

    private HandlePaymentsReportingDeletionActivity activity;

    @BeforeEach
    void setUp() {
        activity = new HandlePaymentsReportingDeletionActivityImpl(paymentsReportingServiceMock, paymentsReportingMapperServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(paymentsReportingServiceMock, paymentsReportingMapperServiceMock);
    }

    @Test
    void givenSuccessfulConditionsWhenHandlePaymentsReportingDeletionThenOk(){
        Long orgId = 1L;
        String iuf = "IUF";
        Long ingestionFlowFileId = 100L;

        PaymentsReporting paymentsReporting = buildPaymentsReporting();
        List<PaymentsReporting> paymentsReportingDeleted = List.of(paymentsReporting);

        PaymentsReportingTransferDTO paymentsReportingTransferDTO = PaymentsReportingTransferDTO.builder()
                .orgId(orgId).iuv("iuv").iur("iur").transferIndex(1).paymentOutcomeCode("0").build();
        List<PaymentsReportingTransferDTO> expected = List.of(paymentsReportingTransferDTO);

        Mockito.when(paymentsReportingServiceMock.findAndDeleteByOrgIdAndIufAndIngestionFlowFileIdNot(orgId, iuf, ingestionFlowFileId))
                .thenReturn(paymentsReportingDeleted);

        Mockito.when(paymentsReportingMapperServiceMock.map(paymentsReporting))
                .thenReturn(paymentsReportingTransferDTO);

        List<PaymentsReportingTransferDTO> result = activity.handlePaymentsReportingDeletion(orgId, iuf, ingestionFlowFileId);

        assertEquals(expected, result);
    }
}
