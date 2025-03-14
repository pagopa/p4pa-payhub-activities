package it.gov.pagopa.payhub.activities.service.debtpositions.ionotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.ionotification.IONotificationFacadeService;
import it.gov.pagopa.payhub.activities.connector.ionotification.mapper.NotificationRequestMapper;
import it.gov.pagopa.payhub.activities.service.debtposition.ionotification.DebtOperationOperationTypeResolver;
import it.gov.pagopa.payhub.activities.service.debtposition.ionotification.IONotificationService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.MessageResponseDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTOWithMultiplePO;
import static it.gov.pagopa.payhub.activities.util.faker.IONotificationDTOFaker.buildIONotificationDTO;
import static it.gov.pagopa.payhub.activities.util.faker.NotificationRequestDTOFaker.buildNotificationRequestDTO;
import static it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO.OperationTypeEnum.CREATE_DP;
import static it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO.OperationTypeEnum.UPDATE_DP;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IONotificationServiceTest {

    @Mock
    private DebtPositionTypeOrgService debtPositionTypeOrgServiceMock;
    @Mock
    private IONotificationFacadeService ioNotificationFacadeServiceMock;
    @Mock
    private NotificationRequestMapper notificationRequestMapperMock;
    @Mock
    private DebtOperationOperationTypeResolver debtOperationOperationTypeResolverMock;

    private IONotificationService service;

    @BeforeEach
    void setUp(){
        service = new IONotificationService(
                debtPositionTypeOrgServiceMock,
                ioNotificationFacadeServiceMock,
                notificationRequestMapperMock,
                debtOperationOperationTypeResolverMock);
    }

    @Test
    void whenSendMessageThenOk(){
        // Given
        IONotificationDTO ioNotificationDTO = buildIONotificationDTO();
        NotificationRequestDTO notificationRequestDTO = buildNotificationRequestDTO();
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTOWithMultiplePO();
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO =
                new IupdSyncStatusUpdateDTO(IupdSyncStatusUpdateDTO.NewStatusEnum.UNPAID, "iupdPagopa");

        Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap = new HashMap<>();
        iupdSyncStatusUpdateDTOMap.put("iud", iupdSyncStatusUpdateDTO);
        iupdSyncStatusUpdateDTOMap.put("iud2", iupdSyncStatusUpdateDTO);

        when(debtOperationOperationTypeResolverMock.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap))
                .thenReturn(CREATE_DP);

        when(debtPositionTypeOrgServiceMock.getIONotificationDetails(debtPositionDTO.getDebtPositionTypeOrgId(), CREATE_DP))
                .thenReturn(ioNotificationDTO);

        when(notificationRequestMapperMock.map(debtPositionDTO, ioNotificationDTO, CREATE_DP))
                .thenReturn(List.of(notificationRequestDTO, notificationRequestDTO, notificationRequestDTO));

        when(ioNotificationFacadeServiceMock.sendMessage(any()))
                .thenAnswer(invocation -> new MessageResponseDTO(UUID.randomUUID().toString()));

        // When
        List<MessageResponseDTO> messageResponseDTOS = service.sendMessage(debtPositionDTO, iupdSyncStatusUpdateDTOMap);

        // Then
        assertNotNull(messageResponseDTOS);
        assertEquals(3, messageResponseDTOS.size());
    }

    @Test
    void givenSendMessageWhenOperationTypeNullThenReturnMessageResponseEmpty(){
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTOWithMultiplePO();
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO =
                new IupdSyncStatusUpdateDTO(IupdSyncStatusUpdateDTO.NewStatusEnum.UNPAID, "iupdPagopa");

        Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap = new HashMap<>();
        iupdSyncStatusUpdateDTOMap.put("iud", iupdSyncStatusUpdateDTO);
        iupdSyncStatusUpdateDTOMap.put("iud2", iupdSyncStatusUpdateDTO);

        when(debtOperationOperationTypeResolverMock.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap))
                .thenReturn(null);

        // When
        List<MessageResponseDTO> messageResponseDTOS = service.sendMessage(debtPositionDTO, iupdSyncStatusUpdateDTOMap);

        // Then
        assertTrue(messageResponseDTOS.isEmpty());
    }

    @Test
    void givenSendMessageWhenOperationTypeNotCreateDpThenReturnMessageResponseEmpty(){
        // Given
        IONotificationDTO ioNotificationDTO = buildIONotificationDTO();
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTOWithMultiplePO();
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO =
                new IupdSyncStatusUpdateDTO(IupdSyncStatusUpdateDTO.NewStatusEnum.UNPAID, "iupdPagopa");

        Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap = new HashMap<>();
        iupdSyncStatusUpdateDTOMap.put("iud", iupdSyncStatusUpdateDTO);
        iupdSyncStatusUpdateDTOMap.put("iud2", iupdSyncStatusUpdateDTO);

        when(debtOperationOperationTypeResolverMock.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap))
                .thenReturn(UPDATE_DP);

        when(debtPositionTypeOrgServiceMock.getIONotificationDetails(debtPositionDTO.getDebtPositionTypeOrgId(), UPDATE_DP))
                .thenReturn(ioNotificationDTO);

        // When
        List<MessageResponseDTO> messageResponseDTOS = service.sendMessage(debtPositionDTO, iupdSyncStatusUpdateDTOMap);

        // Then
        assertTrue(messageResponseDTOS.isEmpty());
    }

    @Test
    void givenSendMessageWhenNotificationDetailsNullThenReturnMessageResponseEmpty(){
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTOWithMultiplePO();
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO =
                new IupdSyncStatusUpdateDTO(IupdSyncStatusUpdateDTO.NewStatusEnum.UNPAID, "iupdPagopa");

        Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap = new HashMap<>();
        iupdSyncStatusUpdateDTOMap.put("iud", iupdSyncStatusUpdateDTO);
        iupdSyncStatusUpdateDTOMap.put("iud2", iupdSyncStatusUpdateDTO);

        when(debtOperationOperationTypeResolverMock.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap))
                .thenReturn(CREATE_DP);

        when(debtPositionTypeOrgServiceMock.getIONotificationDetails(debtPositionDTO.getDebtPositionTypeOrgId(), CREATE_DP))
                .thenReturn(null);

        // When
        List<MessageResponseDTO> messageResponseDTOS = service.sendMessage(debtPositionDTO, iupdSyncStatusUpdateDTOMap);

        // Then
        assertTrue(messageResponseDTOS.isEmpty());
    }
}
