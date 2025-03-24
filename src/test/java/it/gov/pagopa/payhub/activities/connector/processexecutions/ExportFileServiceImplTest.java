package it.gov.pagopa.payhub.activities.connector.processexecutions;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.client.ExportFileClient;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExportFileServiceImplTest {

    @Mock
    private ExportFileClient exportFileClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private PodamFactory podamFactory;

    ExportFileService exportFileService;

    @BeforeEach
    void setUp() {
        exportFileService = new ExportFileServiceImpl(exportFileClientMock, authnServiceMock);
        podamFactory = new PodamFactoryImpl();
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                exportFileClientMock,
                authnServiceMock);
    }

    @Test
    void givenWhenFindPaidExportFileByIdThen() {
        //given
        Long exportFileId = 1L;
        String accessToken = "accessToken";
        PaidExportFile paidExportFileExpected = podamFactory.manufacturePojo(PaidExportFile.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(exportFileClientMock.findPaidExportFileById(exportFileId, accessToken)).thenReturn(paidExportFileExpected);
        //when
        Optional<PaidExportFile> result = exportFileService.findPaidExportFileById(exportFileId);
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(paidExportFileExpected, result.get());
    }
}