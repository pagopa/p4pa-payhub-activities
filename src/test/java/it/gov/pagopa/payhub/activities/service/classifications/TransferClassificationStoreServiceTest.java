package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferClassificationStoreServiceTest {
	private final PaymentsReporting paymentsReportingDTO = PaymentsReportingFaker.buildPaymentsReporting();
	private final Transfer transferDTO = TransferFaker.buildTransfer();
	private final Treasury treasuryDTO = TreasuryFaker.buildTreasuryDTO();

	@Mock
	private ClassificationService classificationServiceMock;
	@Mock
	private ReceiptService receiptServiceMock;
	@Mock
	private DebtPositionTypeOrgService debtPositionTypeOrgServiceMock;

	private TransferClassificationStoreService service;

	@BeforeEach
	void setUp() {
		service = new TransferClassificationStoreService(classificationServiceMock, receiptServiceMock, debtPositionTypeOrgServiceMock);
	}

	@Test
	void whenSaveAllThenReturnSavedList() {
		// Arrange
		List<ClassificationsEnum> classifications = List.of(ClassificationsEnum.RT_IUF_TES);
		TransferSemanticKeyDTO transferSemanticKeyDTO = TransferSemanticKeyDTO.builder()
			.orgId(123L)
			.iuv("01011112222333345")
			.iur("IUR")
			.transferIndex(1)
			.build();
		ReceiptNoPII receiptNoPII = mock(ReceiptNoPII.class);
		DebtPositionTypeOrg debtPositionTypeOrg = mock(DebtPositionTypeOrg.class);

		when(receiptServiceMock.getByTransferId(transferDTO.getTransferId())).thenReturn(receiptNoPII);
		when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByInstallmentId(transferDTO.getInstallmentId())).thenReturn(debtPositionTypeOrg);

		List<Classification> dtoList = List.of(
			Classification.builder()
				.organizationId(transferSemanticKeyDTO.getOrgId())
				.transferId(transferDTO.getTransferId())
				.paymentsReportingId(paymentsReportingDTO.getPaymentsReportingId())
				.treasuryId(treasuryDTO.getTreasuryId())
				.iuf(paymentsReportingDTO.getIuf())
				.iuv(transferSemanticKeyDTO.getIuv())
				.iur(transferSemanticKeyDTO.getIur())
				.transferIndex(transferSemanticKeyDTO.getTransferIndex())
				.label(classifications.getFirst().name())
				.lastClassificationDate(LocalDate.now())
				.payDate(paymentsReportingDTO.getPayDate())
				.paymentDateTime(receiptNoPII.getPaymentDateTime())
				.regulationDate(paymentsReportingDTO.getRegulationDate())
				.billDate(treasuryDTO.getBillDate())
				.regionValueDate(treasuryDTO.getRegionValueDate())
				.pspCompanyName(receiptNoPII.getPspCompanyName())
				.pspLastName(treasuryDTO.getPspLastName())
				.regulationUniqueIdentifier(paymentsReportingDTO.getRegulationUniqueIdentifier())
				.accountRegistryCode(treasuryDTO.getAccountRegistryCode())
				.billAmountCents(treasuryDTO.getBillAmountCents())
				.remittanceInformation(transferDTO.getRemittanceInformation())
				.debtPositionTypeOrgCode(debtPositionTypeOrg.getCode())
				.build());

		when(classificationServiceMock.saveAll(dtoList)).thenReturn(dtoList.size());

		// Act & Assert
		assertEquals(classifications.size(), dtoList.size());
		assertDoesNotThrow(() ->
			service.saveClassifications(transferSemanticKeyDTO, transferDTO, paymentsReportingDTO, treasuryDTO, classifications));
	}

	@Test
	void givenNullTransferDTOWhenSaveAllThenReturnSavedList() {
		// Arrange
		List<ClassificationsEnum> classifications = List.of(ClassificationsEnum.RT_IUF_TES);
		TransferSemanticKeyDTO transferSemanticKeyDTO = TransferSemanticKeyDTO.builder()
			.orgId(123L)
			.iuv("01011112222333345")
			.iur("IUR")
			.transferIndex(1)
			.build();

		List<Classification> dtoList = List.of(
			Classification.builder()
				.organizationId(transferSemanticKeyDTO.getOrgId())
				.transferId(null)
				.paymentsReportingId(paymentsReportingDTO.getPaymentsReportingId())
				.treasuryId(treasuryDTO.getTreasuryId())
				.iuf(paymentsReportingDTO.getIuf())
				.iuv(transferSemanticKeyDTO.getIuv())
				.iur(transferSemanticKeyDTO.getIur())
				.transferIndex(transferSemanticKeyDTO.getTransferIndex())
				.label(classifications.getFirst().name())
				.lastClassificationDate(LocalDate.now())
				.payDate(paymentsReportingDTO.getPayDate())
				.paymentDateTime(null)
				.regulationDate(paymentsReportingDTO.getRegulationDate())
				.billDate(treasuryDTO.getBillDate())
				.regionValueDate(treasuryDTO.getRegionValueDate())
				.pspCompanyName(null)
				.pspLastName(treasuryDTO.getPspLastName())
				.regulationUniqueIdentifier(paymentsReportingDTO.getRegulationUniqueIdentifier())
				.accountRegistryCode(treasuryDTO.getAccountRegistryCode())
				.billAmountCents(treasuryDTO.getBillAmountCents())
				.remittanceInformation(null)
				.debtPositionTypeOrgCode(null)
				.build());

		when(classificationServiceMock.saveAll(dtoList)).thenReturn(dtoList.size());

		// Act & Assert
		assertEquals(classifications.size(), dtoList.size());
		assertDoesNotThrow(() ->
			service.saveClassifications(transferSemanticKeyDTO, null, paymentsReportingDTO, treasuryDTO, classifications));
	}
}