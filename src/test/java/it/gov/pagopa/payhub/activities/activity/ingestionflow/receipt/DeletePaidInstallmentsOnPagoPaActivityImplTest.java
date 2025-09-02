package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;


import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.BrokerService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.AcaService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.GpdService;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.PagoPaInteractionModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletePaidInstallmentsOnPagoPaActivityImplTest {

    @Mock
    private ReceiptService receiptServiceMock;
    @Mock
    private OrganizationService organizationServiceMock;
    @Mock
    private BrokerService brokerServiceMock;
    @Mock
    private AcaService acaServiceMock;
    @Mock
    private GpdService gpdServiceMock;

    private DeletePaidInstallmentsOnPagoPaActivityImpl activity;


    @BeforeEach
    void setUp() {
        activity = new DeletePaidInstallmentsOnPagoPaActivityImpl(
                receiptServiceMock,
                organizationServiceMock,
                brokerServiceMock,
                acaServiceMock,
                gpdServiceMock
        );
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                receiptServiceMock,
                organizationServiceMock,
                brokerServiceMock,
                acaServiceMock,
                gpdServiceMock
        );
    }

    @Test
    void givenOriginTechnicalWhenDeletingThenDoesNothing() {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.setDebtPositionOrigin(DebtPositionOrigin.RECEIPT_PAGOPA);

        // When
        activity.deletePaidInstallmentsOnPagoPa(debtPositionDTO, 1L);

        // Then
        verify(receiptServiceMock, never()).getByReceiptId(1L);
        verify(organizationServiceMock, never()).getOrganizationById(debtPositionDTO.getOrganizationId());
        verify(brokerServiceMock, never()).getBrokerById(anyLong());
        verify(acaServiceMock, never()).syncInstallmentAca(anyString(), any());
        verify(gpdServiceMock, never()).syncInstallmentGpd(anyString(), any());
    }

    @Test
    void givenFlagPagoPaPaymentFalseWhenDeletingThenDoesNothing() {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.setFlagPuPagoPaPayment(false);

        // When
        activity.deletePaidInstallmentsOnPagoPa(debtPositionDTO, 1L);

        // Then
        verify(receiptServiceMock, never()).getByReceiptId(1L);
        verify(organizationServiceMock, never()).getOrganizationById(debtPositionDTO.getOrganizationId());
        verify(brokerServiceMock, never()).getBrokerById(anyLong());
        verify(acaServiceMock, never()).syncInstallmentAca(anyString(), any());
        verify(gpdServiceMock, never()).syncInstallmentGpd(anyString(), any());
    }

    @Test
    void givenNoReceiptFoundWhenDeletingThenDoesNothing() {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();

        // When
        when(receiptServiceMock.getByReceiptId(1L)).thenReturn(null);
        activity.deletePaidInstallmentsOnPagoPa(debtPositionDTO, 1L);

        // Then
        verify(receiptServiceMock, times(1)).getByReceiptId(1L);
        verify(organizationServiceMock, never()).getOrganizationById(debtPositionDTO.getOrganizationId());
        verify(brokerServiceMock, never()).getBrokerById(anyLong());
        verify(acaServiceMock, never()).syncInstallmentAca(anyString(), any());
        verify(gpdServiceMock, never()).syncInstallmentGpd(anyString(), any());
    }

    @Test
    void givenNoOrganizationFoundWhenDeletingThenDoesNothing() {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        ReceiptDTO receiptDTO = new ReceiptDTO();
        receiptDTO.setReceiptId(1L);

        // When
        when(receiptServiceMock.getByReceiptId(1L)).thenReturn(receiptDTO);
        when(organizationServiceMock.getOrganizationById(debtPositionDTO.getOrganizationId())).thenReturn(Optional.empty());
        activity.deletePaidInstallmentsOnPagoPa(debtPositionDTO, 1L);

        // Then
        verify(receiptServiceMock, times(1)).getByReceiptId(1L);
        verify(organizationServiceMock, times(1)).getOrganizationById(debtPositionDTO.getOrganizationId());
        verify(brokerServiceMock, never()).getBrokerByFiscalCode(anyString());
        verify(acaServiceMock, never()).syncInstallmentAca(anyString(), any());
        verify(gpdServiceMock, never()).syncInstallmentGpd(anyString(), any());
    }

    @Test
    void givenNoBrokerFoundWhenDeletingThenDoesNothing() {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        ReceiptDTO receiptDTO = new ReceiptDTO();
        receiptDTO.setReceiptId(1L);
        Organization organization = new Organization();
        organization.setBrokerId(1L);

        // When
        when(receiptServiceMock.getByReceiptId(1L)).thenReturn(receiptDTO);
        when(organizationServiceMock.getOrganizationById(debtPositionDTO.getOrganizationId())).thenReturn(Optional.of(organization));
        when(brokerServiceMock.getBrokerById(1L)).thenReturn(null);
        activity.deletePaidInstallmentsOnPagoPa(debtPositionDTO, 1L);

        // Then
        verify(receiptServiceMock, times(1)).getByReceiptId(1L);
        verify(organizationServiceMock, times(1)).getOrganizationById(debtPositionDTO.getOrganizationId());
        verify(brokerServiceMock, times(1)).getBrokerById(1L);
        verify(acaServiceMock, never()).syncInstallmentAca(anyString(), any());
        verify(gpdServiceMock, never()).syncInstallmentGpd(anyString(), any());
    }

    @Test
    void givenNoInstallmentWithReceiptIdFoundWhenDeletingThenDoesNothing() {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().setReceiptId(42L);
        ReceiptDTO receiptDTO = new ReceiptDTO();
        receiptDTO.setReceiptId(1L);
        Organization organization = new Organization();
        organization.setBrokerId(1L);

        // When
        when(receiptServiceMock.getByReceiptId(1L)).thenReturn(receiptDTO);
        when(organizationServiceMock.getOrganizationById(debtPositionDTO.getOrganizationId())).thenReturn(Optional.of(organization));
        when(brokerServiceMock.getBrokerById(1L)).thenReturn(null);
        activity.deletePaidInstallmentsOnPagoPa(debtPositionDTO, 1L);

        // Then
        verify(receiptServiceMock, times(1)).getByReceiptId(1L);
        verify(organizationServiceMock, times(1)).getOrganizationById(debtPositionDTO.getOrganizationId());
        verify(brokerServiceMock, times(1)).getBrokerById(1L);
        verify(acaServiceMock, never()).syncInstallmentAca(anyString(), any());
        verify(gpdServiceMock, never()).syncInstallmentGpd(anyString(), any());
    }

    @Test
    void givenNoGpdOrAcaWhenDeletingThenDoesNothing() {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        ReceiptDTO receiptDTO = new ReceiptDTO();
        receiptDTO.setReceiptId(1L);
        Organization organization = new Organization();
        organization.setBrokerId(1L);
        Broker broker = new Broker();

        // When
        when(receiptServiceMock.getByReceiptId(1L)).thenReturn(receiptDTO);
        when(organizationServiceMock.getOrganizationById(debtPositionDTO.getOrganizationId())).thenReturn(Optional.of(organization));
        when(brokerServiceMock.getBrokerById(1L)).thenReturn(broker);
        activity.deletePaidInstallmentsOnPagoPa(debtPositionDTO, 1L);

        // Then
        verify(receiptServiceMock, times(1)).getByReceiptId(1L);
        verify(organizationServiceMock, times(1)).getOrganizationById(debtPositionDTO.getOrganizationId());
        verify(brokerServiceMock, times(1)).getBrokerById(1L);
        verify(acaServiceMock, never()).syncInstallmentAca(anyString(), any());
        verify(gpdServiceMock, never()).syncInstallmentGpd(anyString(), any());
    }

    @Test
    void givenAcaWhenDeletingThenSyncOnACA() {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        ReceiptDTO receiptDTO = new ReceiptDTO();
        receiptDTO.setReceiptId(1L);
        receiptDTO.setReceiptOrigin(ReceiptOriginType.RECEIPT_PAGOPA);
        Organization organization = new Organization();
        organization.setBrokerId(1L);
        Broker broker = new Broker();
        broker.setPagoPaInteractionModel(PagoPaInteractionModel.SYNC_ACA);

        InstallmentDTO installmentDTO = debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst();
        installmentDTO.setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.PAID).syncStatusTo(InstallmentStatus.CANCELLED).build());
        installmentDTO.setStatus(InstallmentStatus.TO_SYNC);
        installmentDTO.setReceiptId(1L);

        // When
        when(receiptServiceMock.getByReceiptId(1L)).thenReturn(receiptDTO);
        when(organizationServiceMock.getOrganizationById(debtPositionDTO.getOrganizationId())).thenReturn(Optional.of(organization));
        when(brokerServiceMock.getBrokerById(1L)).thenReturn(broker);
        doNothing().when(acaServiceMock).syncInstallmentAca(installmentDTO.getIud(), debtPositionDTO);
        activity.deletePaidInstallmentsOnPagoPa(debtPositionDTO, 1L);

        // Then
        verify(receiptServiceMock, times(1)).getByReceiptId(1L);
        verify(organizationServiceMock, times(1)).getOrganizationById(debtPositionDTO.getOrganizationId());
        verify(brokerServiceMock, times(1)).getBrokerById(1L);
        verify(acaServiceMock, times(1)).syncInstallmentAca(anyString(), any());
        verify(gpdServiceMock, never()).syncInstallmentGpd(anyString(), any());
    }

    @Test
    void givenAcaWhenDeletingWithExceptionOnAca() {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        ReceiptDTO receiptDTO = new ReceiptDTO();
        receiptDTO.setReceiptId(1L);
        Organization organization = new Organization();
        organization.setBrokerId(1L);
        Broker broker = new Broker();
        broker.setPagoPaInteractionModel(PagoPaInteractionModel.SYNC_ACA);

        InstallmentDTO installmentDTO = debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst();
        installmentDTO.setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.PAID).syncStatusTo(InstallmentStatus.CANCELLED).build());
        installmentDTO.setStatus(InstallmentStatus.TO_SYNC);
        installmentDTO.setReceiptId(1L);

        // When
        when(receiptServiceMock.getByReceiptId(1L)).thenReturn(receiptDTO);
        when(organizationServiceMock.getOrganizationById(debtPositionDTO.getOrganizationId())).thenReturn(Optional.of(organization));
        when(brokerServiceMock.getBrokerById(1L)).thenReturn(broker);
        doThrow(new RuntimeException("Error on ACA")).when(acaServiceMock).syncInstallmentAca(installmentDTO.getIud(), debtPositionDTO);
        activity.deletePaidInstallmentsOnPagoPa(debtPositionDTO, 1L);

        // Then
        verify(receiptServiceMock, times(1)).getByReceiptId(1L);
        verify(organizationServiceMock, times(1)).getOrganizationById(debtPositionDTO.getOrganizationId());
        verify(brokerServiceMock, times(1)).getBrokerById(1L);
        verify(acaServiceMock, times(1)).syncInstallmentAca(anyString(), any());
        verify(gpdServiceMock, never()).syncInstallmentGpd(anyString(), any());
    }

