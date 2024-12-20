package it.gov.pagopa.payhub.activities.connector.auth.client;

import it.gov.pagopa.payhub.activities.connector.auth.config.AuthApiHolder;
import it.gov.pagopa.pu.p4paauth.controller.generated.AuthzApi;
import it.gov.pagopa.pu.p4paauth.dto.generated.UserInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthzClientTest {
    @Mock
    private AuthApiHolder<AuthzApi> authzApiAuthApiHolderMock;
    @Mock
    private AuthzApi authzApiMock;

    private AuthzClient authzClient;

    @BeforeEach
    void setUp() {
        authzClient = new AuthzClient(authzApiAuthApiHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                authzApiAuthApiHolderMock
        );
    }

    @Test
    void whenGetOperatorInfoThenInvokeWithAccessToken(){
        // Given
        UserInfo expectedResult = new UserInfo();
        String accessToken = "accessToken";
        String externalUserId = "externalUserId";

        Mockito.when(authzApiAuthApiHolderMock.getAuthApi(accessToken))
                .thenReturn(authzApiMock);
        Mockito.when(authzApiMock.getUserInfoFromMappedExternaUserId(externalUserId))
                .thenReturn(expectedResult);

        // When
        UserInfo result = authzClient.getOperatorInfo(externalUserId, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }
}
