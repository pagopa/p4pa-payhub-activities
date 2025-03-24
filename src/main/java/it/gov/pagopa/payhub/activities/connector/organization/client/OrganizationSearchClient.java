package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.dto.generated.CollectionModelOrganization;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Lazy
@Service
@Slf4j
public class OrganizationSearchClient {

    private final OrganizationApisHolder organizationApisHolder;

    public OrganizationSearchClient(OrganizationApisHolder organizationApisHolder) {
        this.organizationApisHolder = organizationApisHolder;
    }

    public Organization findByIpaCode(String ipaCode, String accessToken) {
        try {
            return organizationApisHolder.getOrganizationSearchControllerApi(accessToken)
                    .crudOrganizationsFindByIpaCode(ipaCode);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Organization not found: ipaCode: {}", ipaCode);
            return null;
        }
    }

    public Organization findByOrgFiscalCode(String orgFiscalCode, String accessToken) {
        try {
            return organizationApisHolder.getOrganizationSearchControllerApi(accessToken)
                    .crudOrganizationsFindByOrgFiscalCode(orgFiscalCode);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Organization not found: orgFiscalCode: {}", orgFiscalCode);
            return null;
        }
    }

    public Organization findById(Long organizationId, String accessToken) {
        try{
            return organizationApisHolder.getOrganizationEntityControllerApi(accessToken)
                    .crudGetOrganization(String.valueOf(organizationId));
        } catch (HttpClientErrorException.NotFound e){
            log.info("Cannot find organization having id {}", organizationId);
            return null;
        }
    }

    public CollectionModelOrganization findActiveOrganizationsByBrokerId(Long brokerId, String accessToken) {
        return organizationApisHolder.getOrganizationSearchControllerApi(accessToken)
                .crudOrganizationsFindByBrokerIdAndStatus(brokerId, OrganizationStatus.ACTIVE);
    }
}
