package it.gov.pagopa.payhub.activities.connector.pagopapayments;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.client.PrintPaymentNoticeClient;
import it.gov.pagopa.pu.pagopapayments.dto.generated.GeneratedNoticeMassiveFolderDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.NoticeRequestMassiveDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.SignedUrlResultDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrintPaymentNoticeServiceTest {
    @Mock
    private PrintPaymentNoticeClient printPaymentNoticeClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private PrintPaymentNoticeService service;

    private PodamFactory podamFactory;

    @BeforeEach
    void setUp() {
        service = new PrintPaymentNoticeServiceImpl(printPaymentNoticeClientMock, authnServiceMock);
        podamFactory = new PodamFactoryImpl();
    }

    @AfterEach
    void tearDown() {
        Mockito.verifyNoMoreInteractions(printPaymentNoticeClientMock, authnServiceMock);
    }

    @Test
    void whenGenerateMassiveThenInvokeClient() {
        // Given
        String accessToken = "accessToken";
        NoticeRequestMassiveDTO noticeRequestMassiveDTO = podamFactory.manufacturePojo(NoticeRequestMassiveDTO.class);
        GeneratedNoticeMassiveFolderDTO response = podamFactory.manufacturePojo(GeneratedNoticeMassiveFolderDTO.class);

        when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        when(printPaymentNoticeClientMock.generateMassive(noticeRequestMassiveDTO, accessToken)).thenReturn(response);

        // When
        GeneratedNoticeMassiveFolderDTO result = service.generateMassive(noticeRequestMassiveDTO);

        // Then
        assertEquals(response, result);
    }

    @Test
    void whenGetSignedUrlThenInvokeClient() {
        // Given
        String accessToken = "accessToken";
        Long orgId = 1L;
        String folderId = "folderId";
        SignedUrlResultDTO response = podamFactory.manufacturePojo(SignedUrlResultDTO.class);

        when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        when(printPaymentNoticeClientMock.getSignedUrl(orgId, folderId, accessToken)).thenReturn(response);

        // When
        SignedUrlResultDTO result = service.getSignedUrl(orgId, folderId);

        // Then
        assertEquals(response, result);
    }
}