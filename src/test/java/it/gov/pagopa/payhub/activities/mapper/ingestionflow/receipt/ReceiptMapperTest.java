package it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.service.receipt.RtFileHandlerService;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.PaSendRTV2Request;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ReceiptMapperTest {

    @Mock
    private RtFileHandlerService rtFileHandlerServiceMock;

    @Mock
    private OrganizationService organizationServiceMock;

    @InjectMocks
    private ReceiptMapper receiptMapper;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(rtFileHandlerServiceMock,
                organizationServiceMock);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void givenPaSendRTV2RequestWhenMapThenOk(boolean isPayerNull) {
        // Given
        Long organizationId = 0L;
        IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
        ingestionFlowFile.setIngestionFlowFileId(10L);
        ingestionFlowFile.setOrganizationId(organizationId);
        ingestionFlowFile.setFileName("rtFileName.xml");

        PaSendRTV2Request request = podamFactory.manufacturePojo(PaSendRTV2Request.class);
        if (isPayerNull)
            request.getReceipt().setPayer(null);
        //fix due to the fact that the field setter has non-standard name
        request.getReceipt().setPSPCompanyName(podamFactory.manufacturePojo(String.class));
        request.getReceipt().getTransferList().getTransfers().forEach(t -> t.setIBAN(podamFactory.manufacturePojo(String.class)));
        request.getReceipt().getTransferList().getTransfers().getFirst().setMBDAttachment(podamFactory.manufacturePojo(byte[].class));

        Organization organization = new Organization();
        organization.setOrganizationId(organizationId);
        organization.setIpaCode("IPACODE");

        String rtFilePath = "RT/FILE/PATH.xml";
        Mockito.when(rtFileHandlerServiceMock.store(Mockito.same(ingestionFlowFile.getOrganizationId()), Mockito.same(request.getReceipt()), Mockito.same(ingestionFlowFile.getFileName())))
                .thenReturn(rtFilePath);
        Mockito.when(organizationServiceMock.getOrganizationById(organizationId)).thenReturn(
                Optional.of(organization));

        // When
        ReceiptWithAdditionalNodeDataDTO result = receiptMapper.map(ingestionFlowFile, request);

        // Then
        TestUtils.checkNotNullFields(result, "receiptId", "creationDate", "updateDate", "payer", "iud", "debtPositionTypeOrgCode", "balance", "updateOperatorExternalId", "updateTraceId");
        TestUtils.checkNotNullFields(result.getDebtor());
        if (!isPayerNull) {
            Assertions.assertNotNull(result.getPayer());
            TestUtils.checkNotNullFields(result.getPayer());
        }
        Assertions.assertEquals(ingestionFlowFile.getIngestionFlowFileId(), result.getIngestionFlowFileId());
        Assertions.assertEquals(rtFilePath, result.getRtFilePath());
        Assertions.assertEquals(request.getReceipt().getTransferList().getTransfers().size(), result.getTransfers().size());
        Assertions.assertEquals(request.getReceipt().getMetadata().getMapEntries().size(), result.getMetadata().size());
        result.getTransfers().forEach(o -> TestUtils.checkNotNullFields(o, "mbdAttachment"));
        Assertions.assertNotNull(result.getTransfers().getFirst().getMbdAttachment());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void givenReceiptIngestionFlowFileDTOWhenMapThenOk(boolean codPaymentResult) {
        // Given
        IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
        ingestionFlowFile.setIngestionFlowFileId(10L);
        ingestionFlowFile.setFileName("rtFileName.csv");

        ReceiptIngestionFlowFileDTO request = podamFactory.manufacturePojo(ReceiptIngestionFlowFileDTO.class);
        if (codPaymentResult) {
            request.setOutcome("1");
            request.setRt(null);
            request.setPaymentNote(null);
            request.setSourceFlowName(null);
        }

        String rtFilePath = "RT/FILE/PATH.xml";
        if (!codPaymentResult) {
            Mockito.when(rtFileHandlerServiceMock.store(Mockito.same(ingestionFlowFile.getOrganizationId()), Mockito.same(request.getRt()), Mockito.same(ingestionFlowFile.getFileName())))
                    .thenReturn(rtFilePath);
        }

        // When
        ReceiptWithAdditionalNodeDataDTO result = receiptMapper.map(ingestionFlowFile, request);

        // Then
        TestUtils.checkNotNullFields(result, "receiptId", "officeName", "pspFiscalCode", "pspPartitaIva",
                "idChannel", "channelDescription", "paymentMethod", "applicationDate", "transferDate", "standin",
                "creationDate", "updateDate", "metadata", "rtFilePath", "paymentNote", "updateOperatorExternalId", "updateTraceId");
        TestUtils.checkNotNullFields(result.getDebtor());
        TestUtils.checkNotNullFields(result.getPayer());
        if (!codPaymentResult) {
            Assertions.assertEquals("OK", result.getOutcome());
            Assertions.assertEquals(rtFilePath, result.getRtFilePath());
            Assertions.assertEquals("9/".concat(request.getPaymentNote()), result.getPaymentNote());
            Assertions.assertEquals(request.getSourceFlowName(), result.getSourceFlowName());
        } else {
            Assertions.assertEquals("rtFileName.csv", result.getSourceFlowName());
        }
        Assertions.assertEquals(ingestionFlowFile.getIngestionFlowFileId(), result.getIngestionFlowFileId());
        result.getTransfers().forEach((transfer -> TestUtils.checkNotNullFields(transfer, "iban", "metadata")));
    }

    @ParameterizedTest
    @CsvSource({
            "true,true,true",
            "false,true,true",
            "true,false,true",
            "true,true,false"
    })
    void givenVariousPayerInputs_whenMap_thenPayerSetOnlyIfValid(
            boolean hasEntityType,
            boolean hasFiscalCode,
            boolean hasFullName
    ) {
        // Given
        IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
        ingestionFlowFile.setIngestionFlowFileId(10L);
        ingestionFlowFile.setFileName("rtFileName.csv");

        ReceiptIngestionFlowFileDTO request = podamFactory.manufacturePojo(ReceiptIngestionFlowFileDTO.class);

        if (!hasEntityType) {
            request.setPayerEntityType(null);
        }

        if (!hasFiscalCode) {
            request.setPayerFiscalCode(null);
        }

        if (!hasFullName) {
            request.setPayerFullName(null);
        }

        String rtFilePath = "RT/FILE/PATH.xml";
        Mockito.when(rtFileHandlerServiceMock.store(Mockito.same(ingestionFlowFile.getOrganizationId()), Mockito.same(request.getRt()), Mockito.same(ingestionFlowFile.getFileName())))
                .thenReturn(rtFilePath);

        // When
        ReceiptWithAdditionalNodeDataDTO result = receiptMapper.map(ingestionFlowFile, request);

        // Then
        if (hasEntityType && hasFiscalCode && hasFullName) {
            TestUtils.checkNotNullFields(result.getPayer(), "entityType", "fiscalCode", "fullName");
        } else {
            Assertions.assertNull(result.getPayer());
        }
    }

}
