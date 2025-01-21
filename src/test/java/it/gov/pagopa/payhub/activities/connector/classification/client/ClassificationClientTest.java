package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.client.generated.ClassificationEntityControllerApi;
import it.gov.pagopa.pu.classification.client.generated.ClassificationEntityExtendedControllerApi;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassificationClientTest {

    @Mock
    private ClassificationApisHolder classificationApisHolder;

    private ClassificationClient classificationClient;

    @BeforeEach
    void setUp() {
        classificationClient = new ClassificationClient(classificationApisHolder);
    }
    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(classificationApisHolder);
    }


    @Test
    void testSaveAll() {
        // Given
        List<Classification> classificationList = List.of(new Classification());
        String accessToken = "accessToken";
        Integer expectedResponse = 1;
        ClassificationEntityExtendedControllerApi mockApi = mock(ClassificationEntityExtendedControllerApi.class);
        when(classificationApisHolder.getClassificationEntityExtendedControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.saveAll2(classificationList)).thenReturn(expectedResponse);

        // When
        Integer result = classificationClient.saveAll(classificationList, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationApisHolder.getClassificationEntityExtendedControllerApi(accessToken), times(1))
                .saveAll2(classificationList);
    }

    @Test
    void testSave() {
        // Given
        Classification classification = new Classification();
        String accessToken = "accessToken";
        Classification expectedResponse = new Classification();
        ClassificationEntityControllerApi mockApi = mock(ClassificationEntityControllerApi.class);
        when(classificationApisHolder.getClassificationEntityControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.crudCreateClassification(any())).thenReturn(expectedResponse);

        // When
        Classification result = classificationClient.save(classification, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationApisHolder.getClassificationEntityControllerApi(accessToken), times(1))
                .crudCreateClassification(any());
    }

    @Test
    void testDeleteByOrganizationIdAndIufAndLabel() {
        // Given
        Long organizationId = 1L;
        String iuf = "IUF123";
        String classification = "classification";
        String accessToken = "accessToken";
        Long expectedResponse = 1L;
        ClassificationEntityExtendedControllerApi mockApi = mock(ClassificationEntityExtendedControllerApi.class);
        when(classificationApisHolder.getClassificationEntityExtendedControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.deleteByOrganizationIdAndIufAndLabel(organizationId, iuf, classification)).thenReturn(expectedResponse);

        // When
        Long result = classificationClient.deleteByOrganizationIdAndIufAndLabel(organizationId, iuf, classification, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationApisHolder.getClassificationEntityExtendedControllerApi(accessToken), times(1))
                .deleteByOrganizationIdAndIufAndLabel(organizationId, iuf, classification);
    }

    @Test
    void testDeleteByOrganizationIdAndIuvAndIurAndTransferIndex() {
        // Given
        Long organizationId = 1L;
        String iuv = "IUV123";
        String iur = "IUR123";
        int transferIndex = 0;
        String accessToken = "accessToken";
        Long expectedResponse = 1L;
        ClassificationEntityExtendedControllerApi mockApi = mock(ClassificationEntityExtendedControllerApi.class);
        when(classificationApisHolder.getClassificationEntityExtendedControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.deleteByOrganizationIdAndIuvAndIurAndTransferIndex(organizationId, iuv, iur, transferIndex)).thenReturn(expectedResponse);

        // When
        Long result = classificationClient.deleteByOrganizationIdAndIuvAndIurAndTransferIndex(organizationId, iuv, iur, transferIndex, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationApisHolder.getClassificationEntityExtendedControllerApi(accessToken), times(1))
                .deleteByOrganizationIdAndIuvAndIurAndTransferIndex(organizationId, iuv, iur, transferIndex);
    }
}