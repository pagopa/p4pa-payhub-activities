package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class to cache IPA codes of organizations by their IDs.
 * It uses a LinkedHashMap to maintain the order of insertion and implements a simple LRU cache mechanism.
 */
@Service
@RequiredArgsConstructor
public class OrganizationIpaCacheUtils {

    private static final int CACHE_SIZE = 128;
    private static final int TO_REMOVE = CACHE_SIZE / 4;

    private static final Map<Long, String> idToIpaMap = new LinkedHashMap<>(CACHE_SIZE, 1f, true);

    private final OrganizationService organizationService;

    public String getIpaById(Long id) {
        String ipa = idToIpaMap.get(id);
        if (ipa == null) {
            ipa = loadOrganizationByIdCode(id);
        }
        return ipa;
    }

    private synchronized String loadOrganizationByIdCode(Long id) {
        String ipa = idToIpaMap.get(id);
        if (ipa == null) {
            Optional<Organization> optionalOrganization = organizationService.getOrganizationById(id);
            if (optionalOrganization.isEmpty()) {
                throw new IllegalArgumentException("Organization with id " + id + " not found");
            }
            if (idToIpaMap.size() == CACHE_SIZE) {
                removeOldestEntries();
            }
            ipa = optionalOrganization.get().getIpaCode();
            idToIpaMap.put(id, ipa);
        }
        return ipa;
    }

    private synchronized void removeOldestEntries() {
        Iterator<Map.Entry<Long, String>> iterator = idToIpaMap.entrySet().iterator();
        int removed = 0;
        while (iterator.hasNext() && removed < TO_REMOVE) {
            iterator.next();
            iterator.remove();
            removed++;
        }
    }

    synchronized void clear() {
        idToIpaMap.clear();
    }
}
