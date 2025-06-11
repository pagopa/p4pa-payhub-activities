package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationIpaCacheUtilsTest {

    @Mock
    private OrganizationService organizationService;

    @InjectMocks
    private OrganizationIpaCacheUtils cacheUtils;

    @BeforeEach
    void setup() {
        cacheUtils.clear();
    }

    @Test
    void testGetIpaById_LoadsAndCaches() {
        Long orgId = 123L;
        String expectedIpa = "IPA123";

        Organization org = mock(Organization.class);
        when(org.getIpaCode()).thenReturn(expectedIpa);
        when(organizationService.getOrganizationById(orgId)).thenReturn(Optional.of(org));

        String ipaFirstCall = cacheUtils.getIpaById(orgId);
        String ipaSecondCall = cacheUtils.getIpaById(orgId);

        assertEquals(expectedIpa, ipaFirstCall);
        assertEquals(expectedIpa, ipaSecondCall);

        verify(organizationService, times(1)).getOrganizationById(orgId);
    }

    @Test
    void testGetIpaById_ThrowsWhenNotFound() {
        Long missingId = 999L;

        when(organizationService.getOrganizationById(missingId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> cacheUtils.getIpaById(missingId));

        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void testCacheEviction_removesOldestEntries() {
        for (long i = 0; i < 130; i++) {
            Organization org = mock(Organization.class);
            when(org.getIpaCode()).thenReturn("IPA" + i);
            when(organizationService.getOrganizationById(i)).thenReturn(Optional.of(org));

            cacheUtils.getIpaById(i);
        }

        cacheUtils.getIpaById(0L);

        verify(organizationService, atLeast(2)).getOrganizationById(0L);
    }
}
