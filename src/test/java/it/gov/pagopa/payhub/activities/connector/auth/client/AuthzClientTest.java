package it.gov.pagopa.payhub.activities.connector.auth.client;

import it.gov.pagopa.payhub.activities.connector.auth.config.AuthApisHolder;
import it.gov.pagopa.payhub.activities.exception.common.RestInvokeNotFoundException;
import it.gov.pagopa.pu.auth.client.generated.AuthzApi;
import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthzClientTest {

    @Mock
    private AuthApisHolder authApisHolderMock;
    @Mock
    private AuthzApi authzApiMock;

    private AuthzClient authzClient;

    @BeforeEach
    void setUp() {
        authzClient = new AuthzClient(authApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                authApisHolderMock
        );
    }

    @Test
    void whenGetOperatorInfoThenInvokeWithAccessToken(){
        // Given
        UserInfo expectedResult = new UserInfo();
        String accessToken = "accessToken";
        String externalUserId = "externalUserId";

        when(authApisHolderMock.getAuthzApi(accessToken))
                .thenReturn(authzApiMock);
        when(authzApiMock.getUserInfoFromMappedExternaUserId(externalUserId))
                .thenReturn(expectedResult);

        // When
        UserInfo result = authzClient.getOperatorInfo(externalUserId, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotExistentUsedWhenGetOperatorInfoThenNull(){
        // Given
        String accessToken = "accessToken";
        String externalUserId = "externalUserId";

        when(authApisHolderMock.getAuthzApi(accessToken))
                .thenReturn(authzApiMock);
        when(authzApiMock.getUserInfoFromMappedExternaUserId(externalUserId))
                .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"));

        // When
        UserInfo result = authzClient.getOperatorInfo(externalUserId, accessToken);

        // Then
        Assertions.assertNull(result);
    }
}
