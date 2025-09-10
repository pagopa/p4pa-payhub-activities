package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.pu_sil.PuSilService;
import it.gov.pagopa.payhub.activities.exception.organization.OrganizationNotFoundException;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceiptPagopaNotifySilActivityTest {

  @Mock
  private OrganizationService organizationServiceMock;
  @Mock
  private DebtPositionTypeOrgService debtPositionTypeOrgServiceMock;
  @Mock
  private PuSilService puSilServiceMock;
  @Mock
  private InstallmentService installmentServiceMock;

  private ReceiptPagopaNotifySilActivity activity;

  @BeforeEach
  void setUp() {
    activity = new ReceiptPagopaNotifySilActivityImpl(
        organizationServiceMock,
        debtPositionTypeOrgServiceMock,
        puSilServiceMock,
        installmentServiceMock
    );
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
        organizationServiceMock,
        debtPositionTypeOrgServiceMock,
        puSilServiceMock,
        installmentServiceMock

    );
  }

  @Test
  void givenValidReceiptAndInstallmentWhenNotifyReceiptToSilThenOk() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = new ReceiptWithAdditionalNodeDataDTO();
    receiptDTO.setReceiptId(999L);
    receiptDTO.setOrgFiscalCode("FISCALCODE");
    receiptDTO.setDebtPositionTypeOrgCode("DPORGCODE");

    Organization organization = new Organization();
    organization.setFlagNotifyOutcomePush(true);
    organization.setOrganizationId(1L);
    organization.setIpaCode("IPACODE");

    DebtPositionTypeOrg debtPositionTypeOrg = new DebtPositionTypeOrg();
    debtPositionTypeOrg.setDebtPositionTypeId(1L);
    debtPositionTypeOrg.setNotifyOutcomePushOrgSilServiceId(2L);

    InstallmentDTO installlmentMixed = buildInstallmentDTO();
    installlmentMixed.setInstallmentId(2L);

    DebtPositionTypeOrg debtPositionTypeOrgMixed = new DebtPositionTypeOrg();
    debtPositionTypeOrgMixed.setDebtPositionTypeId(-2L);
    debtPositionTypeOrgMixed.setNotifyOutcomePushOrgSilServiceId(2L);

    List<InstallmentDTO> installmentDTOs = List.of(buildInstallmentDTO(), installlmentMixed);

    Mockito.when(organizationServiceMock.getOrganizationByFiscalCode("FISCALCODE")).thenReturn(
        Optional.of(organization));
    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByInstallmentId(1L))
        .thenReturn(debtPositionTypeOrg);
    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByInstallmentId(2L))
        .thenReturn(debtPositionTypeOrgMixed);
    Mockito.when(installmentServiceMock.getByOrganizationIdAndReceiptId(1L, receiptDTO.getReceiptId(), null))
        .thenReturn(installmentDTOs);

    // When
    InstallmentDTO result = activity.notifyReceiptToSil(receiptDTO);

    // Then
    Assertions.assertEquals(installmentDTOs.getLast(), result);
    Mockito.verify(puSilServiceMock).notifyPayment(2L, installmentDTOs.getFirst(), "IPACODE");

  }

  @Test
  void givenInvalidOrganizationWhenNotifyReceiptToSilThenOrganizationNotFound() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = new ReceiptWithAdditionalNodeDataDTO();
    receiptDTO.setOrgFiscalCode("FISCALCODE");

    Mockito.when(organizationServiceMock.getOrganizationByFiscalCode("FISCALCODE")).thenReturn(
        Optional.empty());
    // When Then
    Assertions.assertThrows(OrganizationNotFoundException.class, () ->
        activity.notifyReceiptToSil(receiptDTO));
  }

  @Test
  void givenFlagNotifyOutcomePushFalseWhenNotifyReceiptToSilThenVerifyNoMoreInteractions() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = new ReceiptWithAdditionalNodeDataDTO();
    receiptDTO.setOrgFiscalCode("FISCALCODE");
    receiptDTO.setDebtPositionTypeOrgCode("DPORGCODE");

    Organization organization = new Organization();
    organization.setFlagNotifyOutcomePush(false);

    Mockito.when(organizationServiceMock.getOrganizationByFiscalCode("FISCALCODE")).thenReturn(
        Optional.of(organization));

    // When
    InstallmentDTO result = activity.notifyReceiptToSil(receiptDTO);

    // Then
    Assertions.assertNull(result);
    Mockito.verifyNoMoreInteractions(debtPositionTypeOrgServiceMock);
    Mockito.verifyNoMoreInteractions(puSilServiceMock);
  }

  @Test
  void givenServiceIdNullWhenNotifyReceiptToSilThenVerify() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = new ReceiptWithAdditionalNodeDataDTO();
    receiptDTO.setOrgFiscalCode("FISCALCODE");
    receiptDTO.setDebtPositionTypeOrgCode("DPORGCODE");

    Organization organization = new Organization();
    organization.setFlagNotifyOutcomePush(true);
    organization.setOrganizationId(1L);
    organization.setIpaCode("IPACODE");

    DebtPositionTypeOrg debtPositionTypeOrg = new DebtPositionTypeOrg();
    debtPositionTypeOrg.setDebtPositionTypeId(1L);
    debtPositionTypeOrg.setNotifyOutcomePushOrgSilServiceId(null);

    List<InstallmentDTO> installmentDTOs = List.of(buildInstallmentDTO());

    Mockito.when(organizationServiceMock.getOrganizationByFiscalCode("FISCALCODE")).thenReturn(
        Optional.of(organization));
    Mockito.when(installmentServiceMock.getByOrganizationIdAndReceiptId(1L, receiptDTO.getReceiptId(), null))
        .thenReturn(installmentDTOs);
    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByInstallmentId(installmentDTOs.getFirst().getInstallmentId()))
        .thenReturn(debtPositionTypeOrg);

    // When
    InstallmentDTO result = activity.notifyReceiptToSil(receiptDTO);
    // Then
    Assertions.assertNull(result);
    Mockito.verify(puSilServiceMock, never()).notifyPayment(anyLong(), any(), any());
  }

  @Test
  void givenNegativeDebtPositionTypeIdWhenNotifyReceiptToSilThenVerify() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = new ReceiptWithAdditionalNodeDataDTO();
    receiptDTO.setOrgFiscalCode("FISCALCODE");
    receiptDTO.setDebtPositionTypeOrgCode("DPORGCODE");

    Organization organization = new Organization();
    organization.setFlagNotifyOutcomePush(true);
    organization.setOrganizationId(1L);
    organization.setIpaCode("IPACODE");

    DebtPositionTypeOrg debtPositionTypeOrg = new DebtPositionTypeOrg();
    debtPositionTypeOrg.setDebtPositionTypeId(-1L);
    debtPositionTypeOrg.setNotifyOutcomePushOrgSilServiceId(9L);

    List<InstallmentDTO> installmentDTOs = List.of(buildInstallmentDTO());

    Mockito.when(organizationServiceMock.getOrganizationByFiscalCode("FISCALCODE")).thenReturn(
        Optional.of(organization));
    Mockito.when(installmentServiceMock.getByOrganizationIdAndReceiptId(1L, receiptDTO.getReceiptId(), null))
        .thenReturn(installmentDTOs);
    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByInstallmentId(installmentDTOs.getFirst().getInstallmentId()))
        .thenReturn(debtPositionTypeOrg);

    // When
    InstallmentDTO result =activity.notifyReceiptToSil(receiptDTO);
    // Then
    Assertions.assertNull(result);
    Mockito.verify(puSilServiceMock, never()).notifyPayment(anyLong(), any(), any());
  }

}