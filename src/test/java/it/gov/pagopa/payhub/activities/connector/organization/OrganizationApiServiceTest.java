package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.OrganizationApiClient;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationApiServiceTest {

  @Mock
  private AuthnService authnServiceMock;
  @Mock
  private OrganizationApiClient organizationApiClientMock;

  private OrganizationApiService service;

  private final String accessToken = "ACCESSTOKEN";

  @BeforeEach
  void init() {
    service = new OrganizationApiServiceImpl(
        authnServiceMock,
        organizationApiClientMock);

    Mockito.when(authnServiceMock.getAccessToken())
        .thenReturn(accessToken);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
        authnServiceMock,
        organizationApiClientMock
    );
  }
    @Test
    void encryptAndSaveApiKeyShouldCallClientWithCorrectParameters() {
        Long organizationId = 1L;
        OrganizationApiKeys apiKeys = new OrganizationApiKeys();

        service.encryptAndSaveApiKey(organizationId, apiKeys);

        Mockito.verify(organizationApiClientMock).encryptAndSaveApiKey(organizationId, apiKeys, accessToken);
    }

}
