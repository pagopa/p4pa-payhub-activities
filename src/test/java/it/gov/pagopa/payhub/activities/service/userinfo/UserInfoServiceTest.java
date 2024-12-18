package it.gov.pagopa.payhub.activities.service.userinfo;


import it.gov.pagopa.pu.p4paauth.controller.ApiClient;
import it.gov.pagopa.pu.p4paauth.controller.generated.AuthnApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@EnableConfigurationProperties
class UserInfoServiceTest {
  private static final String AUTH_SERVER_BASE_URL = "https://localhost:8080/p4paauth/payhub";
  private static final String BEARER_TOKEN = "BEARER_TOKEN";

  @Autowired
  UserInfoService authorizationService;

    @BeforeEach
  void setUp() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(AUTH_SERVER_BASE_URL);
    apiClient.setBearerToken(BEARER_TOKEN);
    AuthnApi authnApi = new AuthnApi(apiClient);
    authorizationService = new UserInfoService(authnApi);
  }

  @Test
  void testGetUserInfo(){
    assertThrows(Exception.class,
        () -> authorizationService.getUserInfo(), "Error searching user info");
  }

}

