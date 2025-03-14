package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.export.debtposition.InstallmentExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.enums.EntityIdentifierType;
import it.gov.pagopa.payhub.activities.enums.UniqueIdentifierType;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentPaidViewDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.junit.jupiter.api.Assertions.*;

class InstallmentExportFlowFileDTOMapperTest {

    private InstallmentExportFlowFileDTOMapper installmentExportFlowFileDTOMapper;
    PodamFactory podamFactory;

    @BeforeEach
    void setUp() {
        installmentExportFlowFileDTOMapper = new InstallmentExportFlowFileDTOMapper();
        podamFactory = new PodamFactoryImpl();
    }

    @Test
    void  givenValidInstallmentPaidViewAndVersionTrack1_whenMap_thenReturnInstallmentPaidViewDTO() {
        //given
        InstallmentPaidViewDTO installmentPaidViewDTO = podamFactory.manufacturePojo(InstallmentPaidViewDTO.class);
        //when
        InstallmentExportFlowFileDTO result = installmentExportFlowFileDTOMapper.map(1.0F, installmentPaidViewDTO);
        //then
        assertNotNull(result);
        assert1(installmentPaidViewDTO, result);

        TestUtils.checkNotNullFields(result,
                "objectVersion",
                "requestingStationIdentifier",
                "attestingUnitOperCode",
                "attestingUnitOperName",
                "attestingAddress",
                "attestingStreetNumber",
                "attestingPostalCode",
                "attestingCity",
                "attestingProvince",
                "attestingCountry",
                "beneficiaryUnitOperCode",
                "beneficiaryUnitOperName",
                "beneficiaryAddress",
                "beneficiaryStreetNumber",
                "beneficiaryPostalCode",
                "beneficiaryCity",
                "beneficiaryProvince",
                "beneficiaryCountry",
                "payerFullName",
                "payerAddress",
                "payerStreetNumber",
                "payerPostalCode",
                "payerCity",
                "payerProvince",
                "payerCountry",
                "payerEmail",
                "debtorFullName",
                "debtorAddress",
                "debtorStreetNumber",
                "debtorPostalCode",
                "debtorCity",
                "debtorProvince",
                "debtorCountry",
                "debtorEmail",
                "paymentOutcomeCode",
                "totalAmountPaid",
                "uniquePaymentIdentifier",
                "paymentContextCode",
                "singleAmountPaid",
                "singlePaymentOutcome",
                "singlePaymentOutcomeDateTime",
                "uniqueCollectionIdentifier",
                "paymentReason",
                "collectionSpecificData",
                "dueType",
                "signatureType",
                "rt",
                "singlePaymentDataIndex",
                "pspAppliedFees",
                "receiptAttachmentType",
                "receiptAttachmentTest",
                "balance",
                "orgFiscalCode",
                "orgName",
                "dueTaxonomicCode"
        );
    }

    @Test
    void  givenValidInstallmentPaidViewAndVersionTrack1_1_whenMap_thenReturnInstallmentPaidViewDTO() {
        //given
        InstallmentPaidViewDTO installmentPaidViewDTO = podamFactory.manufacturePojo(InstallmentPaidViewDTO.class);
        installmentPaidViewDTO.setCode("MARCA_BOLLO");
        //when
        InstallmentExportFlowFileDTO result = installmentExportFlowFileDTOMapper.map(1.1F, installmentPaidViewDTO);
        //then
        assertNotNull(result);
        assert1(installmentPaidViewDTO, result);
        assert1_1(installmentPaidViewDTO, result);

        TestUtils.checkNotNullFields(result,
                "objectVersion",
                "requestingStationIdentifier",
                "attestingUnitOperCode",
                "attestingUnitOperName",
                "attestingAddress",
                "attestingStreetNumber",
                "attestingPostalCode",
                "attestingCity",
                "attestingProvince",
                "attestingCountry",
                "beneficiaryUnitOperCode",
                "beneficiaryUnitOperName",
                "beneficiaryAddress",
                "beneficiaryStreetNumber",
                "beneficiaryPostalCode",
                "beneficiaryCity",
                "beneficiaryProvince",
                "beneficiaryCountry",
                "payerFullName",
                "payerAddress",
                "payerStreetNumber",
                "payerPostalCode",
                "payerCity",
                "payerProvince",
                "payerCountry",
                "payerEmail",
                "debtorFullName",
                "debtorAddress",
                "debtorStreetNumber",
                "debtorPostalCode",
                "debtorCity",
                "debtorProvince",
                "debtorCountry",
                "debtorEmail",
                "paymentOutcomeCode",
                "totalAmountPaid",
                "uniquePaymentIdentifier",
                "paymentContextCode",
                "singleAmountPaid",
                "singlePaymentOutcome",
                "singlePaymentOutcomeDateTime",
                "uniqueCollectionIdentifier",
                "signatureType",
                "rt",
                "receiptAttachmentTest",
                "balance",
                "orgFiscalCode",
                "orgName",
                "dueTaxonomicCode"
        );
    }

