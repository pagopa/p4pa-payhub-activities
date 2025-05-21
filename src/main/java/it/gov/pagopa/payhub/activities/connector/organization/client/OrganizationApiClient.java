package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class OrganizationApiClient {

    private final OrganizationApisHolder organizationApisHolder;

    public OrganizationApiClient(OrganizationApisHolder organizationApisHolder) {
        this.organizationApisHolder = organizationApisHolder;
    }

    public void encryptAndSaveApiKey(Long organizationId, OrganizationApiKeys organizationApiKeys, String accessToken) {
            organizationApisHolder.getOrganizationApi(accessToken)
                    .encryptAndSaveApiKey(organizationId, organizationApiKeys);
    }

}
