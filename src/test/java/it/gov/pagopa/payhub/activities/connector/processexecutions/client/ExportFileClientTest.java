package it.gov.pagopa.payhub.activities.connector.processexecutions.client;

import static org.junit.jupiter.api.Assertions.assertNull;

import it.gov.pagopa.payhub.activities.connector.processexecutions.config.ProcessExecutionsApisHolder;
import it.gov.pagopa.payhub.activities.dto.exportflow.UpdateStatusRequest;
import it.gov.pagopa.pu.processexecutions.client.generated.*;
import it.gov.pagopa.pu.processexecutions.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(MockitoExtension.class)
class ExportFileClientTest {

    @Mock
    private ProcessExecutionsApisHolder processExecutionsApisHolderMock;
    @Mock
    private PaidExportFileEntityControllerApi paidExportFileEntityControllerApiMock;
    @Mock
    private ExportFileEntityControllerApi exportFileEntityControllerApiMock;
    @Mock
    private ExportFileEntityExtendedControllerApi exportFileEntityExtendedControllerApiMock;
    @Mock
    private ReceiptsArchivingExportFileEntityControllerApi receiptsArchivingExportFileEntityControllerApiMock;
    @Mock
    private ClassificationsExportFileEntityControllerApi classificationsExportFileEntityControllerApiMock;

    ExportFileClient exportFileClient;
    PodamFactory podamFactory;

