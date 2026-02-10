package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.PaidInstallmentExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.enums.UniqueIdentifierType;
import it.gov.pagopa.payhub.activities.service.receipt.RtFileHandlerService;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentPaidViewDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonEntityType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static it.gov.pagopa.payhub.activities.util.Utilities.INSTALLMENT_REMITTANCE_INFORMATION_PLACEHOLDER;

@ExtendWith(MockitoExtension.class)
class PaidInstallmentExportFlowFileDTOMapperTest {

    @Mock
    private RtFileHandlerService rtFileHandlerServiceMock;

    private InstallmentExportFlowFileDTOMapper installmentExportFlowFileDTOMapper;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @BeforeEach
    void setUp() {
        installmentExportFlowFileDTOMapper = new InstallmentExportFlowFileDTOMapper(rtFileHandlerServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(rtFileHandlerServiceMock);
    }

    @Test
    void  givenValidInstallmentPaidView_whenMap_thenReturnInstallmentPaidViewDTO() {
        // Given
        InstallmentPaidViewDTO installmentPaidViewDTO = podamFactory.manufacturePojo(InstallmentPaidViewDTO.class);
        installmentPaidViewDTO.setCode("MARCA_BOLLO");

        Mockito.when(rtFileHandlerServiceMock.read(installmentPaidViewDTO.getOrganizationId(), installmentPaidViewDTO.getRtFilePath()))
                .thenReturn("RTXML");

        // When
        PaidInstallmentExportFlowFileDTO result = installmentExportFlowFileDTOMapper.map(installmentPaidViewDTO);

        // Then
        assertNotNull(result);
        assertAllField(installmentPaidViewDTO, result);
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
                "receiptAttachmentTest",
                "codIun",
                "notificationDate",
                "notificationFeeCents"
        );
    }

    @Test
    void  givenValidInstallmentPaidWithNullFieldView_whenMap_thenReturnInstallmentPaidViewDTO() {
        // Given
        InstallmentPaidViewDTO installmentPaidViewDTO = podamFactory.manufacturePojo(InstallmentPaidViewDTO.class);
        installmentPaidViewDTO.getDebtor().setEntityType(null);
        installmentPaidViewDTO.getDebtor().setFiscalCode(null);
        installmentPaidViewDTO.getPayer().setEntityType(null);
        installmentPaidViewDTO.getPayer().setFiscalCode(null);
        installmentPaidViewDTO.setFeeCents(null);

        Mockito.when(rtFileHandlerServiceMock.read(installmentPaidViewDTO.getOrganizationId(), installmentPaidViewDTO.getRtFilePath()))
                .thenReturn("RTXML");

        // When
        PaidInstallmentExportFlowFileDTO result = installmentExportFlowFileDTOMapper.map(installmentPaidViewDTO);

        // Then
        assertNotNull(result);
        assertNull(result.getReceiptAttachmentType());
        assertNull(result.getReceiptAttachmentTest());
        assertNull(result.getDebtorEntityType());
        assertEquals("ANONIMO", result.getDebtorUniqueIdentifierCode());
        assertNull(result.getPayerEntityType());
        assertEquals("ANONIMO", result.getPayerUniqueIdentifierCode());
        assertNull(result.getPspAppliedFees());
    }

    @Test
    void  givenValidInstallmentPaidWithNullPayerView_whenMap_thenReturnInstallmentPaidViewDTO() {
        // Given
        InstallmentPaidViewDTO installmentPaidViewDTO = podamFactory.manufacturePojo(InstallmentPaidViewDTO.class);
        installmentPaidViewDTO.setPayer(null);

        Mockito.when(rtFileHandlerServiceMock.read(installmentPaidViewDTO.getOrganizationId(), installmentPaidViewDTO.getRtFilePath()))
                .thenReturn("RTXML");

        // When
        PaidInstallmentExportFlowFileDTO result = installmentExportFlowFileDTOMapper.map(installmentPaidViewDTO);

        // Then
        assertNotNull(result);
        assertNull(result.getPayerEntityType());
        assertNull(result.getPayerUniqueIdentifierCode());
        assertNull(result.getPayerFullName());
        assertNull(result.getPayerAddress());
        assertNull(result.getPayerStreetNumber());
        assertNull(result.getPayerPostalCode());
        assertNull(result.getPayerCity());
        assertNull(result.getPayerProvince());
        assertNull(result.getPayerCountry());
        assertNull(result.getPayerEmail());
    }

    @Test
    void  givenInstallmentPaidViewDTOWithNullPaymentDateTimeWhenMapThenReturnReturnInstallmentPaidViewDTO() {
        InstallmentPaidViewDTO installmentPaidViewDTO = podamFactory.manufacturePojo(InstallmentPaidViewDTO.class);
        installmentPaidViewDTO.setPaymentDateTime(null);

        Mockito.when(rtFileHandlerServiceMock.read(installmentPaidViewDTO.getOrganizationId(), installmentPaidViewDTO.getRtFilePath()))
                .thenReturn("RTXML");

        PaidInstallmentExportFlowFileDTO result = installmentExportFlowFileDTOMapper.map(installmentPaidViewDTO);

        assertNotNull(result);
        assertNull(result.getRequestDateReference());
        assertNull(result.getReceiptMessageDateTime());
        assertNull(result.getSinglePaymentOutcomeDate());
    }

