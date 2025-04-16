package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.ReceiptsArchivingExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptArchivingView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.junit.jupiter.api.Assertions.*;

class ReceiptsArchivingExportFlowFileDTOMapperTest {

    private ReceiptsArchivingExportFlowFileDTOMapper receiptsArchivingExportFlowFileDTOMapper;
    private PodamFactory podamFactory;

    @BeforeEach
    void setUp() {
        receiptsArchivingExportFlowFileDTOMapper = new ReceiptsArchivingExportFlowFileDTOMapper();
        podamFactory= new PodamFactoryImpl();
    }

    @Test
    void givenValidReceiptArchivingView_whenMap_thenReturnReceiptsArchivingViewDTO() {
        //given
        ReceiptArchivingView receiptArchivingView = podamFactory.manufacturePojo(ReceiptArchivingView.class);
        //when
        ReceiptsArchivingExportFlowFileDTO result = receiptsArchivingExportFlowFileDTOMapper.map(receiptArchivingView);
        //then
        assertNotNull(result);
        TestUtils.reflectionEqualsByName(receiptArchivingView,result);
        TestUtils.checkNotNullFields(result, "receiptXml");
        assertNull(result.getReceiptXml());
        assertEquals("OK", result.getPaymentOutcome());
    }

    @Test
    void givenValidReceiptArchivingViewWhenPayerAndDebtorAreNull_whenMap_thenReturnReceiptsArchivingViewDTO() {
        //given
        ReceiptArchivingView receiptArchivingView = podamFactory.manufacturePojo(ReceiptArchivingView.class);
        receiptArchivingView.setPayer(null);
        receiptArchivingView.setDebtor(null);
        //when
        ReceiptsArchivingExportFlowFileDTO result = receiptsArchivingExportFlowFileDTOMapper.map(receiptArchivingView);
        //then
        assertNotNull(result);
        TestUtils.reflectionEqualsByName(receiptArchivingView,result);
        TestUtils.checkNotNullFields(result, "receiptXml","debtorEntityType", "debtorFullName", "debtorEmail", "debtorUniqueIdentifierCode", "payerFullName", "payerUniqueIdentifierCode");
        assertNull(result.getDebtorEntityType());
        assertNull(result.getDebtorFullName());
        assertNull(result.getDebtorUniqueIdentifierCode());
        assertNull(result.getDebtorEmail());
        assertNull(result.getPayerUniqueIdentifierCode());
        assertNull(result.getPayerFullName());
        assertEquals("OK", result.getPaymentOutcome());
    }

    @Test
    void givenValidReceiptArchivingViewWhenDebtorEntityIsNull_whenMap_thenReturnReceiptsArchivingViewDTO() {
        //given
        ReceiptArchivingView receiptArchivingView = podamFactory.manufacturePojo(ReceiptArchivingView.class);
        receiptArchivingView.getDebtor().setEntityType(null);
        //when
        ReceiptsArchivingExportFlowFileDTO result = receiptsArchivingExportFlowFileDTOMapper.map(receiptArchivingView);
        //then
        assertNotNull(result);
        TestUtils.reflectionEqualsByName(receiptArchivingView,result);
        TestUtils.checkNotNullFields(result, "receiptXml", "debtorEntityType");
        assertNull(result.getDebtorEntityType());
        assertEquals("OK", result.getPaymentOutcome());
    }
}