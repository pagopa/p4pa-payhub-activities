package it.gov.pagopa.payhub.activities.activity.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.export.debtposition.ExportFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.exportFlow.ExportFlowFileTypeNotSupported;
import it.gov.pagopa.payhub.activities.service.exportflow.debtposition.PaidExportFlowFileService;
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
class ExportFlowFileActivityImplTest {

    @Mock
    private PaidExportFlowFileService paidExportFlowFileService;

    private PodamFactory podamFactory;
    ExportFlowFileActivityImpl exportFlowFileActivity;

    @BeforeEach
    void setUp() {
        exportFlowFileActivity = new ExportFlowFileActivityImpl(paidExportFlowFileService);
        podamFactory = new PodamFactoryImpl();
    }

    @Test
    void givenValidFlowIdAndPaidType_whenExecuteExport_thenReturnsExpectedExportFlowFileResult() {
        //given
        ExportFlowFileResult exportFlowFileResult = podamFactory.manufacturePojo(ExportFlowFileResult.class);

        Mockito.when(paidExportFlowFileService.executeExport(1L)).thenReturn(exportFlowFileResult);
        //when
        ExportFlowFileResult result = exportFlowFileActivity.executeExport(1L, ExportFile.FlowFileTypeEnum.PAID);
        //then
        assertNotNull(result);
        assertEquals(exportFlowFileResult, result);
    }

    @Test
    void givenInvalidFlowIdAndPaidType_whenExecuteExport_thenReturnExportFlowFileTypeNotSupported(){

        ExportFlowFileTypeNotSupported ex = assertThrows(ExportFlowFileTypeNotSupported.class, () ->
                exportFlowFileActivity.executeExport(1L, ExportFile.FlowFileTypeEnum.PAYMENTS_REPORTING));

        assertEquals("Invalid export flow file type: PAYMENTS_REPORTING", ex.getMessage());

    }
}