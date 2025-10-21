
package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.ClassificationClient;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.CollectionModelClassification;
import it.gov.pagopa.pu.classification.dto.generated.PagedModelClassificationEmbedded;
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
class ClassificationServiceTest {

    @Mock
    private ClassificationClient classificationClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private ClassificationServiceImpl classificationService;

    @BeforeEach
    void setUp() {
        classificationService = new ClassificationServiceImpl(classificationClientMock, authnServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                classificationClientMock,
                authnServiceMock);
    }

    @Test
    void testSaveAll() {
        // Given
        List<Classification> classificationList = List.of(new Classification());
        String accessToken = "accessToken";
        Integer expectedResponse = 1;

        when(classificationClientMock.saveAll(classificationList, accessToken)).thenReturn(expectedResponse);
        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        Integer result = classificationService.saveAll(classificationList);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationClientMock, times(1)).saveAll(classificationList, accessToken);
    }

    @Test
    void testDeleteByOrganizationIdAndIufAndLabel() {
        // Given
        Long organizationId = 1L;
        String iuf = "IUF123";
        ClassificationsEnum classification = ClassificationsEnum.RT_NO_IUF;
        Integer expectedResponse = 1;
        String accessToken = "accessToken";

        when(classificationClientMock.deleteByOrganizationIdAndIufAndLabel(organizationId, iuf, classification, accessToken)).thenReturn(expectedResponse);
        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        Integer result = classificationService.deleteByOrganizationIdAndIufAndLabel(organizationId, iuf, classification);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationClientMock, times(1)).deleteByOrganizationIdAndIufAndLabel(organizationId, iuf, classification, accessToken);
    }

    @Test
    void testDeleteBySemanticKey() {
        // Given
        TransferSemanticKeyDTO transferSemanticKeyDTO = new TransferSemanticKeyDTO(1L, "IUV123", "IUR123", 0);
        Integer expectedResponse = 1;
        String accessToken = "accessToken";
        Long organizationId = transferSemanticKeyDTO.getOrgId();
        String iuv = transferSemanticKeyDTO.getIuv();
        String iur = transferSemanticKeyDTO.getIur();
        int transferIndex = transferSemanticKeyDTO.getTransferIndex();


        when(classificationClientMock.deleteByOrganizationIdAndIuvAndIurAndTransferIndex(organizationId,iuv,iur,transferIndex,accessToken)).thenReturn(expectedResponse);
        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        Integer result = classificationService.deleteBySemanticKey(transferSemanticKeyDTO);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationClientMock, times(1)).deleteByOrganizationIdAndIuvAndIurAndTransferIndex(organizationId,iuv,iur,transferIndex,accessToken);
    }

	@Test
	void testDeleteByOrganizationIdAndIudAndLabel() {
        // Given
        Long organizationId = 1L;
        String iud = "IUD123";
        ClassificationsEnum classification = ClassificationsEnum.RT_NO_IUF;
        Integer expectedResponse = 1;
        String accessToken = "accessToken";

        when(classificationClientMock.deleteByOrganizationIdAndIudAndLabel(organizationId, iud, classification, accessToken)).thenReturn(expectedResponse);
        Mockito.when(authnServiceMock.getAccessToken())
            .thenReturn(accessToken);

        // When
        Integer result = classificationService.deleteByOrganizationIdAndIudAndLabel(organizationId, iud, classification);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationClientMock, times(1)).deleteByOrganizationIdAndIudAndLabel(organizationId, iud, classification, accessToken);
    }

    @Test
    void testDeleteByOrganizationIdAndTreasuryId() {
        // Given
        Long organizationId = 1L;
        String treasuryId = "TREASURY123";
        Integer expectedResponse = 1;
        String accessToken = "accessToken";

        when(classificationClientMock.deleteByOrganizationIdAndTreasuryId(organizationId, treasuryId, accessToken)).thenReturn(expectedResponse);
        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        Integer result = classificationService.deleteByOrganizationIdAndTreasuryId(organizationId, treasuryId);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationClientMock, times(1)).deleteByOrganizationIdAndTreasuryId(organizationId, treasuryId, accessToken);
    }

    @Test
    void testFindAllByOrganizationIdAndIuvAndIud() {
        // Given
        Long organizationId = 1L;
        String iuv = "testIUV";
        String iud = "testIUD";
        String accessToken = "accessToken";

        CollectionModelClassification expectedResponse = new CollectionModelClassification();
        Classification classification = new Classification();
        classification.setOrganizationId(organizationId);
        classification.setIuv(iuv);
        classification.setIud(iud);
        expectedResponse.setEmbedded(new PagedModelClassificationEmbedded(List.of(classification)));

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        when(classificationClientMock.findAllByOrganizationIdAndIuvAndIud(organizationId, iuv, iud, accessToken))
                .thenReturn(expectedResponse);

        // When
        CollectionModelClassification actualResult = classificationService.findAllByOrganizationIdAndIuvAndIud(organizationId, iuv, iud);

        // Then
        assertEquals(expectedResponse, actualResult);
    }
}
