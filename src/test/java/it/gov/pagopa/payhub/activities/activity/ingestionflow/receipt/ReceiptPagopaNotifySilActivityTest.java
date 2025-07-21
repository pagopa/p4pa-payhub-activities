package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.pu_sil.PuSilService;
import it.gov.pagopa.payhub.activities.exception.organization.OrganizationNotFoundException;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
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

  private ReceiptPagopaNotifySilActivity activity;

  @BeforeEach
  void setUp() {
    activity = new ReceiptPagopaNotifySilActivityImpl(
        organizationServiceMock,
        debtPositionTypeOrgServiceMock,
        puSilServiceMock
    );
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
        organizationServiceMock,
        debtPositionTypeOrgServiceMock,
        puSilServiceMock
    );
  }

  @Test
  void givenValidReceiptAndInstallmentWhenNotifyReceiptToSilThenOk() {
    // Given
    String accessToken = "ACCESSTOKEN";
    ReceiptWithAdditionalNodeDataDTO receiptDTO = new ReceiptWithAdditionalNodeDataDTO();
    receiptDTO.setOrgFiscalCode("FISCALCODE");
    receiptDTO.setDebtPositionTypeOrgCode("DPORGCODE");

    Organization organization = new Organization();
    organization.setFlagNotifyOutcomePush(true);
    organization.setOrganizationId(1L);
    organization.setIpaCode("IPACODE");

    DebtPositionTypeOrg debtPositionTypeOrg = new DebtPositionTypeOrg();
    debtPositionTypeOrg.setNotifyOutcomePushOrgSilServiceId(2L);

    InstallmentDTO installmentDTO = buildInstallmentDTO();

    Mockito.when(organizationServiceMock.getOrganizationByFiscalCode("FISCALCODE")).thenReturn(
        Optional.of(organization));
    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByOrganizationIdAndCode(1L, "DPORGCODE"))
        .thenReturn(debtPositionTypeOrg);


    // When
    Assertions.assertDoesNotThrow(() -> activity.notifyReceiptToSil(receiptDTO, installmentDTO));

    // Then
    Mockito.verify(puSilServiceMock).notifyPayment(2L, installmentDTO, accessToken);
  }

  @Test
  void givenInvalidOrganizationWhenNotifyReceiptToSilThenOrganizationNotFound() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = new ReceiptWithAdditionalNodeDataDTO();
    receiptDTO.setOrgFiscalCode("FISCALCODE");
    InstallmentDTO installmentDTO = buildInstallmentDTO();

    Mockito.when(organizationServiceMock.getOrganizationByFiscalCode("FISCALCODE")).thenReturn(
        Optional.empty());
    // When Then
    Assertions.assertThrows(OrganizationNotFoundException.class, () ->
        activity.notifyReceiptToSil(receiptDTO, installmentDTO));
  }

  @Test
  void givenFlagNotifyOutcomePushFalseWhenNotifyReceiptToSilThenVerifyNoMoreInteractions() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = new ReceiptWithAdditionalNodeDataDTO();
    receiptDTO.setOrgFiscalCode("FISCALCODE");
    receiptDTO.setDebtPositionTypeOrgCode("DPORGCODE");

    Organization organization = new Organization();
    organization.setFlagNotifyOutcomePush(false);

    InstallmentDTO installmentDTO = buildInstallmentDTO();

    Mockito.when(organizationServiceMock.getOrganizationByFiscalCode("FISCALCODE")).thenReturn(
        Optional.of(organization));

    // When
    activity.notifyReceiptToSil(receiptDTO, installmentDTO);

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

    InstallmentDTO installmentDTO = buildInstallmentDTO();

    Mockito.when(organizationServiceMock.getOrganizationByFiscalCode("FISCALCODE")).thenReturn(
        Optional.of(organization));
    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByOrganizationIdAndCode(1L, "DPORGCODE"))
        .thenReturn(debtPositionTypeOrg);
    // When
    activity.notifyReceiptToSil(receiptDTO, installmentDTO);
    // Then
    Mockito.verify(puSilServiceMock, never()).notifyPayment(anyLong(), any(), any());
  }

}