package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class OrganizationEntityClient {

    private final OrganizationApisHolder organizationApisHolder;

    public OrganizationEntityClient(OrganizationApisHolder organizationApisHolder) {
        this.organizationApisHolder = organizationApisHolder;
    }

    public Organization createOrganization(OrganizationRequestBody organizationRequestBody, String accessToken) {
            return organizationApisHolder.getOrganizationEntityControllerApi(accessToken)
                    .crudCreateOrganization(organizationRequestBody);
    }
}
