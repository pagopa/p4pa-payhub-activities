package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.dto.generated.CollectionModelOrgSilService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Lazy
@Service
@Slf4j
public class OrgSilServiceSearchClient {

    private final OrganizationApisHolder organizationApisHolder;

    public OrgSilServiceSearchClient(OrganizationApisHolder organizationApisHolder) {
        this.organizationApisHolder = organizationApisHolder;
    }

    public CollectionModelOrgSilService findAllByOrganizationIdAndServiceType(Long orgId, OrgSilServiceType serviceType, String accessToken) {
        try {
            return organizationApisHolder.getOrgSilServiceSearchControllerApi(accessToken)
                    .crudOrgSilServicesFindAllByOrganizationIdAndServiceType(orgId, serviceType);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("OrgSilService not found: orgId: {}, serviceType: {}", orgId, serviceType);
            return null;
        }
    }
}
