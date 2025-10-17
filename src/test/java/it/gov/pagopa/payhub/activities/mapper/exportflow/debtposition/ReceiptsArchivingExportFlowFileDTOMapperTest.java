package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.ReceiptsArchivingExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.service.receipt.RtFileHandlerService;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptArchivingView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReceiptsArchivingExportFlowFileDTOMapperTest {

    @Mock
    private RtFileHandlerService rtFileHandlerServiceMock;

    private ReceiptsArchivingExportFlowFileDTOMapper receiptsArchivingExportFlowFileDTOMapper;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @BeforeEach
    void setUp() {
        receiptsArchivingExportFlowFileDTOMapper = new ReceiptsArchivingExportFlowFileDTOMapper(rtFileHandlerServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(rtFileHandlerServiceMock);
    }

    @Test
    void givenValidReceiptArchivingView_whenMap_thenReturnReceiptsArchivingViewDTO() {
        // Given
        ReceiptArchivingView receiptArchivingView = podamFactory.manufacturePojo(ReceiptArchivingView.class);

        Mockito.when(rtFileHandlerServiceMock.read(receiptArchivingView.getOrganizationId(), receiptArchivingView.getRtFilePath()))
                .thenReturn("RTXML");

        // When
        ReceiptsArchivingExportFlowFileDTO result = receiptsArchivingExportFlowFileDTOMapper.map(receiptArchivingView);

        // Then
        assertNotNull(result);
        TestUtils.reflectionEqualsByName(receiptArchivingView,result);
        TestUtils.checkNotNullFields(result);
        assertEquals("RTXML", result.getReceiptXml());
        assertEquals("OK", result.getPaymentOutcome());
    }

    @Test
    void givenValidReceiptArchivingViewWhenPayerAndDebtorAreNull_whenMap_thenReturnReceiptsArchivingViewDTO() {
        // Given
        ReceiptArchivingView receiptArchivingView = podamFactory.manufacturePojo(ReceiptArchivingView.class);
        receiptArchivingView.setPayer(null);
        receiptArchivingView.setDebtor(null);

        Mockito.when(rtFileHandlerServiceMock.read(receiptArchivingView.getOrganizationId(), receiptArchivingView.getRtFilePath()))
                .thenReturn("RTXML");

        // When
        ReceiptsArchivingExportFlowFileDTO result = receiptsArchivingExportFlowFileDTOMapper.map(receiptArchivingView);

        // Then
        assertNotNull(result);
        TestUtils.reflectionEqualsByName(receiptArchivingView,result);
        TestUtils.checkNotNullFields(result, "debtorEntityType", "debtorFullName", "debtorEmail", "debtorUniqueIdentifierCode", "payerFullName", "payerUniqueIdentifierCode");
        assertNull(result.getDebtorEntityType());
        assertNull(result.getDebtorFullName());
        assertNull(result.getDebtorUniqueIdentifierCode());
        assertNull(result.getDebtorEmail());
        assertNull(result.getPayerUniqueIdentifierCode());
        assertNull(result.getPayerFullName());
        assertEquals("RTXML", result.getReceiptXml());
        assertEquals("OK", result.getPaymentOutcome());
    }

    @Test
    void  givenValidReceiptArchivingViewWithNullPaymentDateTimeWhenMapThenReturnReturnReturnReceiptsArchivingViewDTO() {
        ReceiptArchivingView receiptArchivingView = podamFactory.manufacturePojo(ReceiptArchivingView.class);
        receiptArchivingView.setPaymentDateTime(null);

        Mockito.when(rtFileHandlerServiceMock.read(receiptArchivingView.getOrganizationId(), receiptArchivingView.getRtFilePath()))
                .thenReturn("RTXML");

        ReceiptsArchivingExportFlowFileDTO result = receiptsArchivingExportFlowFileDTOMapper.map(receiptArchivingView);

        assertNotNull(result);
        TestUtils.reflectionEqualsByName(receiptArchivingView, result);
        TestUtils.checkNotNullFields(result, "paymentDateTime");
        assertNull(result.getPaymentDateTime());
        assertEquals("RTXML", result.getReceiptXml());
        assertEquals("OK", result.getPaymentOutcome());
    }
}