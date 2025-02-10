package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.TreasuryApisHolder;
import it.gov.pagopa.payhub.activities.connector.classification.mapper.TreasuryRequestMapper;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.classification.client.generated.TreasuryEntityControllerApi;
import it.gov.pagopa.pu.classification.client.generated.TreasuryEntityExtendedControllerApi;
import it.gov.pagopa.pu.classification.client.generated.TreasurySearchControllerApi;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.classification.dto.generated.TreasuryRequestBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreasuryClientTest {

    @Mock
    private TreasuryApisHolder treasuryApisHolderMock;
    @Mock
    private TreasuryRequestMapper mapperMock;

    private TreasuryClient treasuryClient;

    @BeforeEach
    void setUp() {
        treasuryClient = new TreasuryClient(treasuryApisHolderMock, mapperMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(treasuryApisHolderMock, mapperMock);
    }

    @Test
    void whenFindByOrganizationIdAndIufThenOk() {
        // Given
        Long organizationId = 1L;
        String iuf = "IUF123";
        String accessToken = "accessToken";
        Treasury expectedTreasury = new Treasury();
        TreasurySearchControllerApi mockApi = mock(TreasurySearchControllerApi.class);
        when(treasuryApisHolderMock.getTreasurySearchApi(accessToken)).thenReturn(mockApi);
        when(mockApi.crudTreasuryGetByOrganizationIdAndIuf(organizationId, iuf)).thenReturn(expectedTreasury);

        // When
        Treasury result = treasuryClient.findByOrganizationIdAndIuf(organizationId, iuf, accessToken);

        // Then
        assertEquals(expectedTreasury, result);
        verify(treasuryApisHolderMock.getTreasurySearchApi(accessToken), times(1))
                .crudTreasuryGetByOrganizationIdAndIuf(organizationId, iuf);
    }

    @Test
    void givenNotExistentTreasuryWhenFindByOrganizationIdAndIufThenNull() {
        // Given
        Long organizationId = 1L;
        String iuf = "IUF123";
        String accessToken = "accessToken";
        TreasurySearchControllerApi mockApi = mock(TreasurySearchControllerApi.class);
        when(treasuryApisHolderMock.getTreasurySearchApi(accessToken)).thenReturn(mockApi);
        when(mockApi.crudTreasuryGetByOrganizationIdAndIuf(organizationId, iuf))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // When
        Treasury result = treasuryClient.findByOrganizationIdAndIuf(organizationId, iuf, accessToken);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void whenGetByOrganizationIdAndBillCodeAndBillYearThenOk() {
        // Given
        Long organizationId = 1L;
        String billCode = "BILL123";
        String billYear = "2023";
        String accessToken = "accessToken";
        Treasury expectedTreasury = new Treasury();
        TreasurySearchControllerApi mockApi = mock(TreasurySearchControllerApi.class);
        when(treasuryApisHolderMock.getTreasurySearchApi(accessToken)).thenReturn(mockApi);
        when(mockApi.crudTreasuryFindBySemanticKey(organizationId, billCode, billYear)).thenReturn(expectedTreasury);

        // When
        Treasury result = treasuryClient.getBySemanticKey(organizationId, billCode, billYear, accessToken);

        // Then
        assertEquals(expectedTreasury, result);
        verify(treasuryApisHolderMock.getTreasurySearchApi(accessToken), times(1))
                .crudTreasuryFindBySemanticKey(organizationId, billCode, billYear);
    }

    @Test
    void givenNotExistentTreasuryWhenGetByOrganizationIdAndBillCodeAndBillYearThenNull() {
        // Given
        Long organizationId = 1L;
        String billCode = "BILL123";
        String billYear = "2023";
        String accessToken = "accessToken";
        TreasurySearchControllerApi mockApi = mock(TreasurySearchControllerApi.class);
        when(treasuryApisHolderMock.getTreasurySearchApi(accessToken)).thenReturn(mockApi);
        when(mockApi.crudTreasuryFindBySemanticKey(organizationId, billCode, billYear))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // When
        Treasury result = treasuryClient.getBySemanticKey(organizationId, billCode, billYear, accessToken);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void testInsert() {
        // Given
        Treasury treasury = TreasuryFaker.buildTreasuryDTO();
        String accessToken = "accessToken";
        Treasury expectedTreasury = TreasuryFaker.buildTreasuryDTO();
        TreasuryEntityControllerApi mockApi = mock(TreasuryEntityControllerApi.class);
        when(mapperMock.map(treasury)).thenReturn(mock(TreasuryRequestBody.class));
        when(treasuryApisHolderMock.getTreasuryEntityControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.crudCreateTreasury(any())).thenReturn(expectedTreasury);

        // When
        Treasury result = treasuryClient.insert(treasury, accessToken);

        // Then
        assertEquals(expectedTreasury, result);
        verify(treasuryApisHolderMock.getTreasuryEntityControllerApi(accessToken), times(1)).crudCreateTreasury(any());
    }

    @Test
    void testDeleteByOrganizationIdAndBillCodeAndBillYear() {
        // Given
        Long organizationId = 1L;
        String billCode = "BILL123";
        String billYear = "2023";
        String accessToken = "accessToken";
        Long expectedDeletedCount = 1L;
        TreasuryEntityExtendedControllerApi mockApi = mock(TreasuryEntityExtendedControllerApi.class);
        when(treasuryApisHolderMock.getTreasuryEntityExtendedControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.deleteByOrganizationIdAndBillCodeAndBillYear(organizationId, billCode, billYear)).thenReturn(expectedDeletedCount);

        // When
        Long result = treasuryClient.deleteByOrganizationIdAndBillCodeAndBillYear(organizationId, billCode, billYear, accessToken);

        // Then
        assertEquals(expectedDeletedCount, result);
        verify(treasuryApisHolderMock.getTreasuryEntityExtendedControllerApi(accessToken), times(1))
                .deleteByOrganizationIdAndBillCodeAndBillYear(organizationId, billCode, billYear);
    }
}