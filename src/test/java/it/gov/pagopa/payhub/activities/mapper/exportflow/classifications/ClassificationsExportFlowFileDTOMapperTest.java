package it.gov.pagopa.payhub.activities.mapper.exportflow.classifications;

import it.gov.pagopa.payhub.activities.dto.exportflow.classifications.ClassificationsExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationViewDTO;
import it.gov.pagopa.pu.classification.dto.generated.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.junit.jupiter.api.Assertions.*;

class ClassificationsExportFlowFileDTOMapperTest {

    private PodamFactory podamFactory;
    ClassificationsExportFlowFileDTOMapper classificationsExportFlowFileDTOMapper;

    @BeforeEach
    void setUp() {
        classificationsExportFlowFileDTOMapper = new ClassificationsExportFlowFileDTOMapper();
        podamFactory = new PodamFactoryImpl();
    }

    @Test
    void givenClassificationViewDTOWhenMapThenReturnClassificationsExportFlowFileDTO() {
        //given
        ClassificationViewDTO classificationViewDTO = podamFactory.manufacturePojo(ClassificationViewDTO.class);
        //when
        ClassificationsExportFlowFileDTO result = classificationsExportFlowFileDTOMapper.map(classificationViewDTO);
        //then
        assertNotNull(result);
        TestUtils.checkNotNullFields(result,
                "objectVersion",
                "requestingStationId",
                "institutionAttOperatingUnitCode",
                "institutionAttOperatingUnitName",
                "institutionAttAddress",
                "institutionAttCivicNumber",
                "institutionAttPostalCode",
                "institutionAttCity",
                "institutionAttProvince",
                "institutionAttCountry",
                "beneficiaryOperatingUnitCode",
                "beneficiaryOperatingUnitName",
                "beneficiaryAddress",
                "beneficiaryCivicNumber",
                "beneficiaryPostalCode",
                "beneficiaryCity",
                "beneficiaryProvince",
                "beneficiaryCountry",
                "dueTypeCode",
                "objectVersionR",
                "currencyCode",
                "signCode",
                "checkNumber",
                "bankReferenceCode",
                "clientReferenceCode",
                "orderDate",
                "completenessClassification",
                "dueTypeCodePa1",
                "dueTypeDescriptionPa1",
                "taxonomicCodePa1",
                "fiscalCodePa1",
                "namePa1",

                "payNoticeIud",
                "payNoticeIuv",
                "payNoticePaymentExecutionDate",
                "payNoticePaymentType",
                "singlePaymentAmountI",
                "payerUniqueIdTypeI",
                "payerUniqueIdCodeI",
                "payerFullNameI",
                "payerAddressI",
                "payerCivicNumberI",
                "payerPostalCodeI",
                "payerLocationI",
                "payerProvinceI",
                "payerCountryI",
                "payerEmailI",
                "payNoticePaCommission",
                "payNoticeRemittanceInformation",
                "payNoticeTransferCategory",
                "payNoticeDebtPositionTypeOrgCode",
                "payNoticeBalance",
                "acquisitionDateI"
                );
        allFieldsAssertions(classificationViewDTO, result);
    }

    @Test
    void givenClassificationViewDTOWithNullPayerAndDebtorWhenMapThenReturnClassificationsExportFlowFileDTO() {
        //given
        ClassificationViewDTO classificationViewDTO = podamFactory.manufacturePojo(ClassificationViewDTO.class);
        classificationViewDTO.setRecPayer(null);
        classificationViewDTO.setRecDebtor(null);
        //when
        ClassificationsExportFlowFileDTO result = classificationsExportFlowFileDTOMapper.map(classificationViewDTO);
        //then
        assertNotNull(result);
        assertNull(result.getPayerUniqueIdType());
        assertNull(result.getPayerUniqueIdCode());
        assertNull(result.getPayerFullName());
        assertNull(result.getPayerAddress());
        assertNull(result.getPayerCivicNumber());
        assertNull(result.getPayerLocation());
        assertNull(result.getPayerPostalCode());
        assertNull(result.getPayerProvince());
        assertNull(result.getPayerNation());
        assertNull(result.getPayerEmail());

        assertNull(result.getDebtorUniqueIdType());
        assertNull(result.getDebtorUniqueIdCode());
        assertNull(result.getDebtorFullName());
        assertNull(result.getDebtorAddress());
        assertNull(result.getDebtorCivicNumber());
        assertNull(result.getDebtorLocation());
        assertNull(result.getDebtorPostalCode());
        assertNull(result.getDebtorProvince());
        assertNull(result.getDebtorNation());
        assertNull(result.getDebtorEmail());
    }

