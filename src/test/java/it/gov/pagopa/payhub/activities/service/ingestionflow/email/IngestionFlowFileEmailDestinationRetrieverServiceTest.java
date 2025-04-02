package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.connector.auth.AuthzService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker;
import it.gov.pagopa.payhub.activities.util.faker.OrganizationFaker;
import it.gov.pagopa.payhub.activities.util.faker.UserInfoFaker;
import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class IngestionFlowFileEmailDestinationRetrieverServiceTest {

    @Mock
    private AuthzService authzServiceMock;
    @Mock
    private OrganizationService organizationServiceMock;

    private IngestionFlowFileEmailDestinationRetrieverService service;

    @BeforeEach
    void init() {
        this.service = new IngestionFlowFileEmailDestinationRetrieverService(authzServiceMock, organizationServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                authzServiceMock,
                organizationServiceMock
        );
    }

    @Test
    void givenNoOrganizationWhenRetrieveEmailDestinationsThenOk() {
        // Given
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
        ingestionFlowFileDTO.setOrganizationId(0L);
        UserInfo userInfo = UserInfoFaker.buildUserInfo();

        Mockito.when(authzServiceMock.getOperatorInfo(ingestionFlowFileDTO.getOperatorExternalId()))
                .thenReturn(userInfo);

        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFileDTO.getOrganizationId()))
                .thenReturn(Optional.empty());

        // When
        Pair<String[], String[]> result = service.retrieveEmailDestinations(ingestionFlowFileDTO);

        // Then
        String[] to = result.getLeft();
        String[] cc = result.getRight();

        Assertions.assertEquals(1, to.length);
        Assertions.assertNull(to[0]);
        Assertions.assertNull(cc);
    }

    @Test
    void givenOrganizationWithSameEmailWhenRetrieveEmailDestinationsThenOk() {
        // Given
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
        UserInfo userInfo = UserInfoFaker.buildUserInfo();
        Organization organizationDTO = OrganizationFaker.buildOrganizationDTO();
        organizationDTO.setOrgEmail("user@email.it");
        ingestionFlowFileDTO.setOrganizationId(Objects.requireNonNull(organizationDTO.getOrganizationId()));

        Mockito.when(authzServiceMock.getOperatorInfo(ingestionFlowFileDTO.getOperatorExternalId()))
                .thenReturn(userInfo);

        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFileDTO.getOrganizationId()))
                .thenReturn(Optional.of(organizationDTO));

        // When
        Pair<String[], String[]> result = service.retrieveEmailDestinations(ingestionFlowFileDTO);

        // Then
        String[] to = result.getLeft();
        String[] cc = result.getRight();

        Assertions.assertEquals(1, to.length);
        Assertions.assertEquals("user@email.it", to[0]);
        Assertions.assertNull(cc);
    }

    @Test
    void givenOrganizationWithDifferentEmailWhenRetrieveEmailDestinationsThenOk() {
        // Given
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
        UserInfo userInfo = UserInfoFaker.buildUserInfo();
        Organization organizationDTO = OrganizationFaker.buildOrganizationDTO();
        ingestionFlowFileDTO.setOrganizationId(Objects.requireNonNull(organizationDTO.getOrganizationId()));

        Mockito.when(authzServiceMock.getOperatorInfo(ingestionFlowFileDTO.getOperatorExternalId()))
                .thenReturn(userInfo);

        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFileDTO.getOrganizationId()))
                .thenReturn(Optional.of(organizationDTO));

        // When
        Pair<String[], String[]> result = service.retrieveEmailDestinations(ingestionFlowFileDTO);

        // Then
        String[] to = result.getLeft();
        String[] cc = result.getRight();

        Assertions.assertEquals(1, to.length);
        Assertions.assertEquals("user@email.it", to[0]);
        Assertions.assertEquals(1, cc.length);
        Assertions.assertEquals(organizationDTO.getOrgEmail(), cc[0]);
    }
}
