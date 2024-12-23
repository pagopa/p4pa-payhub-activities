package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.connector.auth.AuthzService;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.service.OrganizationService;
import it.gov.pagopa.payhub.activities.utility.faker.IngestionFlowFileFaker;
import it.gov.pagopa.payhub.activities.utility.faker.OrganizationFaker;
import it.gov.pagopa.pu.p4paauth.dto.generated.UserInfo;
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
        IngestionFlowFileDTO ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFileDTO();
        UserInfo userInfo = UserInfo.builder()
                .email("user@email.it")
                .build();

        Mockito.when(authzServiceMock.getOperatorInfo(ingestionFlowFileDTO.getMappedExternalUserId()))
                .thenReturn(userInfo);

        Mockito.when(organizationServiceMock.getOrganizationByIpaCode(ingestionFlowFileDTO.getOrg().getIpaCode()))
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
        IngestionFlowFileDTO ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFileDTO();
        UserInfo userInfo = UserInfo.builder()
                .email("user@email.it")
                .build();
        OrganizationDTO organizationDTO = OrganizationFaker.buildOrganizationDTO();
        organizationDTO.setAdminEmail(userInfo.getEmail());

        Mockito.when(authzServiceMock.getOperatorInfo(ingestionFlowFileDTO.getMappedExternalUserId()))
                .thenReturn(userInfo);

        Mockito.when(organizationServiceMock.getOrganizationByIpaCode(ingestionFlowFileDTO.getOrg().getIpaCode()))
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
        IngestionFlowFileDTO ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFileDTO();
        UserInfo userInfo = UserInfo.builder()
                .email("user@email.it")
                .build();
        OrganizationDTO organizationDTO = OrganizationFaker.buildOrganizationDTO();

        Mockito.when(authzServiceMock.getOperatorInfo(ingestionFlowFileDTO.getMappedExternalUserId()))
                .thenReturn(userInfo);

        Mockito.when(organizationServiceMock.getOrganizationByIpaCode(ingestionFlowFileDTO.getOrg().getIpaCode()))
                .thenReturn(Optional.of(organizationDTO));

        // When
        service.configure(ingestionFlowFileDTO, emailDTO);

        // Then
        Assertions.assertEquals(1, emailDTO.getTo().length);
        Assertions.assertEquals(userInfo.getEmail(), emailDTO.getTo()[0]);
        Assertions.assertEquals(1, emailDTO.getCc().length);
        Assertions.assertEquals(organizationDTO.getAdminEmail(), emailDTO.getCc()[0]);
    }
}
