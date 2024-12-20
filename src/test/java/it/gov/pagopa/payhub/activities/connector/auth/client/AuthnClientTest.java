package it.gov.pagopa.payhub.activities.connector.auth.client;

import it.gov.pagopa.payhub.activities.connector.auth.config.AuthApiHolder;
import it.gov.pagopa.pu.p4paauth.controller.generated.AuthnApi;
import it.gov.pagopa.pu.p4paauth.dto.generated.AccessToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthnClientTest {
    @Mock
    private AuthApiHolder<AuthnApi> authnApiAuthApiHolderMock;
    @Mock
    private AuthnApi authnApiMock;

    private AuthnClient authnClient;

    @BeforeEach
    void setUp() {
        authnClient = new AuthnClient(authnApiAuthApiHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                authnApiAuthApiHolderMock
        );
    }

    @Test
    void whenGetOperatorInfoThenInvokeWithAccessToken(){
        // Given
        AccessToken expectedResult = new AccessToken();

        String clientId = "clientId";
        String grantType = "grantType";
        String scope = "scope";
        String subjectToken = "subjectToken";
        String subjectIssuer = "subjectIssuer";
        String subjectTokenType = "subjectTokenType";
        String clientSecret = "clientSecret";

        Mockito.when(authnApiAuthApiHolderMock.getAuthApi(null))
                .thenReturn(authnApiMock);
        Mockito.when(authnApiMock.postToken(clientId, grantType, scope, subjectToken, subjectIssuer, subjectTokenType, clientSecret))
                .thenReturn(expectedResult);

        // When
        AccessToken result = authnClient.postToken(clientId, grantType, scope, subjectToken, subjectIssuer, subjectTokenType, clientSecret);

        // Then
        Assertions.assertSame(expectedResult, result);
    }
}
