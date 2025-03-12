package it.gov.pagopa.payhub.activities.connector.processexecutions.client;

import it.gov.pagopa.payhub.activities.connector.processexecutions.config.ProcessExecutionsApisHolder;
import it.gov.pagopa.pu.processexecutions.client.generated.PaidExportFileEntityControllerApi;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFile;
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

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ExportFileClientTest {

    @Mock
    private ProcessExecutionsApisHolder processExecutionsApisHolderMock;
    @Mock
    private PaidExportFileEntityControllerApi paidExportFileEntityControllerApiMock;

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
                paidExportFileEntityControllerApiMock
        );
    }

    @Test
    void givenExportFileId_WhenFindById_ThenReturnPaidExportFile() {
        //given
        Long exportFileId = 1L;
        String accessToken = "accessToken";
        PaidExportFile paidExportFileExpected = podamFactory.manufacturePojo(PaidExportFile.class);

        Mockito.when(paidExportFileEntityControllerApiMock.crudGetPaidexportfile(String.valueOf(exportFileId))).thenReturn(paidExportFileExpected);
        Mockito.when(processExecutionsApisHolderMock.getPaidExportFileEntityControllerApi(accessToken)).thenReturn(paidExportFileEntityControllerApiMock);
        //when
        PaidExportFile result = exportFileClient.findById(exportFileId, accessToken);
        //then
        Assertions.assertEquals(paidExportFileExpected, result);
    }

    @Test
    void givenNotExistentPaidExportFile_WhenFindById_ThenReturnNull() {
        // Given
        Long exportFileId = 1L;
        String accessToken = "accessToken";

        Mockito.when(paidExportFileEntityControllerApiMock.crudGetPaidexportfile(String.valueOf(exportFileId))).thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));
        Mockito.when(processExecutionsApisHolderMock.getPaidExportFileEntityControllerApi(accessToken)).thenReturn(paidExportFileEntityControllerApiMock);
        // When
        PaidExportFile result = exportFileClient.findById(exportFileId, accessToken);
        // Then
        assertNull(result);
    }
}