//    @Test
//    void givenGpdWhenDeletingThenSyncOnGPD() {
//        // Given
//        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
//        ReceiptDTO receiptDTO = new ReceiptDTO();
//        receiptDTO.setReceiptId(1L);
//        receiptDTO.setReceiptOrigin(ReceiptOriginType.RECEIPT_FILE);
//        Organization organization = new Organization();
//        organization.setBrokerId(1L);
//        Broker broker = new Broker();
//        broker.setPagoPaInteractionModel(PagoPaInteractionModel.ASYNC_GPD);
//
//        InstallmentDTO installmentDTO = debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst();
//        installmentDTO.setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.PAID).syncStatusTo(InstallmentStatus.CANCELLED).build());
//        installmentDTO.setStatus(InstallmentStatus.TO_SYNC);
//        installmentDTO.setReceiptId(1L);
//
//        // When
//        when(receiptServiceMock.getByReceiptId(1L)).thenReturn(receiptDTO);
//        when(organizationServiceMock.getOrganizationById(debtPositionDTO.getOrganizationId())).thenReturn(Optional.of(organization));
//        when(brokerServiceMock.getBrokerById(1L)).thenReturn(broker);
//        when(gpdServiceMock.syncInstallmentGpd(installmentDTO.getIud(), debtPositionDTO)).thenReturn("ok");
//        activity.deletePaidInstallmentsOnPagoPa(debtPositionDTO, 1L);
//
//        // Then
//        verify(receiptServiceMock, times(1)).getByReceiptId(1L);
//        verify(organizationServiceMock, times(1)).getOrganizationById(debtPositionDTO.getOrganizationId());
//        verify(brokerServiceMock, times(1)).getBrokerById(1L);
//        verify(acaServiceMock, never()).syncInstallmentAca(anyString(), any());
//        verify(gpdServiceMock, times(1)).syncInstallmentGpd(anyString(), any());
//    }
//
//    @Test
//    void givenGpdWhenDeletingWithExceptionOnGpd() {
//        // Given
//        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
//        ReceiptDTO receiptDTO = new ReceiptDTO();
//        receiptDTO.setReceiptId(1L);
//        receiptDTO.setReceiptOrigin(ReceiptOriginType.RECEIPT_FILE);
//        Organization organization = new Organization();
//        organization.setBrokerId(1L);
//        Broker broker = new Broker();
//        broker.setPagoPaInteractionModel(PagoPaInteractionModel.ASYNC_GPD);
//
//        InstallmentDTO installmentDTO = debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst();
//        installmentDTO.setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.PAID).syncStatusTo(InstallmentStatus.CANCELLED).build());
//        installmentDTO.setStatus(InstallmentStatus.TO_SYNC);
//        installmentDTO.setReceiptId(1L);
//
//        // When
//        when(receiptServiceMock.getByReceiptId(1L)).thenReturn(receiptDTO);
//        when(organizationServiceMock.getOrganizationById(debtPositionDTO.getOrganizationId())).thenReturn(Optional.of(organization));
//        when(brokerServiceMock.getBrokerById(1L)).thenReturn(broker);
//        when(gpdServiceMock.syncInstallmentGpd(installmentDTO.getIud(), debtPositionDTO)).thenThrow(new RuntimeException("Error on GPD"));
//        activity.deletePaidInstallmentsOnPagoPa(debtPositionDTO, 1L);
//
//        // Then
//        verify(receiptServiceMock, times(1)).getByReceiptId(1L);
//        verify(organizationServiceMock, times(1)).getOrganizationById(debtPositionDTO.getOrganizationId());
//        verify(brokerServiceMock, times(1)).getBrokerById(1L);
//        verify(acaServiceMock, never()).syncInstallmentAca(anyString(), any());
//        verify(gpdServiceMock, times(1)).syncInstallmentGpd(anyString(), any());
//    }
//
//    @Test
//    void givenGpdWithOriginReceiptPagopaWhenDeletingThenDoesNothing() {
//        // Given
//        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
//        ReceiptDTO receiptDTO = new ReceiptDTO();
//        receiptDTO.setReceiptId(1L);
//        receiptDTO.setReceiptOrigin(ReceiptOriginType.RECEIPT_PAGOPA);
//        Organization organization = new Organization();
//        organization.setBrokerId(1L);
//        Broker broker = new Broker();
//        broker.setPagoPaInteractionModel(PagoPaInteractionModel.ASYNC_GPD);
//
//        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().setReceiptId(1L);
//
//        // When
//        when(receiptServiceMock.getByReceiptId(1L)).thenReturn(receiptDTO);
//        when(organizationServiceMock.getOrganizationById(debtPositionDTO.getOrganizationId())).thenReturn(Optional.of(organization));
//        when(brokerServiceMock.getBrokerById(1L)).thenReturn(broker);
//        activity.deletePaidInstallmentsOnPagoPa(debtPositionDTO, 1L);
//
//        // Then
//        verify(receiptServiceMock, times(1)).getByReceiptId(1L);
//        verify(organizationServiceMock, times(1)).getOrganizationById(debtPositionDTO.getOrganizationId());
//        verify(brokerServiceMock, times(1)).getBrokerById(1L);
//        verify(acaServiceMock, never()).syncInstallmentAca(anyString(), any());
//        verify(gpdServiceMock, never()).syncInstallmentGpd(anyString(), any());
//    }

}