    @BeforeEach
    void setUp() {
        exportFileClient = new ExportFileClient(processExecutionsApisHolderMock);
        podamFactory = new PodamFactoryImpl();
    }


    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                processExecutionsApisHolderMock,
                paidExportFileEntityControllerApiMock,
                receiptsArchivingExportFileEntityControllerApiMock,
                classificationsExportFileEntityControllerApiMock
        );
    }

    @Test
    void givenExportFileId_WhenFindPaidExportFileById_ThenReturnPaidExportFile() {
        //given
        Long exportFileId = 1L;
        String accessToken = "accessToken";
        PaidExportFile paidExportFileExpected = podamFactory.manufacturePojo(PaidExportFile.class);

        Mockito.when(paidExportFileEntityControllerApiMock.crudGetPaidexportfile(String.valueOf(exportFileId)))
                .thenReturn(paidExportFileExpected);
        Mockito.when(processExecutionsApisHolderMock.getPaidExportFileEntityControllerApi(accessToken))
                .thenReturn(paidExportFileEntityControllerApiMock);
        //when
        PaidExportFile result = exportFileClient.findPaidExportFileById(exportFileId, accessToken);
        //then
        Assertions.assertEquals(paidExportFileExpected, result);
    }

    @Test
    void givenNotExistentPaidExportFile_WhenFindPaidExportFileById_ThenReturnNull() {
        // Given
        Long exportFileId = 1L;
        String accessToken = "accessToken";

        Mockito.when(paidExportFileEntityControllerApiMock.crudGetPaidexportfile(String.valueOf(exportFileId)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));
        Mockito.when(processExecutionsApisHolderMock.getPaidExportFileEntityControllerApi(accessToken))
                .thenReturn(paidExportFileEntityControllerApiMock);
        // When
        PaidExportFile result = exportFileClient.findPaidExportFileById(exportFileId, accessToken);
        // Then
        assertNull(result);
    }

    @Test
    void givenExportFileId_WhenFindById_ThenReturnExportFile() {
        //given
        Long exportFileId = 1L;
        String accessToken = "accessToken";
        ExportFile exportFile = podamFactory.manufacturePojo(ExportFile.class);

        Mockito.when(processExecutionsApisHolderMock.getExportFileEntityControllerApi(accessToken))
                .thenReturn(exportFileEntityControllerApiMock);
        Mockito.when(exportFileEntityControllerApiMock.crudGetExportfile(String.valueOf(exportFileId)))
                .thenReturn(exportFile);
        //when
        ExportFile result = exportFileClient.findById(exportFileId, accessToken);
        //then
        Assertions.assertEquals(exportFile, result);
    }

    @Test
    void givenNonExistentExportFileId_WhenFindById_ThenReturnNull() {
        //given
        Long exportFileId = 1L;
        String accessToken = "accessToken";

        Mockito.when(processExecutionsApisHolderMock.getExportFileEntityControllerApi(accessToken))
                .thenReturn(exportFileEntityControllerApiMock);
        Mockito.when(exportFileEntityControllerApiMock.crudGetExportfile(String.valueOf(exportFileId)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));
        //when
        ExportFile result = exportFileClient.findById(exportFileId, accessToken);
        //then
        Assertions.assertNull(result);
    }

    @Test
    void whenUpdateStatusOkThenReturn1() {
        //given
        Long exportFileId = 1L;
        String accessToken = "accessToken";
        String filePath = "filePath";
        String fileName = "fileName";
        Long fileSize = 20L;
        Long numTotalRows = 2L;

        Mockito.when(processExecutionsApisHolderMock.getExportFileEntityExtendedControllerApi(accessToken))
                .thenReturn(exportFileEntityExtendedControllerApiMock);
        Mockito.when(exportFileEntityExtendedControllerApiMock.updateExportFileStatus(exportFileId, ExportFileStatus.COMPLETED, ExportFileStatus.EXPIRED, filePath, fileName, fileSize, numTotalRows, ""))
                .thenReturn(1);
        //when
        Integer result = exportFileClient.updateStatus(new UpdateStatusRequest(exportFileId, ExportFileStatus.COMPLETED, ExportFileStatus.EXPIRED, filePath, fileName, fileSize, numTotalRows,""),accessToken);
        //then
        Assertions.assertEquals(1, result);
    }

    @Test
    void whenUpdateStatusKoThenReturnNull() {
        //given
        Long exportFileId = 1L;
        String accessToken = "accessToken";
        String filePath = "filePath";
        String fileName = "fileName";
        Long fileSize = 20L;
        Long numTotalRows = 2L;

        Mockito.when(processExecutionsApisHolderMock.getExportFileEntityExtendedControllerApi(accessToken))
                .thenReturn(exportFileEntityExtendedControllerApiMock);
        Mockito.when(exportFileEntityExtendedControllerApiMock.updateExportFileStatus(exportFileId, ExportFileStatus.COMPLETED, ExportFileStatus.EXPIRED, filePath, fileName, fileSize, numTotalRows, ""))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));
        //when
        Integer result = exportFileClient.updateStatus(new UpdateStatusRequest(exportFileId, ExportFileStatus.COMPLETED, ExportFileStatus.EXPIRED, filePath, fileName, fileSize, numTotalRows, ""), accessToken);
        //then
        Assertions.assertNull(result);
    }

    @Test
    void givenExportFileId_WhenFindReceiptsArchivingExportFileById_ThenReturnReceiptsArchivingExportFile() {
        //given
        Long exportFileId = 1L;
        String accessToken = "accessToken";
        ReceiptsArchivingExportFile receiptsArchivingExportFile = podamFactory.manufacturePojo(ReceiptsArchivingExportFile.class);

        Mockito.when(receiptsArchivingExportFileEntityControllerApiMock.crudGetReceiptsarchivingexportfile(String.valueOf(exportFileId)))
                .thenReturn(receiptsArchivingExportFile);
        Mockito.when(processExecutionsApisHolderMock.getReceiptsArchivingExportFileEntityControllerApi(accessToken))
                .thenReturn(receiptsArchivingExportFileEntityControllerApiMock);
        //when
        ReceiptsArchivingExportFile result = exportFileClient.findReceiptsArchivingExportFileById(exportFileId, accessToken);
        //then
        Assertions.assertEquals(receiptsArchivingExportFile, result);
    }

    @Test
    void givenNotExistentReceiptsArchivingExportFile_WhenFindReceiptsArchivingExportFileById_ThenReturnNull() {
        // Given
        Long exportFileId = 1L;
        String accessToken = "accessToken";

        Mockito.when(receiptsArchivingExportFileEntityControllerApiMock.crudGetReceiptsarchivingexportfile(String.valueOf(exportFileId)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));
        Mockito.when(processExecutionsApisHolderMock.getReceiptsArchivingExportFileEntityControllerApi(accessToken))
                .thenReturn(receiptsArchivingExportFileEntityControllerApiMock);
        // When
        ReceiptsArchivingExportFile result = exportFileClient.findReceiptsArchivingExportFileById(exportFileId, accessToken);
        // Then
        assertNull(result);
    }

    @Test
    void givenExportFileId_WhenFindClassificationsExportFileById_ThenReturnClassificationExportFile() {
        //given
        Long exportFileId = 1L;
        String accessToken = "accessToken";
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);

        Mockito.when(classificationsExportFileEntityControllerApiMock.crudGetClassificationsexportfile(String.valueOf(exportFileId)))
                .thenReturn(classificationsExportFile);
        Mockito.when(processExecutionsApisHolderMock.getClassificationsExportFileEntityControllerApi(accessToken))
                .thenReturn(classificationsExportFileEntityControllerApiMock);
        //when
        ClassificationsExportFile result = exportFileClient.findClassificationsExportFileById(exportFileId, accessToken);
        //then
        Assertions.assertEquals(classificationsExportFile, result);
    }

    @Test
    void givenNotExistentClassificationsExportFile_WhenFindClassificationsExportFileById_ThenReturnNull() {
        // Given
        Long exportFileId = 1L;
        String accessToken = "accessToken";

        Mockito.when(classificationsExportFileEntityControllerApiMock.crudGetClassificationsexportfile(String.valueOf(exportFileId)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));
        Mockito.when(processExecutionsApisHolderMock.getClassificationsExportFileEntityControllerApi(accessToken))
                .thenReturn(classificationsExportFileEntityControllerApiMock);
        // When
        ClassificationsExportFile result = exportFileClient.findClassificationsExportFileById(exportFileId, accessToken);
        // Then
        assertNull(result);
    }
}