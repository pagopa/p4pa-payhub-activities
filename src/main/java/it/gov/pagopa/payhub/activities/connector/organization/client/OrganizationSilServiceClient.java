package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class OrganizationSilServiceClient {

    private final OrganizationApisHolder organizationApisHolder;

    public OrganizationSilServiceClient(OrganizationApisHolder organizationApisHolder) {
        this.organizationApisHolder = organizationApisHolder;
    }

    public OrgSilServiceDTO createOrUpdateOrgSilService(OrgSilServiceDTO orgSilServiceDTO, String accessToken) {
            return organizationApisHolder.getOrganizationSilServiceApi(accessToken)
                    .createOrUpdateOrgSilService(orgSilServiceDTO);
    }
}
