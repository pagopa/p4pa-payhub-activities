package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.connector.auth.AuthzService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker;
import it.gov.pagopa.payhub.activities.util.faker.OrganizationFaker;
import it.gov.pagopa.payhub.activities.util.faker.UserInfoFaker;
import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void givenNoOrganizationWhenConfigureThenOk() {
        // Given
        EmailDTO emailDTO = new EmailDTO();
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
        UserInfo userInfo = UserInfoFaker.buildUserInfo();

        Mockito.when(authzServiceMock.getOperatorInfo(ingestionFlowFileDTO.getOperatorExternalId()))
                .thenReturn(userInfo);

        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFileDTO.getOrganizationId()))
                .thenReturn(Optional.empty());

        // When
        service.configure(ingestionFlowFileDTO, emailDTO);

        // Then
        Assertions.assertEquals(1, emailDTO.getTo().length);
        Assertions.assertEquals(userInfo.getEmail(), emailDTO.getTo()[0]);
        Assertions.assertNull(emailDTO.getCc());
    }

    @Test
    void givenOrganizationWithSameEmailWhenConfigureThenOk() {
        // Given
        EmailDTO emailDTO = new EmailDTO();
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
        UserInfo userInfo = UserInfoFaker.buildUserInfo();
        Organization organizationDTO = OrganizationFaker.buildOrganizationDTO();
        organizationDTO.setOrgEmail(userInfo.getEmail());

        Mockito.when(authzServiceMock.getOperatorInfo(ingestionFlowFileDTO.getOperatorExternalId()))
                .thenReturn(userInfo);

        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFileDTO.getOrganizationId()))
                .thenReturn(Optional.of(organizationDTO));

        // When
        service.configure(ingestionFlowFileDTO, emailDTO);

        // Then
        Assertions.assertEquals(1, emailDTO.getTo().length);
        Assertions.assertEquals(userInfo.getEmail(), emailDTO.getTo()[0]);
        Assertions.assertNull(emailDTO.getCc());
    }

    @Test
    void givenOrganizationWithDifferentEmailWhenConfigureThenOk() {
        // Given
        EmailDTO emailDTO = new EmailDTO();
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
        UserInfo userInfo = UserInfoFaker.buildUserInfo();
        Organization organizationDTO = OrganizationFaker.buildOrganizationDTO();

        Mockito.when(authzServiceMock.getOperatorInfo(ingestionFlowFileDTO.getOperatorExternalId()))
                .thenReturn(userInfo);

        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFileDTO.getOrganizationId()))
                .thenReturn(Optional.of(organizationDTO));

        // When
        service.configure(ingestionFlowFileDTO, emailDTO);

        // Then
        Assertions.assertEquals(1, emailDTO.getTo().length);
        Assertions.assertEquals(userInfo.getEmail(), emailDTO.getTo()[0]);
        Assertions.assertEquals(1, emailDTO.getCc().length);
        Assertions.assertEquals(organizationDTO.getOrgEmail(), emailDTO.getCc()[0]);
    }
}
