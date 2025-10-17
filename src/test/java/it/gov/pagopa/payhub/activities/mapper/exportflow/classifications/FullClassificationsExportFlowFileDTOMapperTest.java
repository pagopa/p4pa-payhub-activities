package it.gov.pagopa.payhub.activities.mapper.exportflow.classifications;

import it.gov.pagopa.payhub.activities.dto.exportflow.classifications.ClassificationsExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.classification.dto.generated.FullClassificationViewDTO;
import it.gov.pagopa.pu.classification.dto.generated.PersonDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.junit.jupiter.api.Assertions.*;

class FullClassificationsExportFlowFileDTOMapperTest {

    private PodamFactory podamFactory;
    FullClassificationsExportFlowFileDTOMapper fullClassificationsExportFlowFileDTOMapper;

    @BeforeEach
    void setUp() {
        fullClassificationsExportFlowFileDTOMapper = new FullClassificationsExportFlowFileDTOMapper();
        podamFactory = new PodamFactoryImpl();
    }

    @Test
    void givenFullClassificationViewDTOWhenMapThenReturnClassificationsExportFlowFileDTO() {
        //given
        FullClassificationViewDTO fullClassificationViewDTO = podamFactory.manufacturePojo(FullClassificationViewDTO.class);
        //when
        ClassificationsExportFlowFileDTO result = fullClassificationsExportFlowFileDTOMapper.map(fullClassificationViewDTO);
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
                "acquisitionDateI"
        );
        allFieldsAssertions(fullClassificationViewDTO, result);
    }

    @Test
    void givenFullClassificationViewDTOWithNullPayerAndDebtorWhenMapThenReturnClassificationsExportFlowFileDTO() {
        //given
        FullClassificationViewDTO fullClassificationViewDTO = podamFactory.manufacturePojo(FullClassificationViewDTO.class);
        fullClassificationViewDTO.setRecPayer(null);
        fullClassificationViewDTO.setRecDebtor(null);
        //when
        ClassificationsExportFlowFileDTO result = fullClassificationsExportFlowFileDTOMapper.map(fullClassificationViewDTO);

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
        assertNull(result.getPayerUniqueIdTypeI());
        assertNull(result.getPayerUniqueIdCodeI());
        assertNull(result.getPayerFullNameI());
        assertNull(result.getPayerAddressI());
        assertNull(result.getPayerCivicNumberI());
        assertNull(result.getPayerPostalCodeI());
        assertNull(result.getPayerLocationI());
        assertNull(result.getPayerProvinceI());
        assertNull(result.getPayerCountryI());
        assertNull(result.getPayerEmailI());

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

    public static void allFieldsAssertions(FullClassificationViewDTO fullClassificationViewDTO, ClassificationsExportFlowFileDTO result){
        assertEquals(fullClassificationViewDTO.getRecFileName(), result.getRecFileName());
        assertEquals(1, result.getFlowRowNumber());
        assertEquals(fullClassificationViewDTO.getRecIud(), result.getRecIud());
        assertEquals(fullClassificationViewDTO.getRecIuv(), result.getRecIuv());
        assertEquals(fullClassificationViewDTO.getRecOrgFiscalCode(), result.getRecOrgFiscalCode());
        assertEquals(fullClassificationViewDTO.getRecPaymentReceiptId(), result.getRecPaymentReceiptId());
        assertEquals(fullClassificationViewDTO.getRecPaymentDateTime().toLocalDateTime(), result.getRecPaymentDateTime());
        assertEquals(fullClassificationViewDTO.getRecPaymentReceiptId(), result.getRequestMessageReferenceId());
        assertEquals(fullClassificationViewDTO.getRecPaymentDateTime().toLocalDate(), result.getRequestReferenceDate());
        assertEquals("B", result.getInstitutionAttTypeUniqueId());
        assertEquals(fullClassificationViewDTO.getRecIdPsp(), result.getRecPspId());
        assertEquals(fullClassificationViewDTO.getRecPspCompanyName(), result.getRecPspCompanyName());
        assertEquals("G", result.getBeneficiaryUniqueIdType());
        assertEquals(fullClassificationViewDTO.getRecOrgFiscalCode(), result.getBeneficiaryUniqueIdCode());
        assertEquals(fullClassificationViewDTO.getRecBeneficiaryOrgName(), result.getRecBeneficiaryName());

        PersonDTO payer = fullClassificationViewDTO.getRecPayer();

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
         assertEquals(payer.getEntityType(), result.getPayerUniqueIdTypeI());
         assertEquals(payer.getEntityType(), result.getPayerUniqueIdCodeI());
         assertEquals(payer.getFullName(), result.getPayerFullNameI());
         assertEquals(payer.getAddress(), result.getPayerAddressI());
         assertEquals(payer.getCivic(), result.getPayerCivicNumberI());
         assertEquals(payer.getPostalCode(), result.getPayerPostalCodeI());
         assertEquals(payer.getLocation(), result.getPayerLocationI());
         assertEquals(payer.getProvince(), result.getPayerProvinceI());
         assertEquals(payer.getNation(), result.getPayerCountryI());
         assertEquals(payer.getEmail(), result.getPayerEmailI());

        PersonDTO debtor = fullClassificationViewDTO.getRecDebtor();
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
        assertEquals(Utilities.longCentsToBigDecimalEuro(fullClassificationViewDTO.getRecPaymentAmount()), result.getRecPaymentAmount());
        assertEquals(fullClassificationViewDTO.getRecCreditorReferenceId(), result.getUniquePaymentId());
        assertEquals(fullClassificationViewDTO.getRecPaymentReceiptId(), result.getPaymentContextCode());
        assertEquals(Utilities.longCentsToBigDecimalEuro(fullClassificationViewDTO.getRecTransferAmount()), result.getRecTransferAmount());
        assertEquals("0", result.getSinglePaymentOutcomeE());
        assertEquals(fullClassificationViewDTO.getRecPaymentDateTime().toLocalDate(), result.getSinglePaymentOutcomeDateE());
        assertEquals(fullClassificationViewDTO.getRecPaymentReceiptId(), result.getUniqueCollectionIdE());
        assertEquals(fullClassificationViewDTO.getRecTransferRemittanceInformation(), result.getRecTransferRemittanceInformation());
        assertEquals("9/" + fullClassificationViewDTO.getRecTransferCategory(), result.getRecTransferCategory());
        assertEquals(fullClassificationViewDTO.getRecCreationDate().toLocalDate(), result.getRecCreationDate());
        assertEquals(fullClassificationViewDTO.getRecInstallmentBalance(), result.getRecInstallmentBalance());
        assertEquals(fullClassificationViewDTO.getPayRepIuf(), result.getPayRepIuf());
        assertEquals(fullClassificationViewDTO.getPayRepFlowDateTime().toLocalDateTime(), result.getPayRepFlowDateTime());
        assertEquals(fullClassificationViewDTO.getPayRepRegulationUniqueIdentifier(), result.getUniqueRegulationCodeR());
        assertEquals(fullClassificationViewDTO.getPayRepRegulationDate(), result.getRegulationDateR());
        assertEquals(fullClassificationViewDTO.getPayRepSenderPspType(), result.getSenderInstitutionUniqueIdType());
        assertEquals(fullClassificationViewDTO.getPayRepSenderPspCode(), result.getSenderInstitutionUniqueId());
        assertEquals(fullClassificationViewDTO.getPayRepSenderPspName(), result.getSenderInstitutionName());
        assertEquals(fullClassificationViewDTO.getPayRepReceiverOrganizationType(), result.getReceiverInstitutionUniqueIdType());
        assertEquals(fullClassificationViewDTO.getPayRepReceiverOrganizationCode(), result.getReceiverInstitutionUniqueId());
        assertEquals(fullClassificationViewDTO.getPayRepReceiverOrganizationName(), result.getReceiverInstitutionName());
        assertEquals(fullClassificationViewDTO.getPayRepTotalPayments(), result.getTotalPaymentsNumberR());
        assertEquals(Utilities.longCentsToBigDecimalEuro(fullClassificationViewDTO.getPayRepTotalAmountCents()), result.getTotalPaymentsAmountR());
        assertEquals(fullClassificationViewDTO.getPayRepIuv(), result.getPayRepIuv());
        assertEquals(fullClassificationViewDTO.getPayRepIur(), result.getPayRepIur());
        assertEquals(Utilities.longCentsToBigDecimalEuro(fullClassificationViewDTO.getPayRepAmountPaidCents()), result.getSinglePaymentAmountR());
        assertEquals(fullClassificationViewDTO.getPayRepPaymentOutcomeCode(), result.getSinglePaymentOutcomeCodeR());
        assertEquals(fullClassificationViewDTO.getPayRepPayDate(), result.getSinglePaymentOutcomeDateR());
        assertEquals(fullClassificationViewDTO.getRecCreationDate().toLocalDate(), result.getAcquisitionDateR());
        assertEquals(fullClassificationViewDTO.getTresAbiCode(), result.getTresAbiCode());
        assertEquals(fullClassificationViewDTO.getTresCabCode(), result.getTresCabCode());
        assertEquals(fullClassificationViewDTO.getTresAccountRegistryCode(), result.getTresAccountRegistryCode());
        assertEquals(fullClassificationViewDTO.getTresBillDate(), result.getTresBillDate());
        assertEquals(fullClassificationViewDTO.getTresRegionValueDate(), result.getTresRegionValueDate());
        assertEquals(Utilities.longCentsToBigDecimalEuro(fullClassificationViewDTO.getTresBillAmountCents()), result.getTresBillAmount());
        assertEquals(fullClassificationViewDTO.getTresRemittanceCode(), result.getTresRemittanceCode());
        assertEquals(fullClassificationViewDTO.getTresLastName(), result.getTresLastName());
        assertEquals(fullClassificationViewDTO.getTresLastName(), result.getTresOrCode());
        assertEquals(fullClassificationViewDTO.getTresIuf(), result.getTresIuf());
        assertEquals(fullClassificationViewDTO.getTresIuv(), result.getTresIuv());
        assertEquals(fullClassificationViewDTO.getTresCreationDate().toLocalDate(), result.getTresAcquisitionDateT());
        assertEquals(fullClassificationViewDTO.getTresBillYear(), result.getTresBillYear());
        assertEquals(fullClassificationViewDTO.getTresBillCode(), result.getTresBillCode());
        assertEquals(fullClassificationViewDTO.getTresDomainIdCode(), result.getDomainUniqueId());
        assertEquals(fullClassificationViewDTO.getTresReceptionDate().toLocalDate(), result.getTresReceiptDate());
        assertEquals(fullClassificationViewDTO.getTresDocumentYear(), result.getTresDocumentYear());
        assertEquals(fullClassificationViewDTO.getTresDocumentCode(), result.getTresDocumentCode());
        assertEquals(fullClassificationViewDTO.getTresProvisionalAe(), result.getTresProvisionalAe());
        assertEquals(fullClassificationViewDTO.getTresProvisionalCode(), result.getTresProvisionalCode());
        assertEquals(fullClassificationViewDTO.getTresActualSuspensionDate(), result.getTresActualSuspensionDate());
        assertEquals(fullClassificationViewDTO.getTresManagementProvisionalCode(), result.getTresManagementProvisionalCode());
        assertEquals(fullClassificationViewDTO.getLastClassificationDate(), result.getLastClassificationDate());


        assertEquals(fullClassificationViewDTO.getPayNoticeIud(), result.getPayNoticeIud());
         assertEquals(fullClassificationViewDTO.getPayNoticeIuv(), result.getPayNoticeIuv());
         assertEquals(fullClassificationViewDTO.getPayNoticePaymentExecutionDate(), result.getPayNoticePaymentExecutionDate());
         assertEquals(fullClassificationViewDTO.getPayNoticePaymentType(), result.getPayNoticePaymentType());
         assertEquals(Utilities.longCentsToBigDecimalEuro(fullClassificationViewDTO.getPayNoticeAmountPaidCents()), result.getSinglePaymentAmountI());
         assertEquals(Utilities.longCentsToBigDecimalEuro(fullClassificationViewDTO.getPayNoticePaCommissionCents()), result.getPayNoticePaCommission());
         assertEquals(fullClassificationViewDTO.getPayNoticeRemittanceInformation(), result.getPayNoticeRemittanceInformation());
         assertEquals(fullClassificationViewDTO.getPayNoticeTransferCategory(), result.getPayNoticeTransferCategory());
         assertEquals(fullClassificationViewDTO.getPayNoticeDebtPositionTypeOrgCode(), result.getPayNoticeDebtPositionTypeOrgCode());
         assertEquals(fullClassificationViewDTO.getPayNoticeBalance(), result.getPayNoticeBalance());

    }
}