    @Test
    void  givenValidInstallmentPaidWithoutCodeViewAndVersionTrack1_1_whenMap_thenReturnInstallmentPaidViewDTO() {
        //given
        InstallmentPaidViewDTO installmentPaidViewDTO = podamFactory.manufacturePojo(InstallmentPaidViewDTO.class);
        //when
        InstallmentExportFlowFileDTO result = installmentExportFlowFileDTOMapper.map(1.1F, installmentPaidViewDTO);
        //then
        assertNotNull(result);
        assertNull(result.getReceiptAttachmentType());
        assertNull(result.getReceiptAttachmentTest());
    }

    @Test
    void  givenValidInstallmentPaidViewAndVersionTrack1_2_whenMap_thenReturnInstallmentPaidViewDTO() {
        //given
        InstallmentPaidViewDTO installmentPaidViewDTO = podamFactory.manufacturePojo(InstallmentPaidViewDTO.class);
        installmentPaidViewDTO.setCode("MARCA_BOLLO");
        //when
        InstallmentExportFlowFileDTO result = installmentExportFlowFileDTOMapper.map(1.2F, installmentPaidViewDTO);
        //then
        assertNotNull(result);
        assert1(installmentPaidViewDTO, result);
        assert1_1(installmentPaidViewDTO, result);
        assert1_2(installmentPaidViewDTO, result);

        TestUtils.checkNotNullFields(result,
                "objectVersion",
                "requestingStationIdentifier",
                "attestingUnitOperCode",
                "attestingUnitOperName",
                "attestingAddress",
                "attestingStreetNumber",
                "attestingPostalCode",
                "attestingCity",
                "attestingProvince",
                "attestingCountry",
                "beneficiaryUnitOperCode",
                "beneficiaryUnitOperName",
                "beneficiaryAddress",
                "beneficiaryStreetNumber",
                "beneficiaryPostalCode",
                "beneficiaryCity",
                "beneficiaryProvince",
                "beneficiaryCountry",
                "payerFullName",
                "payerAddress",
                "payerStreetNumber",
                "payerPostalCode",
                "payerCity",
                "payerProvince",
                "payerCountry",
                "payerEmail",
                "debtorFullName",
                "debtorAddress",
                "debtorStreetNumber",
                "debtorPostalCode",
                "debtorCity",
                "debtorProvince",
                "debtorCountry",
                "debtorEmail",
                "paymentOutcomeCode",
                "totalAmountPaid",
                "uniquePaymentIdentifier",
                "paymentContextCode",
                "singleAmountPaid",
                "singlePaymentOutcome",
                "singlePaymentOutcomeDateTime",
                "uniqueCollectionIdentifier",
                "signatureType",
                "rt",
                "receiptAttachmentTest",
                "orgFiscalCode",
                "orgName",
                "dueTaxonomicCode"
        );
    }

    @Test
    void  givenValidInstallmentPaidViewAndVersionTrack1_3_whenMap_thenReturnInstallmentPaidViewDTO() {
        //given
        InstallmentPaidViewDTO installmentPaidViewDTO = podamFactory.manufacturePojo(InstallmentPaidViewDTO.class);
        installmentPaidViewDTO.setCode("MARCA_BOLLO");
        //when
        InstallmentExportFlowFileDTO result = installmentExportFlowFileDTOMapper.map(1.3F, installmentPaidViewDTO);
        //then
        assertNotNull(result);
        assert1(installmentPaidViewDTO, result);
        assert1_1(installmentPaidViewDTO, result);
        assert1_2(installmentPaidViewDTO, result);
        assert1_3(installmentPaidViewDTO, result);
        TestUtils.checkNotNullFields(result,
                "objectVersion",
                "requestingStationIdentifier",
                "attestingUnitOperCode",
                "attestingUnitOperName",
                "attestingAddress",
                "attestingStreetNumber",
                "attestingPostalCode",
                "attestingCity",
                "attestingProvince",
                "attestingCountry",
                "beneficiaryUnitOperCode",
                "beneficiaryUnitOperName",
                "beneficiaryAddress",
                "beneficiaryStreetNumber",
                "beneficiaryPostalCode",
                "beneficiaryCity",
                "beneficiaryProvince",
                "beneficiaryCountry",
                "payerFullName",
                "payerAddress",
                "payerStreetNumber",
                "payerPostalCode",
                "payerCity",
                "payerProvince",
                "payerCountry",
                "payerEmail",
                "debtorFullName",
                "debtorAddress",
                "debtorStreetNumber",
                "debtorPostalCode",
                "debtorCity",
                "debtorProvince",
                "debtorCountry",
                "debtorEmail",
                "paymentOutcomeCode",
                "totalAmountPaid",
                "uniquePaymentIdentifier",
                "paymentContextCode",
                "singleAmountPaid",
                "singlePaymentOutcome",
                "singlePaymentOutcomeDateTime",
                "uniqueCollectionIdentifier",
                "signatureType",
                "rt",
                "receiptAttachmentTest"
        );
    }

