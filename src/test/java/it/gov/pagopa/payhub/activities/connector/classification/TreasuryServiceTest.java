package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.TreasuryClient;
import it.gov.pagopa.payhub.activities.connector.classification.mapper.TreasuryMapper;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreasuryServiceTest {

    @Mock
    private TreasuryClient treasuryClientMock;
    @Mock
    private AuthnService authnServiceMock;
    @Mock
    private TreasuryMapper treasuryMapperMock;

    private TreasuryServiceImpl treasuryService;

    @BeforeEach
    void setUp() {
        treasuryService = new TreasuryServiceImpl(treasuryClientMock, authnServiceMock, treasuryMapperMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                treasuryClientMock,
                authnServiceMock,
                treasuryMapperMock);
    }

    @Test
    void testFindByOrganizationIdAndIuf() {
        // Given
        Long organizationId = 1L;
        String iuf = "IUF123";
        String accessToken = "accessToken";
        Treasury expectedTreasury = new Treasury();
        TreasuryIuf expectedTreasuryIuf = new TreasuryIuf();
        when(treasuryClientMock.findByOrganizationIdAndIuf(organizationId, iuf, accessToken))
                .thenReturn(expectedTreasury);
        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        Mockito.when(treasuryMapperMock.map2Iuf(expectedTreasury))
                .thenReturn(expectedTreasuryIuf);

        // When
        Treasury result = treasuryService.getByOrganizationIdAndIuf(organizationId, iuf);

        // Then
        assertSame(expectedTreasuryIuf, result);
        verify(treasuryClientMock, times(1)).findByOrganizationIdAndIuf(organizationId, iuf, accessToken);
    }

    @Test
    void testInsert() {
        // Given
        Treasury treasury = new Treasury();
        String accessToken = "accessToken";
        Treasury expectedTreasury = new Treasury();
        when(treasuryClientMock.insert(treasury, accessToken)).thenReturn(expectedTreasury);
        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        Treasury result = treasuryService.insert(treasury);

        // Then
        assertSame(expectedTreasury, result);
        verify(treasuryClientMock, times(1)).insert(treasury, accessToken);
    }

    @Test
    void testDeleteByOrganizationIdAndBillCodeAndBillYear() {
        // Given
        Long organizationId = 1L;
        String billCode = "BILL123";
        String billYear = "2023";
        String accessToken = "accessToken";
        Long expectedDeletedCount = 1L;
        when(treasuryClientMock.deleteByOrganizationIdAndBillCodeAndBillYear(organizationId, billCode, billYear, accessToken))
                .thenReturn(expectedDeletedCount);
        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        Long result = treasuryService.deleteByOrganizationIdAndBillCodeAndBillYear(organizationId, billCode, billYear);

        // Then
        assertSame(expectedDeletedCount, result);
        verify(treasuryClientMock, times(1)).deleteByOrganizationIdAndBillCodeAndBillYear(organizationId, billCode, billYear, accessToken);
    }
}