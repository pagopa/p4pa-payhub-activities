package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.dto.generated.CollectionModelOrgSilService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class OrgSilServiceSearchControllerClient {

    private final OrganizationApisHolder organizationApisHolder;

    public OrgSilServiceSearchControllerClient(OrganizationApisHolder organizationApisHolder) {
        this.organizationApisHolder = organizationApisHolder;
    }

    public CollectionModelOrgSilService findAllByOrganizationIdAndServiceType(Long organizationId, OrgSilServiceType serviceType, String accessToken) {
            return organizationApisHolder.getOrgSilServiceSearchControllerApi(accessToken)
                    .crudOrgSilServicesFindAllByOrganizationIdAndServiceType(organizationId, serviceType);
    }
}
