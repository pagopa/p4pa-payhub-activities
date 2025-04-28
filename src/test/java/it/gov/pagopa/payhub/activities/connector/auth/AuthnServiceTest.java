package it.gov.pagopa.payhub.activities.connector.auth;

import it.gov.pagopa.payhub.activities.connector.auth.service.AuthAccessTokenRetriever;
import it.gov.pagopa.pu.auth.dto.generated.AccessToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthnServiceTest {

    @Mock
    private AuthAccessTokenRetriever accessTokenRetrieverMock;

    private AuthnService authnService;

    @BeforeEach
    void init(){
        authnService = new AuthnServiceImpl(accessTokenRetrieverMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                accessTokenRetrieverMock
        );
    }

    @Test
    void givenNoOrgIpaCodewhenGetAccessTokenThenInvokeAccessTokenRetriever(){
        // Given
        String expectedResult = "TOKEN";
        Mockito.when(accessTokenRetrieverMock.getAccessToken(null))
                .thenReturn(AccessToken.builder().accessToken(expectedResult).tokenType("TOKENTYPE").expiresIn(0).build());

        // When
        String result = authnService.getAccessToken();

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenOrgIpaCodewhenGetAccessTokenThenInvokeAccessTokenRetriever(){
        // Given
        String expectedResult = "TOKEN";
        String orgIpaCode = "ORGIPACODE";
        Mockito.when(accessTokenRetrieverMock.getAccessToken(orgIpaCode))
                .thenReturn(AccessToken.builder().accessToken(expectedResult).tokenType("TOKENTYPE").expiresIn(0).build());

        // When
        String result = authnService.getAccessToken(orgIpaCode);

        // Then
        Assertions.assertSame(expectedResult, result);
    }
}
