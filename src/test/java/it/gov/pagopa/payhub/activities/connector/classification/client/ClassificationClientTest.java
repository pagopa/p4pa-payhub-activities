package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.client.generated.ClassificationEntityExtendedControllerApi;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
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
    private ClassificationApisHolder classificationApisHolderMock;

    private ClassificationClient classificationClient;

    @BeforeEach
    void setUp() {
        classificationClient = new ClassificationClient(classificationApisHolderMock);
    }
    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(classificationApisHolderMock);
    }


    @Test
    void testSaveAll() {
        // Given
        List<Classification> classificationList = List.of(new Classification());
        String accessToken = "accessToken";
        Integer expectedResponse = 1;
        ClassificationEntityExtendedControllerApi mockApi = mock(ClassificationEntityExtendedControllerApi.class);
        when(classificationApisHolderMock.getClassificationEntityExtendedControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.saveAll2(classificationList)).thenReturn(expectedResponse);

        // When
        Integer result = classificationClient.saveAll(classificationList, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationApisHolderMock.getClassificationEntityExtendedControllerApi(accessToken), times(1))
                .saveAll2(classificationList);
    }


    @Test
    void testDeleteByOrganizationIdAndIufAndLabel() {
        // Given
        Long organizationId = 1L;
        String iuf = "IUF123";
        ClassificationsEnum classification = ClassificationsEnum.RT_IUF;
        String accessToken = "accessToken";
        Integer expectedResponse = 1;
        ClassificationEntityExtendedControllerApi mockApi = mock(ClassificationEntityExtendedControllerApi.class);
        when(classificationApisHolderMock.getClassificationEntityExtendedControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.deleteByOrganizationIdAndIufAndLabel(organizationId, iuf, classification)).thenReturn(expectedResponse);

        // When
        Integer result = classificationClient.deleteByOrganizationIdAndIufAndLabel(organizationId, iuf, classification, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationApisHolderMock.getClassificationEntityExtendedControllerApi(accessToken), times(1))
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
        Integer expectedResponse = 1;
        ClassificationEntityExtendedControllerApi mockApi = mock(ClassificationEntityExtendedControllerApi.class);
        when(classificationApisHolderMock.getClassificationEntityExtendedControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.deleteByOrganizationIdAndIuvAndIurAndTransferIndex(organizationId, iuv, iur, transferIndex)).thenReturn(expectedResponse);

        // When
        Integer result = classificationClient.deleteByOrganizationIdAndIuvAndIurAndTransferIndex(organizationId, iuv, iur, transferIndex, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationApisHolderMock.getClassificationEntityExtendedControllerApi(accessToken), times(1))
                .deleteByOrganizationIdAndIuvAndIurAndTransferIndex(organizationId, iuv, iur, transferIndex);
    }

	@Test
	void testDeleteByOrganizationIdAndIudAndLabel() {
        // Given
        Long organizationId = 1L;
        String iud = "IUD123";
        ClassificationsEnum classification = ClassificationsEnum.RT_IUF;
        String accessToken = "accessToken";
        Integer expectedResponse = 1;
        ClassificationEntityExtendedControllerApi mockApi = mock(ClassificationEntityExtendedControllerApi.class);
        when(classificationApisHolderMock.getClassificationEntityExtendedControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.deleteByOrganizationIdAndIudAndLabel(organizationId, iud, classification)).thenReturn(expectedResponse);

        // When
        Integer result = classificationClient.deleteByOrganizationIdAndIudAndLabel(organizationId, iud, classification, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationApisHolderMock.getClassificationEntityExtendedControllerApi(accessToken), times(1))
            .deleteByOrganizationIdAndIudAndLabel(organizationId, iud, classification);
	}

    @Test
    void testDeleteByOrganizationIdAndTreasuryId() {
        // Given
        Long organizationId = 1L;
        String treasuryId = "TREASURY123";
        String accessToken = "accessToken";
        Integer expectedResponse = 1;
        ClassificationEntityExtendedControllerApi mockApi = mock(ClassificationEntityExtendedControllerApi.class);
        when(classificationApisHolderMock.getClassificationEntityExtendedControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.deleteByOrganizationIdAndTreasuryId(organizationId, treasuryId)).thenReturn(expectedResponse);

        // When
        Integer result = classificationClient.deleteByOrganizationIdAndTreasuryId(organizationId, treasuryId, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationApisHolderMock.getClassificationEntityExtendedControllerApi(accessToken), times(1))
                .deleteByOrganizationIdAndTreasuryId(organizationId, treasuryId);
    }
}