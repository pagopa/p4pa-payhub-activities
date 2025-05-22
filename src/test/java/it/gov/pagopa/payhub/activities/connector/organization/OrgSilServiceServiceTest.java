package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.OrgSilServiceClient;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceRequestBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrgSilServiceServiceTest {

  @Mock
  private AuthnService authnServiceMock;
  @Mock
  private OrgSilServiceClient orgSilServiceClientMock;

  private OrgSilServiceService service;

  private final String accessToken = "ACCESSTOKEN";

  @BeforeEach
  void init() {
    service = new OrgSilServiceServiceImpl(
        authnServiceMock,
        orgSilServiceClientMock);

    Mockito.when(authnServiceMock.getAccessToken())
        .thenReturn(accessToken);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
        authnServiceMock,
        orgSilServiceClientMock
    );
  }
    @Test
    void givenOrgSilServiceRequestBodyWhenCreateOrgSilServiceThenReturnOrgSilService() {
      // Given
      OrgSilServiceRequestBody requestBody = new OrgSilServiceRequestBody();
      OrgSilService expectedOrgSilService = new OrgSilService();
      Mockito.when(orgSilServiceClientMock.createOrgSilService(requestBody, accessToken))
          .thenReturn(expectedOrgSilService);

      // When
      OrgSilService result = service.createOrgSilService(requestBody);

      // Then
      Assertions.assertSame(expectedOrgSilService, result);
      Mockito.verify(orgSilServiceClientMock).createOrgSilService(requestBody, accessToken);
    }

}
