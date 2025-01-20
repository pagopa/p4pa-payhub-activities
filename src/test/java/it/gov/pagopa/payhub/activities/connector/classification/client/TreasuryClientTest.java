package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.TreasuryApisHolder;
import it.gov.pagopa.pu.classification.client.generated.TreasuryEntityControllerApi;
import it.gov.pagopa.pu.classification.client.generated.TreasuryEntityExtendedControllerApi;
import it.gov.pagopa.pu.classification.client.generated.TreasurySearchControllerApi;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreasuryClientTest {

    @Mock
    private TreasuryApisHolder treasuryApisHolder;

    private TreasuryClient treasuryClient;

    @BeforeEach
    void setUp() {
        treasuryClient = new TreasuryClient(treasuryApisHolder);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(treasuryApisHolder);
    }

    @Test
    void testFindByOrganizationIdAndIuf() {
        // Given
        Long organizationId = 1L;
        String iuf = "IUF123";
        String accessToken = "accessToken";
        Treasury expectedTreasury = new Treasury();
        TreasurySearchControllerApi mockApi = mock(TreasurySearchControllerApi.class);
        when(treasuryApisHolder.getTreasurySearchApi(accessToken)).thenReturn(mockApi);
        when(mockApi.crudTreasuryGetByOrganizationIdAndIuf(organizationId, iuf)).thenReturn(expectedTreasury);

        // When
        Treasury result = treasuryClient.findByOrganizationIdAndIuf(organizationId, iuf, accessToken);

        // Then
        assertEquals(expectedTreasury, result);
        verify(treasuryApisHolder.getTreasurySearchApi(accessToken), times(1))
                .crudTreasuryGetByOrganizationIdAndIuf(organizationId, iuf);
    }

    @Test
    void testGetByOrganizationIdAndBillCodeAndBillYear() {
        // Given
        Long organizationId = 1L;
        String billCode = "BILL123";
        String billYear = "2023";
        String accessToken = "accessToken";
        Treasury expectedTreasury = new Treasury();
        TreasurySearchControllerApi mockApi = mock(TreasurySearchControllerApi.class);
        when(treasuryApisHolder.getTreasurySearchApi(accessToken)).thenReturn(mockApi);
        when(mockApi.crudTreasuryFindBySemanticKey(organizationId, billCode, billYear)).thenReturn(expectedTreasury);

        // When
        Treasury result = treasuryClient.getBySemanticKey(organizationId, billCode, billYear, accessToken);

        // Then
        assertEquals(expectedTreasury, result);
        verify(treasuryApisHolder.getTreasurySearchApi(accessToken), times(1))
                .crudTreasuryFindBySemanticKey(organizationId, billCode, billYear);
    }

    @Test
    void testInsert() {
        // Given
        Treasury treasury = new Treasury();
        String accessToken = "accessToken";
        Treasury expectedTreasury = new Treasury();
        TreasuryEntityControllerApi mockApi = mock(TreasuryEntityControllerApi.class);
        when(treasuryApisHolder.getTreasuryEntityControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.crudCreateTreasury(any())).thenReturn(expectedTreasury);

        // When
        Treasury result = treasuryClient.insert(treasury, accessToken);

        // Then
        assertEquals(expectedTreasury, result);
        verify(treasuryApisHolder.getTreasuryEntityControllerApi(accessToken), times(1)).crudCreateTreasury(any());
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
        when(treasuryApisHolder.getTreasuryEntityExtendedControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.deleteByOrganizationIdAndBillCodeAndBillYear(organizationId, billCode, billYear)).thenReturn(expectedDeletedCount);

        // When
        Long result = treasuryClient.deleteByOrganizationIdAndBillCodeAndBillYear(organizationId, billCode, billYear, accessToken);

        // Then
        assertEquals(expectedDeletedCount, result);
        verify(treasuryApisHolder.getTreasuryEntityExtendedControllerApi(accessToken), times(1))
                .deleteByOrganizationIdAndBillCodeAndBillYear(organizationId, billCode, billYear);
    }
}