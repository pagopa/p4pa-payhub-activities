package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.ClassificationsDataExportClient;
import it.gov.pagopa.pu.classification.dto.generated.PagedClassificationView;
import it.gov.pagopa.pu.classification.dto.generated.PagedFullClassificationView;
import it.gov.pagopa.pu.processexecutions.dto.generated.ClassificationsExportFileFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ClassificationsDataExportServiceImplTest {

    @Mock
    private ClassificationsDataExportClient classificationsDataExportClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private PodamFactory podamFactory;
    private ClassificationsDataExportService classificationsDataExportService;

    @BeforeEach
    void setUp() {
        classificationsDataExportService = new ClassificationsDataExportServiceImpl(classificationsDataExportClientMock,authnServiceMock);
        podamFactory = new PodamFactoryImpl();
    }


    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                classificationsDataExportClientMock,
                authnServiceMock);
    }

    @Test
    void givenOrganizationId_WhenExportClassificationView_ThenReturnPagedClassificationView() {
        //given
        Long organizationId = 1L;
        String operatorExternalUserId = "operatorExternalUserId";
        String accessToken = "accessToken";
        ClassificationsExportFileFilter classificationsExportFileFilter = podamFactory.manufacturePojo(ClassificationsExportFileFilter.class);
        PagedClassificationView pagedClassificationView = podamFactory.manufacturePojo(PagedClassificationView.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(classificationsDataExportClientMock.getPagedClassificationView(accessToken, organizationId, operatorExternalUserId, classificationsExportFileFilter, 0,0, List.of("classificationId"))).thenReturn(pagedClassificationView);
        //when
        PagedClassificationView result = classificationsDataExportService.exportClassificationView(organizationId, operatorExternalUserId, classificationsExportFileFilter, 0, 0, List.of("classificationId"));
        //then
        assertNotNull(result);
        assertEquals(pagedClassificationView, result);
    }

    @Test
    void givenOrganizationId_WhenExportFullClassificationView_ThenReturnPagedFullClassificationView() {
        //given
        Long organizationId = 1L;
        String operatorExternalUserId = "operatorExternalUserId";
        String accessToken = "accessToken";
        ClassificationsExportFileFilter classificationsExportFileFilter = podamFactory.manufacturePojo(ClassificationsExportFileFilter.class);
        PagedFullClassificationView pagedFullClassificationView = podamFactory.manufacturePojo(PagedFullClassificationView.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(classificationsDataExportClientMock.getPagedFullClassificationView(accessToken, organizationId, operatorExternalUserId, classificationsExportFileFilter, 0,0, List.of("classificationId"))).thenReturn(pagedFullClassificationView);
        //when
        PagedFullClassificationView result = classificationsDataExportService.exportFullClassificationView(organizationId, operatorExternalUserId, classificationsExportFileFilter, 0, 0, List.of("classificationId"));
        //then
        assertNotNull(result);
        assertEquals(pagedFullClassificationView, result);
    }

}