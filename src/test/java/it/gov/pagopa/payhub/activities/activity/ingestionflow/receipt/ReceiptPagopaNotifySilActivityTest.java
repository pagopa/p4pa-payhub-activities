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
    debtPositionTypeOrg.setNotifyOutcomePushOrgSilServiceId(2L);

    List<InstallmentDTO> installmentDTOs = List.of(buildInstallmentDTO());

    Mockito.when(organizationServiceMock.getOrganizationByFiscalCode("FISCALCODE")).thenReturn(
        Optional.of(organization));
    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByOrganizationIdAndCode(1L, "DPORGCODE"))
        .thenReturn(debtPositionTypeOrg);
    Mockito.when(installmentServiceMock.getByOrganizationIdAndReceiptId(1L, receiptDTO.getReceiptId(), null))
        .thenReturn(installmentDTOs);


    // When
    Assertions.assertDoesNotThrow(() -> activity.notifyReceiptToSil(receiptDTO));

    // Then
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
    activity.notifyReceiptToSil(receiptDTO);

    // Then
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
    debtPositionTypeOrg.setNotifyOutcomePushOrgSilServiceId(null);

    Mockito.when(organizationServiceMock.getOrganizationByFiscalCode("FISCALCODE")).thenReturn(
        Optional.of(organization));
    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByOrganizationIdAndCode(1L, "DPORGCODE"))
        .thenReturn(debtPositionTypeOrg);
    // When
    activity.notifyReceiptToSil(receiptDTO);
    // Then
    Mockito.verify(puSilServiceMock, never()).notifyPayment(anyLong(), any(), any());
  }

}