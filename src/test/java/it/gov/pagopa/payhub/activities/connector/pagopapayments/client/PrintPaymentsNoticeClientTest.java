package it.gov.pagopa.payhub.activities.connector.pagopapayments.client;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.config.PagoPaPaymentsApisHolder;
import it.gov.pagopa.pu.pagopapayments.client.generated.PrintPaymentNoticeApi;
import it.gov.pagopa.pu.pagopapayments.dto.generated.NoticeRequestMassiveDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(MockitoExtension.class)
class PrintPaymentsNoticeClientTest {

    @Mock
    private PagoPaPaymentsApisHolder pagoPaPaymentsApisHolderMock;
    @Mock
    private PrintPaymentNoticeApi printPaymentNoticeApiMock;

    private PrintPaymentNoticeClient printPaymentNoticeClient;

    private PodamFactory podamFactory;

    @BeforeEach
    void setUp() {
        printPaymentNoticeClient = new PrintPaymentNoticeClient(pagoPaPaymentsApisHolderMock);
        podamFactory = new PodamFactoryImpl();
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                pagoPaPaymentsApisHolderMock
        );
    }

    @Test
    void whenGenerateMassiveThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        NoticeRequestMassiveDTO request = podamFactory.manufacturePojo(NoticeRequestMassiveDTO.class);

        Mockito.when(pagoPaPaymentsApisHolderMock.getPrintPaymentNoticeApi(accessToken))
                .thenReturn(printPaymentNoticeApiMock);

        // When
        printPaymentNoticeClient.generateMassive(request, accessToken);

        // Then
        Mockito.verify(printPaymentNoticeApiMock)
                .generateMassive(request);
    }

    @Test
    void whenGetSignedUrlThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long orgId = 1L;
        String folderId = "folderId";

        Mockito.when(pagoPaPaymentsApisHolderMock.getPrintPaymentNoticeApi(accessToken))
                .thenReturn(printPaymentNoticeApiMock);

        // When
        printPaymentNoticeClient.getSignedUrl(orgId, folderId, accessToken);

        // Then
        Mockito.verify(printPaymentNoticeApiMock)
                .getSignedUrl(orgId, folderId);
    }
}
