package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static it.gov.pagopa.payhub.activities.util.TestUtils.OFFSETDATETIME;
import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionTypeOrgFaker.buildDebtPositionTypeOrgDTO;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
	@Mock
	private OrganizationService organizationServiceMock;
	@Mock
	private IngestionFlowFileService ingestionFlowFileServiceMock;

	private TransferClassificationStoreService service;

	@BeforeEach
	void setUp() {
		service = new TransferClassificationStoreService(
			classificationServiceMock, 
			receiptServiceMock, 
			debtPositionTypeOrgServiceMock,
			organizationServiceMock,
			ingestionFlowFileServiceMock);
	}

	@Test
	void whenSaveAllThenReturnSavedList() {
		// Arrange
		List<ClassificationsEnum> classifications = List.of(ClassificationsEnum.RT_IUF_TES);
		TransferSemanticKeyDTO transferSemanticKeyDTO = TransferSemanticKeyDTO.builder()
			.orgId(1L)
			.iuv("01011112222333345")
			.iur("IUR")
			.transferIndex(1)
			.build();
		ReceiptNoPII receiptNoPII = new ReceiptNoPII()
			.orgFiscalCode("orgFiscalCode")
			.paymentReceiptId("paymentReceiptId")
			.paymentDateTime(OFFSETDATETIME)
			.receiptId(1L)
			.idPsp("idPsp")
			.pspCompanyName("pspCompanyName")
			.personalDataId(1L)
			.outcome("outcome")
			.paymentAmountCents(100L)
			.creditorReferenceId("referenceId");
		DebtPositionTypeOrg debtPositionTypeOrg = buildDebtPositionTypeOrgDTO();
		InstallmentNoPII installmentNoPII = new InstallmentNoPII().balance("balance").ingestionFlowFileId(1L);
		Organization organization = new Organization().orgName("orgName").orgTypeCode("orgTypeCode");
		IngestionFlowFile ingestionFlowFile = new IngestionFlowFile().creationDate(OFFSETDATETIME).fileName("fileName");
		
		when(organizationServiceMock.getOrganizationByFiscalCode(transferDTO.getOrgFiscalCode())).thenReturn(Optional.of(organization));
		when(receiptServiceMock.getByTransferId(transferDTO.getTransferId())).thenReturn(receiptNoPII);
		when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByInstallmentId(transferDTO.getInstallmentId())).thenReturn(debtPositionTypeOrg);
		when(ingestionFlowFileServiceMock.findById(installmentNoPII.getIngestionFlowFileId())).thenReturn(Optional.of(ingestionFlowFile));
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
				.label(classifications.getFirst())
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
				.installmentIngestionFlowFileName(ingestionFlowFile.getFileName())
				.receiptOrgFiscalCode(receiptNoPII.getOrgFiscalCode())
				.receiptPaymentReceiptId(receiptNoPII.getPaymentReceiptId())
				.receiptPaymentDateTime(receiptNoPII.getPaymentDateTime())
				.receiptPaymentRequestId(String.valueOf(receiptNoPII.getReceiptId()))
				.receiptIdPsp(receiptNoPII.getIdPsp())
				.receiptPspCompanyName(receiptNoPII.getPspCompanyName())
				.organizationEntityType(organization.getOrgTypeCode())
				.organizationName(organization.getOrgName())
				.receiptPersonalDataId(receiptNoPII.getPersonalDataId())
				.receiptPaymentOutcomeCode(receiptNoPII.getOutcome())
				.receiptPaymentAmount(receiptNoPII.getPaymentAmountCents())
				.receiptCreditorReferenceId(receiptNoPII.getCreditorReferenceId())
				.transferAmount(transferDTO.getAmountCents())
				.transferCategory(transferDTO.getCategory())
				.receiptCreationDate(receiptNoPII.getCreationDate())
				.installmentBalance(installmentNoPII.getBalance())
				.build());

		when(classificationServiceMock.saveAll(dtoList)).thenReturn(dtoList.size());

		// Act & Assert
		assertEquals(classifications.size(), dtoList.size());
		assertDoesNotThrow(() ->
			service.saveClassifications(transferSemanticKeyDTO, transferDTO, installmentNoPII, paymentsReportingDTO, treasuryDTO, classifications));
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
				.label(classifications.getFirst())
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
			service.saveClassifications(transferSemanticKeyDTO, null, null, paymentsReportingDTO, treasuryDTO, classifications));
	}
}