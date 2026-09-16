package it.gov.pagopa.payhub.activities.service.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ResolvedInstallmentResult;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiptInstallmentResolverServiceTest {

    @Mock
    private OrganizationService organizationServiceMock;
    @Mock
    private InstallmentService installmentServiceMock;
    @Mock
    private DebtPositionTypeOrgService debtPositionTypeOrgServiceMock;

    @InjectMocks
    private ReceiptInstallmentResolverService service;

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                organizationServiceMock,
                installmentServiceMock,
                debtPositionTypeOrgServiceMock
        );
    }

    @Test
    void givenUnknownOrgFiscalCodeWhenResolveInstallmentThenReturnEmpty() {
        // Given
        ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("UNKNOWN_11111111111");

        // When
        ResolvedInstallmentResult result = service.resolveInstallment(receiptDTO);

        // Then
        Assertions.assertTrue(result.isEmpty());
        Mockito.verifyNoInteractions(organizationServiceMock, installmentServiceMock, debtPositionTypeOrgServiceMock);
    }

    @Test
    void givenOrganizationNotFoundWhenResolveInstallmentThenReturnEmpty() {
        // Given
        ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("FISCALCODE");

        when(organizationServiceMock.getOrganizationById(1L)).thenReturn(Optional.empty());

        // When
        ResolvedInstallmentResult result = service.resolveInstallment(receiptDTO);

        // Then
        Assertions.assertTrue(result.isEmpty());
        verify(organizationServiceMock).getOrganizationById(1L);
        Mockito.verifyNoInteractions(installmentServiceMock, debtPositionTypeOrgServiceMock);
    }

    @Test
    void givenNoInstallmentsWhenResolveInstallmentThenReturnEmptyNotifiableAndNullInstallment() {
        // Given
        ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("FISCALCODE");
        Organization organization = buildOrganization();

        when(organizationServiceMock.getOrganizationById(1L)).thenReturn(Optional.of(organization));
        when(installmentServiceMock.getByOrganizationIdAndReceiptId(1L, 99L, null)).thenReturn(List.of());

        // When
        ResolvedInstallmentResult result = service.resolveInstallment(receiptDTO);

        // Then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertNull(result.getCitizenNotifiableInstallment());
        Assertions.assertTrue(result.getSilNotifiableInstallments().isEmpty());
        verify(organizationServiceMock).getOrganizationById(1L);
        verify(installmentServiceMock).getByOrganizationIdAndReceiptId(1L, 99L, null);
    }

    @Test
    void givenSingleNotifiableInstallmentWhenResolveInstallmentThenReturnIt() {
        // Given
        ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("FISCALCODE");
        Organization organization = buildOrganization();
        InstallmentDTO installment = buildInstallment(10L);

        DebtPositionTypeOrg dptOrg = buildDebtPositionTypeOrg(1L, "CODE", 1L, 5L);

        when(organizationServiceMock.getOrganizationById(1L)).thenReturn(Optional.of(organization));
        when(installmentServiceMock.getByOrganizationIdAndReceiptId(1L, 99L, null)).thenReturn(List.of(installment));
        when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByInstallmentId(10L)).thenReturn(dptOrg);

        // When
        ResolvedInstallmentResult result = service.resolveInstallment(receiptDTO);

        // Then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(installment, result.getCitizenNotifiableInstallment());
        Assertions.assertEquals(1, result.getSilNotifiableInstallments().size());
        Assertions.assertEquals(installment, result.getSilNotifiableInstallments().getFirst().getInstallment());
        Assertions.assertEquals(dptOrg, result.getSilNotifiableInstallments().getFirst().getDebtPositionTypeOrg());
    }

    @Test
    void givenMixedInstallmentWhenResolveInstallmentThenMixedHasPriority() {
        // Given
        ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("FISCALCODE");
        Organization organization = buildOrganization();

        InstallmentDTO normalInstallment = buildInstallment(10L);
        InstallmentDTO mixedInstallment = buildInstallment(20L);

        DebtPositionTypeOrg normalDptOrg = buildDebtPositionTypeOrg(1L, "CODE", 1L, 5L);
        DebtPositionTypeOrg mixedDptOrg = buildDebtPositionTypeOrg(2L, "MIXED", -2L, 5L);

        when(organizationServiceMock.getOrganizationById(1L)).thenReturn(Optional.of(organization));
        when(installmentServiceMock.getByOrganizationIdAndReceiptId(1L, 99L, null))
                .thenReturn(List.of(normalInstallment, mixedInstallment));
        when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByInstallmentId(10L)).thenReturn(normalDptOrg);
        when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByInstallmentId(20L)).thenReturn(mixedDptOrg);

        // When
        ResolvedInstallmentResult result = service.resolveInstallment(receiptDTO);

        // Then
        Assertions.assertEquals(mixedInstallment, result.getCitizenNotifiableInstallment());
        Assertions.assertEquals(1, result.getSilNotifiableInstallments().size());
        Assertions.assertEquals(normalInstallment, result.getSilNotifiableInstallments().getFirst().getInstallment());
    }

    @Test
    void givenNullSilServiceIdWhenResolveInstallmentThenInstallmentNotAddedToSilNotifiableButStillResolvedForCitizen() {
        // Given
        ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("FISCALCODE");
        Organization organization = buildOrganization();
        InstallmentDTO installment = buildInstallment(10L);

        DebtPositionTypeOrg dptOrg = buildDebtPositionTypeOrg(1L, "CODE", 1L, null);

        when(organizationServiceMock.getOrganizationById(1L)).thenReturn(Optional.of(organization));
        when(installmentServiceMock.getByOrganizationIdAndReceiptId(1L, 99L, null)).thenReturn(List.of(installment));
        when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByInstallmentId(10L)).thenReturn(dptOrg);

        // When
        ResolvedInstallmentResult result = service.resolveInstallment(receiptDTO);

        // Then
        Assertions.assertEquals(installment, result.getCitizenNotifiableInstallment());
        Assertions.assertTrue(result.getSilNotifiableInstallments().isEmpty());
    }

    @Test
    void givenNegativeDebtPositionTypeIdWhenResolveInstallmentThenInstallmentNotAddedToNotifiable() {
        // Given
        ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("FISCALCODE");
        Organization organization = buildOrganization();
        InstallmentDTO installment = buildInstallment(10L);

        DebtPositionTypeOrg dptOrg = buildDebtPositionTypeOrg(1L, "CODE", -1L, 5L);

        when(organizationServiceMock.getOrganizationById(1L)).thenReturn(Optional.of(organization));
        when(installmentServiceMock.getByOrganizationIdAndReceiptId(1L, 99L, null)).thenReturn(List.of(installment));
        when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByInstallmentId(10L)).thenReturn(dptOrg);

        // When
        ResolvedInstallmentResult result = service.resolveInstallment(receiptDTO);

        // Then
        Assertions.assertNull(result.getCitizenNotifiableInstallment());
        Assertions.assertTrue(result.getSilNotifiableInstallments().isEmpty());
    }

    @Test
    void givenMultipleNotifiableInstallmentsWhenResolveInstallmentThenLastIsResolved() {
        // Given
        ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("FISCALCODE");
        Organization organization = buildOrganization();

        InstallmentDTO first = buildInstallment(10L);
        InstallmentDTO second = buildInstallment(20L);

        DebtPositionTypeOrg dptOrg1 = buildDebtPositionTypeOrg(1L, "CODE1", 1L, 5L);
        DebtPositionTypeOrg dptOrg2 = buildDebtPositionTypeOrg(2L, "CODE2", 2L, 6L);

        when(organizationServiceMock.getOrganizationById(1L)).thenReturn(Optional.of(organization));
        when(installmentServiceMock.getByOrganizationIdAndReceiptId(1L, 99L, null))
                .thenReturn(List.of(first, second));
        when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByInstallmentId(10L)).thenReturn(dptOrg1);
        when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByInstallmentId(20L)).thenReturn(dptOrg2);

        // When
        ResolvedInstallmentResult result = service.resolveInstallment(receiptDTO);

        // Then
        Assertions.assertEquals(second, result.getCitizenNotifiableInstallment());
        Assertions.assertEquals(2, result.getSilNotifiableInstallments().size());
    }

    @Test
    void givenValidReceiptWhenResolveInstallmentThenOrganizationIsReturned() {
        // Given
        ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("FISCALCODE");
        Organization organization = buildOrganization();

        when(organizationServiceMock.getOrganizationById(1L)).thenReturn(Optional.of(organization));
        when(installmentServiceMock.getByOrganizationIdAndReceiptId(1L, 99L, null)).thenReturn(List.of());

        // When
        ResolvedInstallmentResult result = service.resolveInstallment(receiptDTO);

        // Then
        Assertions.assertEquals(organization, result.getOrganization());
    }

    private static ReceiptWithAdditionalNodeDataDTO buildReceipt(String orgFiscalCode) {
        ReceiptWithAdditionalNodeDataDTO dto = new ReceiptWithAdditionalNodeDataDTO();
        dto.setOrgFiscalCode(orgFiscalCode);
        dto.setOrganizationId(1L);
        dto.setReceiptId(99L);
        return dto;
    }

    private static Organization buildOrganization() {
        Organization org = new Organization();
        org.setOrganizationId(1L);
        org.setIpaCode("IPACODE");
        return org;
    }

    private static InstallmentDTO buildInstallment(Long installmentId) {
        InstallmentDTO dto = new InstallmentDTO();
        dto.setInstallmentId(installmentId);
        return dto;
    }

    private static DebtPositionTypeOrg buildDebtPositionTypeOrg(Long id, String code,
                                                                Long debtPositionTypeId, Long silServiceId) {
        DebtPositionTypeOrg dptOrg = new DebtPositionTypeOrg();
        dptOrg.setDebtPositionTypeOrgId(id);
        dptOrg.setCode(code);
        dptOrg.setDebtPositionTypeId(debtPositionTypeId);
        dptOrg.setNotifyOutcomePushOrgSilServiceId(silServiceId);
        return dptOrg;
    }
}
