package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.ionotification.IONotificationFacadeService;
import it.gov.pagopa.payhub.activities.connector.ionotification.mapper.NotificationRequestMapper;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionIoNotificationDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig.GenericWfExecutionConfig;
import it.gov.pagopa.payhub.activities.service.debtposition.DebtPositionOperationTypeResolver;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.MessageResponseDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTOWithMultiplePO;
import static it.gov.pagopa.payhub.activities.util.faker.IONotificationDTOFaker.buildIONotificationDTO;
import static it.gov.pagopa.payhub.activities.util.faker.NotificationRequestDTOFaker.buildNotificationRequestDTO;
import static it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType.DP_CREATED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IONotificationServiceTest {

    @Mock
    private DebtPositionTypeOrgService debtPositionTypeOrgServiceMock;
    @Mock
    private IoNotificationBaseOpsMessagesResolverService baseOpsMessagesResolverServiceMock;
    @Mock
    private IONotificationFacadeService ioNotificationFacadeServiceMock;
    @Mock
    private NotificationRequestMapper notificationRequestMapperMock;
    @Mock
    private DebtPositionOperationTypeResolver debtPositionOperationTypeResolverMock;

    private IONotificationService service;

    @BeforeEach
    void setUp() {
        service = new IONotificationService(
                debtPositionTypeOrgServiceMock,
                baseOpsMessagesResolverServiceMock,
                ioNotificationFacadeServiceMock,
                notificationRequestMapperMock,
                debtPositionOperationTypeResolverMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                debtPositionTypeOrgServiceMock,
                baseOpsMessagesResolverServiceMock,
                ioNotificationFacadeServiceMock,
                notificationRequestMapperMock,
                debtPositionOperationTypeResolverMock
        );
    }

    @Test
    void givenNoIoMessagesWhenSendMessageThenOk() {
        // Given
        IONotificationDTO ioNotificationDTO = buildIONotificationDTO();
        NotificationRequestDTO notificationRequestDTO1 = buildNotificationRequestDTO();
        NotificationRequestDTO notificationRequestDTO2 = buildNotificationRequestDTO();
        NotificationRequestDTO notificationRequestDTO3 = buildNotificationRequestDTO();
        MessageResponseDTO messageResult1 = new MessageResponseDTO(UUID.randomUUID().toString());
        MessageResponseDTO messageResult2 = new MessageResponseDTO(UUID.randomUUID().toString());
        MessageResponseDTO messageResult3 = new MessageResponseDTO(UUID.randomUUID().toString());
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTOWithMultiplePO();
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO =
                new IupdSyncStatusUpdateDTO(InstallmentStatus.UNPAID);

        Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap = new HashMap<>();
        iupdSyncStatusUpdateDTOMap.put("iud", iupdSyncStatusUpdateDTO);
        iupdSyncStatusUpdateDTOMap.put("iud2", iupdSyncStatusUpdateDTO);

        GenericWfExecutionConfig.IONotificationBaseOpsMessages ioMessages = new GenericWfExecutionConfig.IONotificationBaseOpsMessages();

        when(debtPositionOperationTypeResolverMock.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap))
                .thenReturn(DP_CREATED);

        when(baseOpsMessagesResolverServiceMock.resolveIoMessages(debtPositionDTO, DP_CREATED, ioMessages))
                .thenReturn(null);

        when(debtPositionTypeOrgServiceMock.getDefaultIONotificationDetails(debtPositionDTO.getDebtPositionTypeOrgId(), DP_CREATED))
                .thenReturn(ioNotificationDTO);

        when(notificationRequestMapperMock.map(debtPositionDTO, ioNotificationDTO))
                .thenReturn(List.of(notificationRequestDTO1, notificationRequestDTO2, notificationRequestDTO3));

        when(ioNotificationFacadeServiceMock.sendMessage(any()))
                .thenReturn(messageResult1)
                .thenReturn(messageResult2)
                .thenReturn(messageResult3);

        // When
        DebtPositionIoNotificationDTO result = service.sendMessage(debtPositionDTO, iupdSyncStatusUpdateDTOMap, ioMessages);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getMessages().size());

        assertIoMessage(result.getMessages().getFirst(), notificationRequestDTO1, messageResult1);
        assertIoMessage(result.getMessages().get(1), notificationRequestDTO2, messageResult2);
        assertIoMessage(result.getMessages().get(2), notificationRequestDTO3, messageResult3);
    }

    @Test
    void givenIoMessagesBaseOpsWhenSendMessageThenSendThem() {
        // Given
        IONotificationDTO ioNotificationDTO = buildIONotificationDTO();
        NotificationRequestDTO notificationRequestDTO1 = buildNotificationRequestDTO();
        NotificationRequestDTO notificationRequestDTO2 = buildNotificationRequestDTO();
        NotificationRequestDTO notificationRequestDTO3 = buildNotificationRequestDTO();
        MessageResponseDTO messageResult1 = new MessageResponseDTO(UUID.randomUUID().toString());
        MessageResponseDTO messageResult2 = new MessageResponseDTO(UUID.randomUUID().toString());
        MessageResponseDTO messageResult3 = new MessageResponseDTO(UUID.randomUUID().toString());
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTOWithMultiplePO();
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO =
                new IupdSyncStatusUpdateDTO(InstallmentStatus.UNPAID);

        Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap = new HashMap<>();
        iupdSyncStatusUpdateDTOMap.put("iud", iupdSyncStatusUpdateDTO);
        iupdSyncStatusUpdateDTOMap.put("iud2", iupdSyncStatusUpdateDTO);

        GenericWfExecutionConfig.IONotificationBaseOpsMessages ioMessages = new GenericWfExecutionConfig.IONotificationBaseOpsMessages();

        when(debtPositionOperationTypeResolverMock.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap))
                .thenReturn(DP_CREATED);

        when(baseOpsMessagesResolverServiceMock.resolveIoMessages(debtPositionDTO, DP_CREATED, ioMessages))
                .thenReturn(ioNotificationDTO);

        when(notificationRequestMapperMock.map(debtPositionDTO, ioNotificationDTO))
                .thenReturn(List.of(notificationRequestDTO1, notificationRequestDTO2, notificationRequestDTO3));

        when(ioNotificationFacadeServiceMock.sendMessage(any()))
                .thenReturn(messageResult1)
                .thenReturn(messageResult2)
                .thenReturn(messageResult3);

        // When
        DebtPositionIoNotificationDTO result = service.sendMessage(debtPositionDTO, iupdSyncStatusUpdateDTOMap, ioMessages);

        // Then
        assertNotNull(result);
        TestUtils.checkNotNullFields(result);
        assertEquals(debtPositionDTO.getDebtPositionId(), result.getDebtPositionId());
        assertEquals(debtPositionDTO.getDebtPositionTypeOrgId(), result.getDebtPositionTypeOrgId());
        assertEquals(debtPositionDTO.getOrganizationId(), result.getOrganizationId());
        assertEquals(3, result.getMessages().size());

        assertIoMessage(result.getMessages().getFirst(), notificationRequestDTO1, messageResult1);
        assertIoMessage(result.getMessages().get(1), notificationRequestDTO2, messageResult2);
        assertIoMessage(result.getMessages().get(2), notificationRequestDTO3, messageResult3);
    }

    private void assertIoMessage(DebtPositionIoNotificationDTO.IoMessage message, NotificationRequestDTO request, MessageResponseDTO result) {
        Assertions.assertSame(request.getServiceId(), message.getServiceId());
        Assertions.assertSame(request.getNav(), message.getNav());
        Assertions.assertSame(result.getNotificationId(), message.getNotificationId());
        TestUtils.checkNotNullFields(message);
    }

    @Test
    void givenSendMessageWhenPaymentEventTypeNullThenReturnMessageResponseEmpty() {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTOWithMultiplePO();
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO =
                new IupdSyncStatusUpdateDTO(InstallmentStatus.UNPAID);

        Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap = new HashMap<>();
        iupdSyncStatusUpdateDTOMap.put("iud", iupdSyncStatusUpdateDTO);
        iupdSyncStatusUpdateDTOMap.put("iud2", iupdSyncStatusUpdateDTO);

        GenericWfExecutionConfig.IONotificationBaseOpsMessages ioMessages = new GenericWfExecutionConfig.IONotificationBaseOpsMessages();

        when(debtPositionOperationTypeResolverMock.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap))
                .thenReturn(null);

        // When
        DebtPositionIoNotificationDTO result = service.sendMessage(debtPositionDTO, iupdSyncStatusUpdateDTOMap, ioMessages);

        // Then
        assertNull(result);
    }

    @Test
    void givenSendMessageWhenNotificationDetailsNullThenReturnMessageResponseEmpty() {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTOWithMultiplePO();
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO =
                new IupdSyncStatusUpdateDTO(InstallmentStatus.UNPAID);

        Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap = new HashMap<>();
        iupdSyncStatusUpdateDTOMap.put("iud", iupdSyncStatusUpdateDTO);
        iupdSyncStatusUpdateDTOMap.put("iud2", iupdSyncStatusUpdateDTO);

        GenericWfExecutionConfig.IONotificationBaseOpsMessages ioMessages = new GenericWfExecutionConfig.IONotificationBaseOpsMessages();

        when(debtPositionOperationTypeResolverMock.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap))
                .thenReturn(DP_CREATED);

        when(baseOpsMessagesResolverServiceMock.resolveIoMessages(debtPositionDTO, DP_CREATED, ioMessages))
                .thenReturn(null);

        when(debtPositionTypeOrgServiceMock.getDefaultIONotificationDetails(debtPositionDTO.getDebtPositionTypeOrgId(), DP_CREATED))
                .thenReturn(null);

        // When
        DebtPositionIoNotificationDTO result = service.sendMessage(debtPositionDTO, iupdSyncStatusUpdateDTOMap, ioMessages);

        // Then
        assertNull(result);
    }
}
