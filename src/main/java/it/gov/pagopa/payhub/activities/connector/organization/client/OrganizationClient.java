package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class OrganizationClient {

    private final OrganizationApisHolder organizationApisHolder;

    public OrganizationClient(OrganizationApisHolder organizationApisHolder) {
        this.organizationApisHolder = organizationApisHolder;
    }

    public String getOrganizationApiKey(Long organizationId, String keyType, String accessToken) {
        return organizationApisHolder.getOrganizationApi(accessToken).getOrganizationApiKey(organizationId, keyType);
    }
}