    private void assertAllField(InstallmentPaidViewDTO paidViewDTO, PaidInstallmentExportFlowFileDTO exportFlowFileDTO) {
        assertEquals(paidViewDTO.getIuf(), exportFlowFileDTO.getIuf());
        assertEquals(1, exportFlowFileDTO.getFlowRowNumber());
        assertEquals(paidViewDTO.getIud(), exportFlowFileDTO.getIud());
        assertEquals(paidViewDTO.getCreditorReferenceId(), exportFlowFileDTO.getIuv());
        assertNull(exportFlowFileDTO.getObjectVersion());
        assertEquals(paidViewDTO.getOrgFiscalCode(), exportFlowFileDTO.getDomainIdentifier());
        assertNull(exportFlowFileDTO.getRequestingStationIdentifier());
        assertEquals(paidViewDTO.getPaymentReceiptId(), exportFlowFileDTO.getReceiptMessageIdentifier());
        assertEquals(paidViewDTO.getPaymentDateTime().toLocalDateTime(), exportFlowFileDTO.getReceiptMessageDateTime());
        assertEquals(paidViewDTO.getPaymentReceiptId(), exportFlowFileDTO.getRequestMessageReference());
        assertEquals(paidViewDTO.getPaymentDateTime().toLocalDate(), exportFlowFileDTO.getRequestDateReference());
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
        assertEquals(PersonEntityType.G, exportFlowFileDTO.getBeneficiaryEntityType());
        assertEquals(paidViewDTO.getOrgFiscalCode(), exportFlowFileDTO.getBeneficiaryUniqueIdentifierCode());
        assertEquals(paidViewDTO.getCompanyName(), exportFlowFileDTO.getBeneficiaryName());

        assertEquals(paidViewDTO.getPayer().getEntityType() , exportFlowFileDTO.getPayerEntityType());
        assertEquals(paidViewDTO.getPayer().getFiscalCode() , exportFlowFileDTO.getPayerUniqueIdentifierCode());
        assertEquals(paidViewDTO.getPayer().getFullName(), exportFlowFileDTO.getPayerFullName());
        assertEquals(paidViewDTO.getPayer().getAddress(), exportFlowFileDTO.getPayerAddress());
        assertEquals(paidViewDTO.getPayer().getCivic(), exportFlowFileDTO.getPayerStreetNumber());
        assertEquals(paidViewDTO.getPayer().getPostalCode(), exportFlowFileDTO.getPayerPostalCode());
        assertEquals(paidViewDTO.getPayer().getLocation(), exportFlowFileDTO.getPayerCity());
        assertEquals(paidViewDTO.getPayer().getProvince(), exportFlowFileDTO.getPayerProvince());
        assertEquals(paidViewDTO.getPayer().getNation(), exportFlowFileDTO.getPayerCountry());
        assertEquals(paidViewDTO.getPayer().getEmail(), exportFlowFileDTO.getPayerEmail());

        assertEquals(paidViewDTO.getDebtor().getEntityType() , exportFlowFileDTO.getDebtorEntityType());
        assertEquals(paidViewDTO.getDebtor().getFiscalCode() , exportFlowFileDTO.getDebtorUniqueIdentifierCode());
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
        assertEquals(paidViewDTO.getPaymentDateTime().toLocalDate(), exportFlowFileDTO.getSinglePaymentOutcomeDate());
        assertEquals(paidViewDTO.getPaymentReceiptId(), exportFlowFileDTO.getUniqueCollectionIdentifier());
        assertEquals(Utilities.resolveRemittanceInformation(paidViewDTO.getRemittanceInformation(), paidViewDTO.getOriginalRemittanceInformation()), exportFlowFileDTO.getPaymentReason());
        assertEquals("9/"+ paidViewDTO.getCategory(), exportFlowFileDTO.getCollectionSpecificData());
        assertEquals(paidViewDTO.getCode(), exportFlowFileDTO.getDueType());
        assertNull(exportFlowFileDTO.getSignatureType());
        assertEquals("RTXML", exportFlowFileDTO.getRt());
        assertEquals(paidViewDTO.getTransferIndex(), exportFlowFileDTO.getSinglePaymentDataIndex());
        assertEquals(Utilities.longCentsToBigDecimalEuro(paidViewDTO.getFeeCents()), exportFlowFileDTO.getPspAppliedFees());
        assertEquals("BD", exportFlowFileDTO.getReceiptAttachmentType());
        assertNull(exportFlowFileDTO.getReceiptAttachmentTest());
        assertEquals(paidViewDTO.getBalance(), exportFlowFileDTO.getBalance());
        assertEquals(paidViewDTO.getOrgFiscalCode(), exportFlowFileDTO.getOrgFiscalCode());
        assertEquals(paidViewDTO.getCompanyName(), exportFlowFileDTO.getOrgName());
        assertEquals(paidViewDTO.getCategory(), exportFlowFileDTO.getDueTaxonomicCode());

    }

    @ParameterizedTest
    @MethodSource("provideRemittanceCases")
    void givenRemittanceCases_whenMap_thenExpected(String remittance, String originalRemittance, String expectedResult) {
        // Given
        InstallmentPaidViewDTO installmentPaidViewDTO = podamFactory.manufacturePojo(InstallmentPaidViewDTO.class);
        installmentPaidViewDTO.setRemittanceInformation(remittance);
        installmentPaidViewDTO.setOriginalRemittanceInformation(originalRemittance);

        Mockito.when(rtFileHandlerServiceMock.read(installmentPaidViewDTO.getOrganizationId(), installmentPaidViewDTO.getRtFilePath()))
                .thenReturn("RTXML");

        // When
        PaidInstallmentExportFlowFileDTO result = installmentExportFlowFileDTOMapper.map(installmentPaidViewDTO);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result.getPaymentReason());
    }

    private static Stream<Arguments> provideRemittanceCases() {
        return Stream.of(
                Arguments.of("Standard remittance", null, "Standard remittance"),
                Arguments.of("Standard remittance", "Original remittance", "Standard remittance"),
                Arguments.of(INSTALLMENT_REMITTANCE_INFORMATION_PLACEHOLDER + " - generated", "Original remittance", "Original remittance")
        );
    }

}