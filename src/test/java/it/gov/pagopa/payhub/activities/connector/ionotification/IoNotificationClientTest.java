package it.gov.pagopa.payhub.activities.connector.ionotification;

import it.gov.pagopa.pu.p4paionotification.generated.ApiClient;
import it.gov.pagopa.pu.p4paionotification.dto.generated.NotificationQueueDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class IoNotificationClientTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;
    @Mock
    private RestTemplate restTemplateMock;

    private IoNotificationClient ioNotificationClient;

    @BeforeEach
    void setUp() {
        Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        Mockito.when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
        ApiClient apiClient = new ApiClient(restTemplateMock);
        String baseUrl = "http://example.com";
        apiClient.setBasePath(baseUrl);
        ioNotificationClient = new IoNotificationClientImpl(restTemplateBuilderMock, baseUrl);
    }

    @Test
    void whenSendMessageThenSuccess() {
        NotificationQueueDTO notificationQueueDTO = buildNotificationQueueDTO();

        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.OK);

        Mockito.when(restTemplateMock.exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        ioNotificationClient.sendMessage(notificationQueueDTO);

        Mockito.verify(restTemplateBuilderMock, times(1)).build();
        Mockito.verify(restTemplateMock, times(1)).getUriTemplateHandler();
    }

    private static NotificationQueueDTO buildNotificationQueueDTO() {
        NotificationQueueDTO notificationQueueDTO = new NotificationQueueDTO();
        notificationQueueDTO.setFiscalCode("fiscalCode");
        notificationQueueDTO.setEnteId(1L);
        notificationQueueDTO.setTipoDovutoId(1L);
        notificationQueueDTO.setOperationType("operationType");
        notificationQueueDTO.setPaymentDate("paymentDate");
        notificationQueueDTO.setAmount("amount");
        notificationQueueDTO.setIuv("iuv");
        notificationQueueDTO.setPaymentReason("paymentReason");
        return notificationQueueDTO;
    }
}