    @Test
    void givenValidInstallmentPaidViewAndWrongVersionTrack_whenMapToInstallmentPaidViewDTO_thenThrowException(){
        //given
        InstallmentPaidViewDTO installmentPaidViewDTO = podamFactory.manufacturePojo(InstallmentPaidViewDTO.class);

        //when
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> installmentExportFlowFileDTOMapper.map(1.4F, installmentPaidViewDTO));
        //then
        assertEquals("Unexpected versionTrack 1.4", ex.getMessage());
    }


    private void assert1(InstallmentPaidViewDTO paidViewDTO, InstallmentExportFlowFileDTO exportFlowFileDTO) {
        assertEquals(paidViewDTO.getIuf(), exportFlowFileDTO.getIuf());
        assertEquals(1, exportFlowFileDTO.getFlowRowNumber());
        assertEquals(paidViewDTO.getIud(), exportFlowFileDTO.getIud());
        assertEquals(paidViewDTO.getNoticeNumber(), exportFlowFileDTO.getIuv());
        assertNull(exportFlowFileDTO.getObjectVersion());
        assertEquals(paidViewDTO.getOrgFiscalCode(), exportFlowFileDTO.getDomainIdentifier());
        assertNull(exportFlowFileDTO.getRequestingStationIdentifier());
        assertEquals(paidViewDTO.getPaymentReceiptId(), exportFlowFileDTO.getReceiptMessageIdentifier());
        assertEquals(paidViewDTO.getPaymentDateTime(), exportFlowFileDTO.getReceiptMessageDateTime());
        assertEquals(paidViewDTO.getPaymentReceiptId(), exportFlowFileDTO.getRequestMessageReference());
        assertEquals(paidViewDTO.getPaymentDateTime(), exportFlowFileDTO.getRequestDateTimeReference());
        assertEquals(UniqueIdentifierType.B, exportFlowFileDTO.getUniqueIdentifierType());
        assertEquals(paidViewDTO.getIdPsp(), exportFlowFileDTO.getUniqueIdentifierCode());
        assertEquals(paidViewDTO.getPspCompanyName(), exportFlowFileDTO.getAttestingName());
        assertNull(exportFlowFileDTO.getAttestingUnitOperCode());
        assertNull(exportFlowFileDTO.getAttestingUnitOperName());
        assertNull(exportFlowFileDTO.getAttestingAddress());
        assertNull(exportFlowFileDTO.getAttestingStreetNumber());
        assertNull(exportFlowFileDTO.getAttestingPostalCode());
        assertNull(exportFlowFileDTO.getAttestingCity());
        assertNull(exportFlowFileDTO.getAttestingProvince());
        assertNull(exportFlowFileDTO.getAttestingCountry());
        assertEquals(EntityIdentifierType.G, exportFlowFileDTO.getBeneficiaryEntityType());
        assertEquals(paidViewDTO.getOrgFiscalCode(), exportFlowFileDTO.getBeneficiaryUniqueIdentifierCode());
        assertEquals(paidViewDTO.getCompanyName(), exportFlowFileDTO.getBeneficiaryName());

        assertEquals(EntityIdentifierType.fromValue(paidViewDTO.getPayer().getEntityType().getValue()) , exportFlowFileDTO.getPayerEntityType());
        assertEquals(paidViewDTO.getPayer().getFiscalCode() , exportFlowFileDTO.getPayerUniqueIdentifierCode());
        assertEquals(paidViewDTO.getPayer().getFullName(), exportFlowFileDTO.getPayerFullName());
        assertEquals(paidViewDTO.getPayer().getAddress(), exportFlowFileDTO.getPayerAddress());
        assertEquals(paidViewDTO.getPayer().getCivic(), exportFlowFileDTO.getPayerStreetNumber());
        assertEquals(paidViewDTO.getPayer().getPostalCode(), exportFlowFileDTO.getPayerPostalCode());
        assertEquals(paidViewDTO.getPayer().getLocation(), exportFlowFileDTO.getPayerCity());
        assertEquals(paidViewDTO.getPayer().getProvince(), exportFlowFileDTO.getPayerProvince());
        assertEquals(paidViewDTO.getPayer().getNation(), exportFlowFileDTO.getPayerCountry());
        assertEquals(paidViewDTO.getPayer().getEmail(), exportFlowFileDTO.getPayerEmail());

        assertEquals(EntityIdentifierType.fromValue(paidViewDTO.getDebtor().getEntityType().getValue()) , exportFlowFileDTO.getDebtorEntityType());
        assertEquals(paidViewDTO.getDebtor().getFiscalCode() , exportFlowFileDTO.getDebtorIndentifierCode());
        assertEquals(paidViewDTO.getDebtor().getFullName(), exportFlowFileDTO.getDebtorFullName());
        assertEquals(paidViewDTO.getDebtor().getAddress(), exportFlowFileDTO.getDebtorAddress());
        assertEquals(paidViewDTO.getDebtor().getCivic(), exportFlowFileDTO.getDebtorStreetNumber());
        assertEquals(paidViewDTO.getDebtor().getPostalCode(), exportFlowFileDTO.getDebtorPostalCode());
        assertEquals(paidViewDTO.getDebtor().getLocation(), exportFlowFileDTO.getDebtorCity());
        assertEquals(paidViewDTO.getDebtor().getProvince(), exportFlowFileDTO.getDebtorProvince());
        assertEquals(paidViewDTO.getDebtor().getNation(), exportFlowFileDTO.getDebtorCountry());
        assertEquals(paidViewDTO.getDebtor().getEmail(), exportFlowFileDTO.getDebtorEmail());
        assertEquals(0, exportFlowFileDTO.getPaymentOutcomeCode());
        assertEquals(Utilities.longCentsToBigDecimalEuro(paidViewDTO.getPaymentAmountCents()), exportFlowFileDTO.getTotalAmountPaid());
        assertEquals(paidViewDTO.getCreditorReferenceId(), exportFlowFileDTO.getUniquePaymentIdentifier());
        assertEquals(paidViewDTO.getPaymentReceiptId(), exportFlowFileDTO.getPaymentContextCode());
        assertEquals(Utilities.longCentsToBigDecimalEuro(paidViewDTO.getAmountCents()), exportFlowFileDTO.getSingleAmountPaid());
        assertEquals("0", exportFlowFileDTO.getSinglePaymentOutcome());
        assertEquals(paidViewDTO.getPaymentDateTime(), exportFlowFileDTO.getSinglePaymentOutcomeDateTime());
        assertEquals(paidViewDTO.getPaymentReceiptId(), exportFlowFileDTO.getUniqueCollectionIdentifier());
    }

    private void assert1_1(InstallmentPaidViewDTO paidViewDTO, InstallmentExportFlowFileDTO exportFlowFileDTO){
        assertEquals(paidViewDTO.getRemittanceInformation(), exportFlowFileDTO.getPaymentReason());
        assertEquals("9/"+ paidViewDTO.getCategory(), exportFlowFileDTO.getCollectionSpecificData());
        assertEquals(paidViewDTO.getCode(), exportFlowFileDTO.getDueType());
        assertNull(exportFlowFileDTO.getSignatureType());
        assertNull(exportFlowFileDTO.getRt());
        assertEquals(paidViewDTO.getTransferIndex(), exportFlowFileDTO.getSinglePaymentDataIndex());
        assertEquals(Utilities.longCentsToBigDecimalEuro(paidViewDTO.getFeeCents()), exportFlowFileDTO.getPspAppliedFees());
        assertEquals("BD", exportFlowFileDTO.getReceiptAttachmentType());
        assertNull(exportFlowFileDTO.getReceiptAttachmentTest());
    }

    private void assert1_2(InstallmentPaidViewDTO paidViewDTO, InstallmentExportFlowFileDTO exportFlowFileDTO){
        assertEquals(paidViewDTO.getBalance(), exportFlowFileDTO.getBalance());
    }

    private void assert1_3(InstallmentPaidViewDTO paidViewDTO, InstallmentExportFlowFileDTO exportFlowFileDTO){
        assertEquals(paidViewDTO.getOrgFiscalCode(), exportFlowFileDTO.getOrgFiscalCode());
        assertEquals(paidViewDTO.getCompanyName(), exportFlowFileDTO.getOrgName());
        assertEquals(paidViewDTO.getCategory(), exportFlowFileDTO.getDueTaxonomicCode());
    }

}