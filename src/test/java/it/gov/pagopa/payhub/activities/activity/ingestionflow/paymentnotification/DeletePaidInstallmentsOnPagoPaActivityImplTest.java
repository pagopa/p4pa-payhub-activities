package it.gov.pagopa.payhub.activities.activity.ingestionflow.paymentnotification;


import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.BrokerService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

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
    private InstallmentService installmentServiceMock;


    private DeletePaidInstallmentsOnPagoPaActivityImpl activity;

    @TempDir
    private Path workingDir;

    @BeforeEach
    void setUp() {
        activity = new DeletePaidInstallmentsOnPagoPaActivityImpl(
                receiptServiceMock,
                organizationServiceMock,
                brokerServiceMock,
                installmentServiceMock
        );
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                receiptServiceMock,
                organizationServiceMock,
                brokerServiceMock,
                installmentServiceMock
        );
    }

    @Test
    void givenOriginTechnicalWhenDeletingThenDoesNothing() {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO(1, 1);
        debtPositionDTO.setDebtPositionOrigin(DebtPositionOrigin.RECEIPT_PAGOPA);

        // When
        activity.deletePaidInstallmentsOnPagoPa(debtPositionDTO, 1L);

        // Then
        verify(receiptServiceMock, never()).getByReceiptId(1L);
    }

    @Test
    void givenFlagPagoPaPaymentFalseWhenDeletingThenDoesNothing() {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO(1, 1);
        debtPositionDTO.setFlagPuPagoPaPayment(false);

        // When
        activity.deletePaidInstallmentsOnPagoPa(debtPositionDTO, 1L);

        // Then
        verify(receiptServiceMock, never()).getByReceiptId(1L);
    }

    @Test
    void givenNoReceiptFoundWhenDeletingThenDoesNothing() {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO(1, 1);

        // When
        when(receiptServiceMock.getByReceiptId(1L)).thenReturn(null);
        activity.deletePaidInstallmentsOnPagoPa(debtPositionDTO, 1L);

        // Then
        verify(receiptServiceMock, times(1)).getByReceiptId(1L);
        verify(organizationServiceMock, never()).getOrganizationById(debtPositionDTO.getOrganizationId());
    }

    @Test
    void givenInvalidReceiptOriginWhenDeletingGpdThenDoesNothing() {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO(1, 1);
        ReceiptDTO receiptDTO = new ReceiptDTO();
        receiptDTO.setReceiptId(1L);
        receiptDTO.setReceiptOrigin(ReceiptOriginType.RECEIPT_FILE);
        Organization organization = new Organization();
        organization.setOrgFiscalCode("123TEST");
        Broker broker = new Broker();
        broker.setGpdKey("hello".getBytes(StandardCharsets.UTF_8));

        // When
        when(receiptServiceMock.getByReceiptId(1L)).thenReturn(receiptDTO);
        when(organizationServiceMock.getOrganizationById(debtPositionDTO.getOrganizationId())).thenReturn(Optional.of(organization));
        when(brokerServiceMock.getBrokerByFiscalCode("123TEST")).thenReturn(broker);
        activity.deletePaidInstallmentsOnPagoPa(debtPositionDTO, 1L);

        // Then
        verify(receiptServiceMock, times(1)).getByReceiptId(1L);
        verify(organizationServiceMock, times(1)).getOrganizationById(debtPositionDTO.getOrganizationId());
        verify(brokerServiceMock, times(1)).getBrokerByFiscalCode("123TEST");
        verify(installmentServiceMock, never()).updateStatusAndSyncStatus(anyLong(), any(), any());
    }

    @Test
    void givenValidReceiptOriginWhenDeletingThenDeletesInstallment() {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO(1, 1);
        ReceiptDTO receiptDTO = new ReceiptDTO();
        receiptDTO.setReceiptId(1L);
        receiptDTO.setReceiptOrigin(ReceiptOriginType.RECEIPT_PAGOPA);
        Organization organization = new Organization();
        organization.setOrgFiscalCode("123TEST");
        Broker broker = new Broker();
        broker.setGpdKey("hello".getBytes(StandardCharsets.UTF_8));

        // When
        when(receiptServiceMock.getByReceiptId(1L)).thenReturn(receiptDTO);
        when(organizationServiceMock.getOrganizationById(debtPositionDTO.getOrganizationId())).thenReturn(Optional.of(organization));
        when(brokerServiceMock.getBrokerByFiscalCode("123TEST")).thenReturn(broker);
        activity.deletePaidInstallmentsOnPagoPa(debtPositionDTO, 1L);

        // Then
        verify(receiptServiceMock, times(1)).getByReceiptId(1L);
        verify(organizationServiceMock, times(1)).getOrganizationById(debtPositionDTO.getOrganizationId());
        verify(brokerServiceMock, times(1)).getBrokerByFiscalCode("123TEST");
        verify(installmentServiceMock, times(1)).updateStatusAndSyncStatus(anyLong(), any(), any());
    }
    
    private DebtPositionDTO buildDebtPositionDTO(long debtPositionId, long receiptId) {
        return DebtPositionDTO.builder()
                .debtPositionId(debtPositionId)
                .iupdOrg("IUPD123")
                .description("Mock debt position")
                .status(DebtPositionStatus.PAID)
                .debtPositionOrigin(DebtPositionOrigin.ORDINARY)
                .organizationId(1001L)
                .debtPositionTypeOrgId(2002L)
                .validityDate(LocalDate.now().plusDays(30))
                .flagIuvVolatile(false)
                .multiDebtor(false)
                .flagPuPagoPaPayment(true)
                .creationDate(OffsetDateTime.now())
                .updateDate(OffsetDateTime.now())
                .updateOperatorExternalId("test-operator")
                .updateTraceId("trace-xyz")
                .paymentOptions(List.of(
                        PaymentOptionDTO.builder()
                                .paymentOptionId(5001L)
                                .debtPositionId(debtPositionId)
                                .totalAmountCents(10000L)
                                .status(PaymentOptionStatus.PAID)
                                .description("Mock payment option")
                                .paymentOptionType(PaymentOptionTypeEnum.INSTALLMENTS)
                                .paymentOptionIndex(1)
                                .creationDate(OffsetDateTime.now())
                                .installments(List.of(
                                        InstallmentDTO.builder()
                                                .installmentId(7001L)
                                                .paymentOptionId(5001L)
                                                .status(InstallmentStatus.PAID)
                                                .syncStatus(InstallmentSyncStatus.builder()
                                                        .syncStatusFrom(InstallmentStatus.UNPAID)
                                                        .syncStatusTo(InstallmentStatus.PAID)
                                                        .build())
                                                .iupdPagopa("IUPD-PAGOPA-123")
                                                .generateNotice(true)
                                                .iuv("IUV-001")
                                                .amountCents(10000L)
                                                .remittanceInformation("Mock installment")
                                                .debtor(new PersonDTO())
                                                .receiptId(receiptId)
                                                .creationDate(OffsetDateTime.now())
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }

}
