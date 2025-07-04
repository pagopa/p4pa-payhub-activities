package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.classification.dto.generated.*;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static it.gov.pagopa.payhub.activities.util.TestUtils.OFFSETDATETIME;
import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionTypeOrgFaker.buildDebtPositionTypeOrgDTO;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferClassificationStoreServiceTest {

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

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

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

        PaymentsReporting paymentsReportingDTO = PaymentsReportingFaker.buildPaymentsReporting();
        Transfer transferDTO = TransferFaker.buildTransfer();
        Treasury treasuryDTO = TreasuryFaker.buildTreasuryDTO();

        TransferSemanticKeyDTO transferSemanticKeyDTO = podamFactory.manufacturePojo(TransferSemanticKeyDTO.class);
        transferSemanticKeyDTO.setOrgId(1L);
        transferSemanticKeyDTO.setIuv("01011112222333345");
        transferSemanticKeyDTO.setIur("IUR");
        transferSemanticKeyDTO.setTransferIndex(1);

        ReceiptNoPII receiptNoPII = podamFactory.manufacturePojo(ReceiptNoPII.class)
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
        InstallmentNoPII installmentNoPII = podamFactory.manufacturePojo(InstallmentNoPII.class).balance("balance").ingestionFlowFileId(1L);
        Organization organization = podamFactory.manufacturePojo(Organization.class).orgName("orgName").orgTypeCode("orgTypeCode");
        IngestionFlowFile ingestionFlowFile = podamFactory.manufacturePojo(IngestionFlowFile.class).creationDate(OFFSETDATETIME).fileName("fileName");
        PaymentNotificationNoPII paymentNotificationNoPII = podamFactory.manufacturePojo(PaymentNotificationNoPII.class);

        when(organizationServiceMock.getOrganizationByFiscalCode(transferDTO.getOrgFiscalCode())).thenReturn(Optional.of(organization));
        when(receiptServiceMock.getByTransferId(transferDTO.getTransferId())).thenReturn(receiptNoPII);
        when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByInstallmentId(transferDTO.getInstallmentId())).thenReturn(debtPositionTypeOrg);
        when(ingestionFlowFileServiceMock.findById(installmentNoPII.getIngestionFlowFileId())).thenReturn(Optional.of(ingestionFlowFile));

        List<Classification> dtoList = List.of(
                Classification.builder()
                        .label(classifications.getFirst())
                        .lastClassificationDate(LocalDate.now())

                        .organizationId(transferSemanticKeyDTO.getOrgId())
                        .iuv(transferSemanticKeyDTO.getIuv())
                        .iur(transferSemanticKeyDTO.getIur())
                        .transferIndex(transferSemanticKeyDTO.getTransferIndex())

                        .debtPositionTypeOrgCode(debtPositionTypeOrg.getCode())

                        .organizationEntityType(organization.getOrgTypeCode())
                        .organizationName(organization.getOrgName())

                        .transferId(transferDTO.getTransferId())
                        .transferAmount(transferDTO.getAmountCents())
                        .transferCategory(transferDTO.getCategory())
                        .remittanceInformation(transferDTO.getRemittanceInformation())

                        .iud(installmentNoPII.getIud())
                        .installmentBalance(installmentNoPII.getBalance())
                        .debtorFiscalCodeHash(installmentNoPII.getDebtorFiscalCodeHash())

                        .installmentIngestionFlowFileName(ingestionFlowFile.getFileName())

                        .pspCompanyName(receiptNoPII.getPspCompanyName())
                        .paymentDateTime(receiptNoPII.getPaymentDateTime())
                        .receiptOrgFiscalCode(receiptNoPII.getOrgFiscalCode())
                        .receiptPaymentReceiptId(receiptNoPII.getPaymentReceiptId())
                        .receiptPaymentDateTime(receiptNoPII.getPaymentDateTime())
                        .receiptPaymentRequestId(String.valueOf(receiptNoPII.getReceiptId()))
                        .receiptIdPsp(receiptNoPII.getIdPsp())
                        .receiptPspCompanyName(receiptNoPII.getPspCompanyName())
                        .receiptPersonalDataId(receiptNoPII.getPersonalDataId())
                        .receiptPaymentOutcomeCode(receiptNoPII.getOutcome())
                        .receiptPaymentAmount(receiptNoPII.getPaymentAmountCents())
                        .receiptCreditorReferenceId(receiptNoPII.getCreditorReferenceId())
                        .receiptCreationDate(receiptNoPII.getCreationDate())

                        .paymentsReportingId(paymentsReportingDTO.getPaymentsReportingId())
                        .iuf(paymentsReportingDTO.getIuf())
                        .payDate(paymentsReportingDTO.getPayDate())
                        .regulationDate(paymentsReportingDTO.getRegulationDate())
                        .regulationUniqueIdentifier(paymentsReportingDTO.getRegulationUniqueIdentifier())

                        .treasuryId(treasuryDTO.getTreasuryId())
                        .billDate(treasuryDTO.getBillDate())
                        .regionValueDate(treasuryDTO.getRegionValueDate())
                        .pspLastName(treasuryDTO.getPspLastName())
                        .accountRegistryCode(treasuryDTO.getAccountRegistryCode())
                        .billAmountCents(treasuryDTO.getBillAmountCents())
                        .billCode(treasuryDTO.getBillCode())
                        .billYear(treasuryDTO.getBillYear())
                        .documentCode(treasuryDTO.getDocumentCode())
                        .documentYear(treasuryDTO.getDocumentYear())
                        .provisionalAe(treasuryDTO.getProvisionalAe())
                        .provisionalCode(treasuryDTO.getProvisionalCode())

                        .paymentNotificationId(paymentNotificationNoPII.getPaymentNotificationId())

                        .build());

        int expectedResult = dtoList.size();
        when(classificationServiceMock.saveAll(dtoList)).thenReturn(expectedResult);

        // Act & Assert
        assertEquals(classifications.size(), expectedResult);
        Integer result = service.saveClassifications(transferSemanticKeyDTO, transferDTO, installmentNoPII, paymentsReportingDTO, treasuryDTO, paymentNotificationNoPII, classifications);

        Assertions.assertEquals(expectedResult, result);
        TestUtils.checkNotNullFields(dtoList.getFirst(),
                "classificationId", "creationDate", "updateDate", "updateTraceId", "updateOperatorExternalId", // tech fields
                "links" // Hateoas
        );
    }

    @Test
    void givenNullTransferDTOWhenSaveAllThenReturnSavedList() {
        // Arrange
        List<ClassificationsEnum> classifications = List.of(ClassificationsEnum.RT_IUF_TES);

        PaymentsReporting paymentsReportingDTO = PaymentsReportingFaker.buildPaymentsReporting();
        Treasury treasuryDTO = TreasuryFaker.buildTreasuryDTO();

        TransferSemanticKeyDTO transferSemanticKeyDTO = TransferSemanticKeyDTO.builder()
                .orgId(123L)
                .iuv("01011112222333345")
                .iur("IUR")
                .transferIndex(1)
                .build();

        Organization organization = podamFactory.manufacturePojo(Organization.class);
        when(organizationServiceMock.getOrganizationById(transferSemanticKeyDTO.getOrgId())).thenReturn(Optional.of(organization));

        List<Classification> dtoList = List.of(
                Classification.builder()
                        .label(classifications.getFirst())
                        .lastClassificationDate(LocalDate.now())

                        .organizationId(transferSemanticKeyDTO.getOrgId())
                        .iuv(transferSemanticKeyDTO.getIuv())
                        .iur(transferSemanticKeyDTO.getIur())
                        .transferIndex(transferSemanticKeyDTO.getTransferIndex())

                        .organizationEntityType(organization.getOrgTypeCode())
                        .organizationName(organization.getOrgName())

                        .paymentsReportingId(paymentsReportingDTO.getPaymentsReportingId())
                        .iuf(paymentsReportingDTO.getIuf())
                        .payDate(paymentsReportingDTO.getPayDate())
                        .regulationDate(paymentsReportingDTO.getRegulationDate())
                        .regulationUniqueIdentifier(paymentsReportingDTO.getRegulationUniqueIdentifier())

                        .treasuryId(treasuryDTO.getTreasuryId())
                        .billDate(treasuryDTO.getBillDate())
                        .regionValueDate(treasuryDTO.getRegionValueDate())
                        .pspLastName(treasuryDTO.getPspLastName())
                        .accountRegistryCode(treasuryDTO.getAccountRegistryCode())
                        .billAmountCents(treasuryDTO.getBillAmountCents())
                        .billCode(treasuryDTO.getBillCode())
                        .billYear(treasuryDTO.getBillYear())
                        .documentCode(treasuryDTO.getDocumentCode())
                        .documentYear(treasuryDTO.getDocumentYear())
                        .provisionalAe(treasuryDTO.getProvisionalAe())
                        .provisionalCode(treasuryDTO.getProvisionalCode())

                        .build());

        when(classificationServiceMock.saveAll(dtoList)).thenReturn(dtoList.size());

        // Act & Assert
        assertEquals(classifications.size(), dtoList.size());
        assertDoesNotThrow(() ->
                service.saveClassifications(transferSemanticKeyDTO, null, null, paymentsReportingDTO, treasuryDTO, null, classifications));

        TestUtils.checkNotNullFields(dtoList.getFirst(),
                "iur", "transferIndex", "transferId", "transferAmount", "transferCategory", "remittanceInformation", "debtPositionTypeOrgCode", // Transfer fields
                "iuv", "iud", "installmentIngestionFlowFileName", "installmentBalance", "debtorFiscalCodeHash", // Installment fields
                "paymentDateTime", "pspCompanyName", "receiptOrgFiscalCode", "receiptPaymentReceiptId", "receiptPaymentRequestId", "receiptIdPsp", "receiptPspCompanyName", "receiptPersonalDataId", "receiptPaymentOutcomeCode", "receiptPaymentAmount", "receiptCreditorReferenceId", "receiptCreationDate", "receiptPaymentDateTime", // Receipt fields
                "classificationId", "creationDate", "updateDate", "updateTraceId", "updateOperatorExternalId", // tech fields
                "paymentNotificationId", // PaymentNotification fields
                "links" // Hateoas
        );
    }

    @Test
    void testSaveIudClassifications() {
        // Arrange
        List<ClassificationsEnum> classifications = List.of(ClassificationsEnum.IUD_NO_RT);
        Organization organization = podamFactory.manufacturePojo(Organization.class).orgName("orgName").orgTypeCode("orgTypeCode");
        PaymentNotificationNoPII paymentNotificationNoPII = podamFactory.manufacturePojo(PaymentNotificationNoPII.class);

        when(organizationServiceMock.getOrganizationById(paymentNotificationNoPII.getOrganizationId())).thenReturn(Optional.of(organization));

        List<Classification> dtoList = List.of(
                Classification.builder()
                        .label(classifications.getFirst())
                        .lastClassificationDate(LocalDate.now())

                        .organizationId(paymentNotificationNoPII.getOrganizationId())
                        .organizationEntityType(organization.getOrgTypeCode())
                        .organizationName(organization.getOrgName())

                        .iuv(paymentNotificationNoPII.getIuv())
                        .iud(paymentNotificationNoPII.getIud())
                        .payDate(paymentNotificationNoPII.getPaymentExecutionDate())
                        .debtPositionTypeOrgCode(paymentNotificationNoPII.getDebtPositionTypeOrgCode())
                        .paymentNotificationId(paymentNotificationNoPII.getPaymentNotificationId())
                        .remittanceInformation(paymentNotificationNoPII.getRemittanceInformation())
                        .installmentBalance(paymentNotificationNoPII.getBalance())
                        .debtorFiscalCodeHash(paymentNotificationNoPII.getDebtorFiscalCodeHash())

                        .build());

        int expectedResult = dtoList.size();
        when(classificationServiceMock.saveAll(dtoList)).thenReturn(expectedResult);

        // Act & Assert
        assertEquals(classifications.size(), expectedResult);
        Integer result = service.saveIudClassifications(paymentNotificationNoPII, classifications);

        Assertions.assertEquals(expectedResult, result);
        TestUtils.checkNotNullFields(dtoList.getFirst(),
                "iur", "transferIndex", "transferId", "transferAmount", "transferCategory", // Transfer fields
                "installmentIngestionFlowFileName", // Installment fields
                "paymentsReportingId", "iuf", "regulationDate", "regulationUniqueIdentifier", // PaymentsReporting fields
                "treasuryId", "billDate", "regionValueDate", "accountRegistryCode", "billAmountCents", "pspLastName", "billCode", "billYear", "documentCode", "documentYear", "provisionalAe", "provisionalCode", // Treasury fields
                "paymentDateTime", "pspCompanyName", "receiptOrgFiscalCode", "receiptPaymentReceiptId", "receiptPaymentRequestId", "receiptIdPsp", "receiptPspCompanyName", "receiptPersonalDataId", "receiptPaymentOutcomeCode", "receiptPaymentAmount", "receiptCreditorReferenceId", "receiptCreationDate", "receiptPaymentDateTime", // Receipt fields
                "classificationId", "creationDate", "updateDate", "updateTraceId", "updateOperatorExternalId", // tech fields
                "links" // Hateoas
        );
    }

    @Test
    void testSaveIufClassifications() {
        // Arrange
        List<ClassificationsEnum> classifications = List.of(ClassificationsEnum.IUD_NO_RT);
        Organization organization = podamFactory.manufacturePojo(Organization.class).orgName("orgName").orgTypeCode("orgTypeCode");
        Treasury treasury = podamFactory.manufacturePojo(Treasury.class);

        when(organizationServiceMock.getOrganizationById(treasury.getOrganizationId())).thenReturn(Optional.of(organization));

        List<Classification> dtoList = List.of(
                Classification.builder()
                        .label(classifications.getFirst())
                        .lastClassificationDate(LocalDate.now())

                        .organizationId(treasury.getOrganizationId())
                        .organizationEntityType(organization.getOrgTypeCode())
                        .organizationName(organization.getOrgName())

                        .iuf(treasury.getIuf())
                        .treasuryId(treasury.getTreasuryId())
                        .billDate(treasury.getBillDate())
                        .regionValueDate(treasury.getRegionValueDate())
                        .pspLastName(treasury.getPspLastName())
                        .accountRegistryCode(treasury.getAccountRegistryCode())
                        .billAmountCents(treasury.getBillAmountCents())
                        .billCode(treasury.getBillCode())
                        .billYear(treasury.getBillYear())
                        .documentCode(treasury.getDocumentCode())
                        .documentYear(treasury.getDocumentYear())
                        .provisionalAe(treasury.getProvisionalAe())
                        .provisionalCode(treasury.getProvisionalCode())

                        .build());

        int expectedResult = dtoList.size();
        when(classificationServiceMock.saveAll(dtoList)).thenReturn(expectedResult);

        // Act & Assert
        assertEquals(classifications.size(), expectedResult);
        Integer result = service.saveIufClassifications(treasury, classifications);

        Assertions.assertEquals(expectedResult, result);
        TestUtils.checkNotNullFields(dtoList.getFirst(),
                "iur", "transferIndex", "transferId", "transferAmount", "transferCategory", "remittanceInformation", "debtPositionTypeOrgCode", // Transfer fields
                "iuv", "iud", "installmentIngestionFlowFileName", "installmentBalance", "debtorFiscalCodeHash", // Installment fields
                "paymentsReportingId", "regulationDate", "regulationUniqueIdentifier", "payDate", // PaymentsReporting fields
                "paymentDateTime", "pspCompanyName", "receiptOrgFiscalCode", "receiptPaymentReceiptId", "receiptPaymentRequestId", "receiptIdPsp", "receiptPspCompanyName", "receiptPersonalDataId", "receiptPaymentOutcomeCode", "receiptPaymentAmount", "receiptCreditorReferenceId", "receiptCreationDate", "receiptPaymentDateTime", // Receipt fields
                "classificationId", "creationDate", "updateDate", "updateTraceId", "updateOperatorExternalId", // tech fields
                "paymentNotificationId", // PaymentNotification fields
                "links" // Hateoas
        );
    }
}