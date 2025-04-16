package it.gov.pagopa.payhub.activities.activity.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.exportflow.ExportFileResult;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileTypeNotSupported;
import it.gov.pagopa.payhub.activities.service.exportflow.debtposition.PaidExportFileService;
import it.gov.pagopa.payhub.activities.service.exportflow.debtposition.ReceiptsArchivingExportFileService;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExportFileActivityImplTest {

    @Mock
    private PaidExportFileService paidExportFlowFileService;
    @Mock
    private ReceiptsArchivingExportFileService receiptsArchivingExportFileServiceMock;

    private PodamFactory podamFactory;
    ExportFileActivityImpl exportFlowFileActivity;

    @BeforeEach
    void setUp() {
        exportFlowFileActivity = new ExportFileActivityImpl(paidExportFlowFileService, receiptsArchivingExportFileServiceMock);
        podamFactory = new PodamFactoryImpl();
    }

    @Test
    void givenValidFlowIdAndPaidType_whenExecuteExport_thenReturnsExpectedExportFlowFileResult() {
        //given
        ExportFileResult exportFlowFileResult = podamFactory.manufacturePojo(ExportFileResult.class);

        Mockito.when(paidExportFlowFileService.executeExport(1L)).thenReturn(exportFlowFileResult);
        //when
        ExportFileResult result = exportFlowFileActivity.executeExport(1L, ExportFile.ExportFileTypeEnum.PAID);
        //then
        assertNotNull(result);
        assertEquals(exportFlowFileResult, result);
    }

    @Test
    void givenValidFlowIdAndReceiptsArchivingType_whenExecuteExport_thenReturnsExpectedExportFlowFileResult() {
        //given
        ExportFileResult exportFlowFileResult = podamFactory.manufacturePojo(ExportFileResult.class);

        Mockito.when(receiptsArchivingExportFileServiceMock.executeExport(1L)).thenReturn(exportFlowFileResult);
        //when
        ExportFileResult result = exportFlowFileActivity.executeExport(1L, ExportFile.ExportFileTypeEnum.RECEIPTS_ARCHIVING);
        //then
        assertNotNull(result);
        assertEquals(exportFlowFileResult, result);
    }

    @Test
    void givenInvalidFlowIdAndPaidType_whenExecuteExport_thenReturnExportFlowFileTypeNotSupported(){

        ExportFileTypeNotSupported ex = assertThrows(ExportFileTypeNotSupported.class, () ->
                exportFlowFileActivity.executeExport(1L, ExportFile.ExportFileTypeEnum.PAYMENTS_REPORTING));

        assertEquals("Invalid export file type: PAYMENTS_REPORTING", ex.getMessage());

    }
}