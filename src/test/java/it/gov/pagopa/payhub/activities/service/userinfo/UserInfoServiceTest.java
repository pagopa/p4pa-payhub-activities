package it.gov.pagopa.payhub.activities.service.userinfo;


import it.gov.pagopa.pu.p4paauth.controller.ApiClient;
import it.gov.pagopa.pu.p4paauth.controller.generated.AuthnApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@EnableConfigurationProperties
class UserInfoServiceTest {
  private static final String AUTH_SERVER_BASE_URL = "https://hub.internal.dev.p4pa.pagopa.it/p4paauth/payhub";
  private static final String BEARER_TOKEN = "e1d9c534-86a9-4039-80da-8aa7a33ac9e7";

  @Autowired
  UserInfoService authorizationService;

  @MockBean
  ApiClient apiClient;
  @MockBean
  AuthnApi authnApi;

  String message = "";

  @BeforeEach
  void setUp() {
    apiClient = new ApiClient();
    apiClient.setBasePath(AUTH_SERVER_BASE_URL);
    apiClient.setBearerToken(BEARER_TOKEN);
    authnApi = new AuthnApi(apiClient);
    authorizationService = new UserInfoService(authnApi);
  }

  @Test
  void testGetUserInfo(){
    Exception exception = assertThrows(Exception.class,
            () -> authorizationService.getUserInfo(), "Error searching user info");

    try {
      message = exception.getMessage().substring(0,3);
      Assertions.assertEquals("403", message);
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
      Assertions.assertNotNull(e);
    }

  }

}

