package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.connector.pu_sil.PuSilService;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ResolvedInstallmentResult;
import it.gov.pagopa.payhub.activities.exception.organization.OrganizationNotFoundException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.receipt.ReceiptInstallmentResolverService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
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

@ExtendWith(MockitoExtension.class)
class ReceiptPagopaNotifySilActivityTest {

  @Mock
  private PuSilService puSilServiceMock;
  @Mock
  private ReceiptInstallmentResolverService receiptInstallmentResolverServiceMock;

  @InjectMocks
  private ReceiptPagopaNotifySilActivityImpl activity;

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
            puSilServiceMock,
            receiptInstallmentResolverServiceMock
    );
  }

  @Test
  void givenValidReceiptWhenNotifyReceiptToSilThenNotifyPaymentCalled() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("FISCALCODE");

    Organization organization = new Organization();
    organization.setFlagNotifyOutcomePush(true);
    organization.setIpaCode("IPACODE");

    InstallmentDTO installment = buildInstallmentDTO();
    DebtPositionTypeOrg debtPositionTypeOrg = new DebtPositionTypeOrg();
    debtPositionTypeOrg.setNotifyOutcomePushOrgSilServiceId(2L);

    List<ResolvedInstallmentResult.NotifiableInstallment> notifiable = List.of(
            new ResolvedInstallmentResult.NotifiableInstallment(debtPositionTypeOrg, installment)
    );
    ResolvedInstallmentResult resolved = new ResolvedInstallmentResult(installment, notifiable, organization);

    Mockito.when(receiptInstallmentResolverServiceMock.resolveInstallment(receiptDTO)).thenReturn(resolved);

    // When
    Assertions.assertDoesNotThrow(() -> activity.notifyReceiptToSil(receiptDTO));

    // Then
    Mockito.verify(receiptInstallmentResolverServiceMock).resolveInstallment(receiptDTO);
    Mockito.verify(puSilServiceMock).notifyPayment(2L, installment, "IPACODE");
  }

  @Test
  void givenOrganizationNotFoundWhenNotifyReceiptToSilThenThrows() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("FISCALCODE");

    Mockito.when(receiptInstallmentResolverServiceMock.resolveInstallment(receiptDTO))
            .thenThrow(new OrganizationNotFoundException("Organization not found"));

    // When Then
    Assertions.assertThrows(OrganizationNotFoundException.class,
            () -> activity.notifyReceiptToSil(receiptDTO));

    Mockito.verify(receiptInstallmentResolverServiceMock).resolveInstallment(receiptDTO);
    Mockito.verifyNoInteractions(puSilServiceMock);
  }

  @Test
  void givenFlagNotifyOutcomePushFalseWhenNotifyReceiptToSilThenNoPaymentNotified() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("FISCALCODE");

    Organization organization = new Organization();
    organization.setFlagNotifyOutcomePush(false);

    ResolvedInstallmentResult resolved = new ResolvedInstallmentResult(null, List.of(), organization);

    Mockito.when(receiptInstallmentResolverServiceMock.resolveInstallment(receiptDTO)).thenReturn(resolved);

    // When
    Assertions.assertDoesNotThrow(() -> activity.notifyReceiptToSil(receiptDTO));

    // Then
    Mockito.verify(receiptInstallmentResolverServiceMock).resolveInstallment(receiptDTO);
    Mockito.verifyNoInteractions(puSilServiceMock);
  }

  @Test
  void givenUnknownOrgFiscalCodeWhenNotifyReceiptToSilThenNoPaymentNotified() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("UNKNOWN_11111111111");

    Mockito.when(receiptInstallmentResolverServiceMock.resolveInstallment(receiptDTO))
            .thenReturn(ResolvedInstallmentResult.empty());

    // When
    Assertions.assertDoesNotThrow(() -> activity.notifyReceiptToSil(receiptDTO));

    // Then
    Mockito.verify(receiptInstallmentResolverServiceMock).resolveInstallment(receiptDTO);
    Mockito.verifyNoInteractions(puSilServiceMock);
  }

  @Test
  void givenNoNotifiableInstallmentsWhenNotifyReceiptToSilThenNoPaymentNotified() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("FISCALCODE");

    Organization organization = new Organization();
    organization.setFlagNotifyOutcomePush(true);

    ResolvedInstallmentResult resolved = new ResolvedInstallmentResult(null, List.of(), organization);

    Mockito.when(receiptInstallmentResolverServiceMock.resolveInstallment(receiptDTO)).thenReturn(resolved);

    // When
    Assertions.assertDoesNotThrow(() -> activity.notifyReceiptToSil(receiptDTO));

    // Then
    Mockito.verify(receiptInstallmentResolverServiceMock).resolveInstallment(receiptDTO);
    Mockito.verifyNoInteractions(puSilServiceMock);
  }

  // --- helpers ---

  private static ReceiptWithAdditionalNodeDataDTO buildReceipt(String orgFiscalCode) {
    ReceiptWithAdditionalNodeDataDTO receiptDTO = new ReceiptWithAdditionalNodeDataDTO();
    receiptDTO.setReceiptId(999L);
    receiptDTO.setOrgFiscalCode(orgFiscalCode);
    return receiptDTO;
  }

  private static InstallmentDTO buildInstallmentDTO() {
    InstallmentDTO installmentDTO = new InstallmentDTO();
    installmentDTO.setInstallmentId(1L);
    return installmentDTO;
  }
}