    public static void allFieldsAssertions(ClassificationViewDTO classificationViewDTO, ClassificationsExportFlowFileDTO result){
        assertEquals(classificationViewDTO.getRecFileName(), result.getRecFileName());
        assertEquals(1, result.getFlowRowNumber());
        assertEquals(classificationViewDTO.getRecIud(), result.getRecIud());
        assertEquals(classificationViewDTO.getRecIuv(), result.getRecIuv());
        assertEquals(classificationViewDTO.getRecOrgFiscalCode(), result.getRecOrgFiscalCode());
        assertEquals(classificationViewDTO.getRecPaymentReceiptId(), result.getRecPaymentReceiptId());
        assertEquals(classificationViewDTO.getRecPaymentDateTime(), result.getRecPaymentDateTime());
        assertEquals(classificationViewDTO.getRecPaymentReceiptId(), result.getRequestMessageReferenceId());
        assertEquals(classificationViewDTO.getRecPaymentDateTime(), result.getRequestReferenceDate());
        assertEquals("B", result.getInstitutionAttTypeUniqueId());
        assertEquals(classificationViewDTO.getRecIdPsp(), result.getRecPspId());
        assertEquals(classificationViewDTO.getRecPspCompanyName(), result.getRecPspCompanyName());
        assertEquals("G", result.getBeneficiaryUniqueIdType());
        assertEquals(classificationViewDTO.getRecOrgFiscalCode(), result.getBeneficiaryUniqueIdCode());
        assertEquals(classificationViewDTO.getRecBeneficiaryOrgName(), result.getRecBeneficiaryName());

        Person payer = classificationViewDTO.getRecPayer();
        assertEquals(payer.getEntityType(), result.getPayerUniqueIdType());
        assertEquals(payer.getEntityType(), result.getPayerUniqueIdCode());
        assertEquals(payer.getFullName(), result.getPayerFullName());
        assertEquals(payer.getAddress(), result.getPayerAddress());
        assertEquals(payer.getCivic(), result.getPayerCivicNumber());
        assertEquals(payer.getPostalCode(), result.getPayerPostalCode());
        assertEquals(payer.getLocation(), result.getPayerLocation());
        assertEquals(payer.getProvince(), result.getPayerProvince());
        assertEquals(payer.getNation(), result.getPayerNation());
        assertEquals(payer.getEmail(), result.getPayerEmail());

        Person debtor = classificationViewDTO.getRecDebtor();
        assertEquals(debtor.getEntityType(), result.getDebtorUniqueIdType());
        assertEquals(debtor.getEntityType(), result.getDebtorUniqueIdCode());
        assertEquals(debtor.getFullName(), result.getDebtorFullName());
        assertEquals(debtor.getAddress(), result.getDebtorAddress());
        assertEquals(debtor.getCivic(), result.getDebtorCivicNumber());
        assertEquals(debtor.getPostalCode(), result.getDebtorPostalCode());
        assertEquals(debtor.getLocation(), result.getDebtorLocation());
        assertEquals(debtor.getProvince(), result.getDebtorProvince());
        assertEquals(debtor.getNation(), result.getDebtorNation());
        assertEquals(debtor.getEmail(), result.getDebtorEmail());

        assertEquals("0", result.getPaymentOutcomeCode());
        assertEquals(Utilities.longCentsToBigDecimalEuro(classificationViewDTO.getRecPaymentAmount()), result.getRecPaymentAmount());
        assertEquals(classificationViewDTO.getRecCreditorReferenceId(), result.getUniquePaymentId());
        assertEquals(classificationViewDTO.getRecPaymentReceiptId(), result.getPaymentContextCode());
        assertEquals(Utilities.longCentsToBigDecimalEuro(classificationViewDTO.getRecTransferAmount()), result.getRecTransferAmount());
        assertEquals("0", result.getSinglePaymentOutcomeE());
        assertEquals(classificationViewDTO.getRecPaymentDateTime(), result.getSinglePaymentOutcomeDateE());
        assertEquals(classificationViewDTO.getRecPaymentReceiptId(), result.getUniqueCollectionIdE());
        assertEquals(classificationViewDTO.getRecTransferRemittanceInformation(), result.getRecTransferRemittanceInformation());
        assertEquals("9/" + classificationViewDTO.getRecTransferCategory(), result.getRecTransferCategory());
        assertEquals(classificationViewDTO.getRecCreationDate(), result.getRecCreationDate());
        assertEquals(classificationViewDTO.getRecInstallmentBalance(), result.getRecInstallmentBalance());
        assertEquals(classificationViewDTO.getPayRepIuf(), result.getPayRepiuf());
        assertEquals(classificationViewDTO.getPayRepFlowDateTime(), result.getPayRepFlowDateTime());
        assertEquals(classificationViewDTO.getPayRepRegulationUniqueIdentifier(), result.getUniqueRegulationCodeR());
        assertEquals(classificationViewDTO.getPayRepRegulationDate(), result.getRegulationDateR());
        assertEquals(classificationViewDTO.getPayRepSenderPspType(), result.getSenderInstitutionUniqueIdType());
        assertEquals(classificationViewDTO.getPayRepSenderPspCode(), result.getSenderInstitutionUniqueId());
        assertEquals(classificationViewDTO.getPayRepSenderPspName(), result.getSenderInstitutionName());
        assertEquals(classificationViewDTO.getPayRepReceiverOrganizationType(), result.getReceiverInstitutionUniqueIdType());
        assertEquals(classificationViewDTO.getPayRepReceiverOrganizationCode(), result.getReceiverInstitutionUniqueId());
        assertEquals(classificationViewDTO.getPayRepReceiverOrganizationName(), result.getReceiverInstitutionName());
        assertEquals(classificationViewDTO.getPayRepTotalPayments(), result.getTotalPaymentsNumberR());
        assertEquals(Utilities.longCentsToBigDecimalEuro(classificationViewDTO.getPayRepTotalAmountCents()), result.getTotalPaymentsAmountR());
        assertEquals(classificationViewDTO.getPayRepIuv(), result.getPayRepIuv());
        assertEquals(classificationViewDTO.getPayRepIur(), result.getPayRepIur());
        assertEquals(Utilities.longCentsToBigDecimalEuro(classificationViewDTO.getPayRepAmountPaidCents()), result.getSinglePaymentAmountR());
        assertEquals(classificationViewDTO.getPayRepPaymentOutcomeCode(), result.getSinglePaymentOutcomeCodeR());
        assertEquals(classificationViewDTO.getPayRepPayDate(), result.getSinglePaymentOutcomeDateR());
        assertEquals(classificationViewDTO.getRecCreationDate(), result.getAcquisitionDateR());
        assertEquals(classificationViewDTO.getTresAbiCode(), result.getTresAbiCode());
        assertEquals(classificationViewDTO.getTresCabCode(), result.getTresCabCode());
        assertEquals(classificationViewDTO.getTresAccountRegistryCode(), result.getTresAccountRegistryCode());
        assertEquals(classificationViewDTO.getTresBillDate(), result.getTresBillDate());
        assertEquals(classificationViewDTO.getTresRegionValueDate(), result.getTresRegionValueDate());
        assertEquals(Utilities.longCentsToBigDecimalEuro(classificationViewDTO.getTresBillAmountCents()), result.getTresBillAmount());
        assertEquals(classificationViewDTO.getTresRemittanceCode(), result.getTresRemittanceCode());
        assertEquals(classificationViewDTO.getTresLastName(), result.getTresLastName());
        assertEquals(classificationViewDTO.getTresLastName(), result.getTresOrCode());
        assertEquals(classificationViewDTO.getTresIuf(), result.getTresIuf());
        assertEquals(classificationViewDTO.getTresIuv(), result.getTresIuv());
        assertEquals(classificationViewDTO.getTresCreationDate(), result.getTresAcquisitionDateT());
        assertEquals(classificationViewDTO.getTresBillYear(), result.getTresBillYear());
        assertEquals(classificationViewDTO.getTresBillCode(), result.getTresBillCode());
        assertEquals(classificationViewDTO.getTresDomainIdCode(), result.getDomainUniqueId());
        assertEquals(classificationViewDTO.getTresReceptionDate(), result.getTresReceiptDate());
        assertEquals(classificationViewDTO.getTresDocumentYear(), result.getTresDocumentYear());
        assertEquals(classificationViewDTO.getTresDocumentCode(), result.getTresDocumentCode());
        assertEquals(classificationViewDTO.getTresProvisionalAe(), result.getTresProvisionalAe());
        assertEquals(classificationViewDTO.getTresProvisionalCode(), result.getTresProvisionalCode());
        assertEquals(classificationViewDTO.getTresActualSuspensionDate(), result.getTresActualSuspensionDate());
        assertEquals(classificationViewDTO.getTresManagementProvisionalCode(), result.getTresManagementProvisionalCode());
        assertEquals(classificationViewDTO.getLastClassificationDate(), result.getLastClassificationDate());
    }

}