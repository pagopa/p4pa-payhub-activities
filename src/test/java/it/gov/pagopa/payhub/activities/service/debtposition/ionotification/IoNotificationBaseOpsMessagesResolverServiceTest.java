package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.dto.IONotificationMessage;
import it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig.GenericWfExecutionConfig;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class IoNotificationBaseOpsMessagesResolverServiceTest {

    @Mock
    private DebtPositionTypeOrgService debtPositionTypeOrgServiceMock;

    private IoNotificationBaseOpsMessagesResolverService service;

    @BeforeEach
    void init(){
        service = new IoNotificationBaseOpsMessagesResolverService(debtPositionTypeOrgServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(debtPositionTypeOrgServiceMock);
    }

    @Test
    void givenNoIoMessagesWhenResolveIoMessagesThenReturnNull(){
        // Given
        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        PaymentEventType paymentEventType = PaymentEventType.DP_CREATED;

        // When
        IONotificationDTO result = service.resolveIoMessages(debtPositionDTO, paymentEventType, null);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void givenNoIoMessageForRequestedOpWhenResolveIoMessagesThenReturnNull(){
        // Given
        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        PaymentEventType paymentEventType = PaymentEventType.DP_CREATED;

        // When
        IONotificationDTO result = service.resolveIoMessages(debtPositionDTO, paymentEventType, new GenericWfExecutionConfig.IONotificationBaseOpsMessages());

        // Then
        Assertions.assertNull(result);
    }

    private final GenericWfExecutionConfig.IONotificationBaseOpsMessages ioMessages = GenericWfExecutionConfig.IONotificationBaseOpsMessages.builder()
            .created(new IONotificationMessage("CREATED_SUBJECT", "CREATED_SUBJECT"))
            .updated(new IONotificationMessage("UPDATED_SUBJECT", "UPDATED_SUBJECT"))
            .deleted(new IONotificationMessage("DELETED_SUBJECT", "DELETED_SUBJECT"))
            .build();

    private final Map<PaymentEventType, IONotificationMessage> paymentEventType2IONotificationMessage = Map.of(
            PaymentEventType.DP_CREATED, ioMessages.getCreated(),

            PaymentEventType.DP_UPDATED, ioMessages.getUpdated(),
            PaymentEventType.DPI_ADDED, ioMessages.getUpdated(),
            PaymentEventType.DPI_UPDATED, ioMessages.getUpdated(),

            PaymentEventType.DP_CANCELLED, ioMessages.getDeleted(),
            PaymentEventType.DPI_CANCELLED, ioMessages.getDeleted()
    );

    @ParameterizedTest
    @EnumSource(value = PaymentEventType.class)
    void whenResolveIoMessagesThenReturnIt(PaymentEventType paymentEventType){
        // Given
        long debtPositionTypeOrgId = 1L;
        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setDebtPositionTypeOrgId(debtPositionTypeOrgId);

        DebtPositionTypeOrg debtPositionTypeOrg = new DebtPositionTypeOrg();
        debtPositionTypeOrg.setServiceId("SERVICEID");

        IONotificationMessage expectedIoMessage = paymentEventType2IONotificationMessage.get(paymentEventType);
        IONotificationDTO expectedResult = null;
        if(expectedIoMessage!=null) {
            Mockito.when(debtPositionTypeOrgServiceMock.getById(debtPositionTypeOrgId))
                    .thenReturn(debtPositionTypeOrg);

            expectedResult = IONotificationDTO.builder()
                    .serviceId("SERVICEID")
                    .ioTemplateSubject(expectedIoMessage.getSubject())
                    .ioTemplateMessage(expectedIoMessage.getMessage())
                    .build();
        }

        // When
        IONotificationDTO result = service.resolveIoMessages(debtPositionDTO, paymentEventType, ioMessages);

        // Then
        Assertions.assertEquals(expectedResult, result);
    }
}
