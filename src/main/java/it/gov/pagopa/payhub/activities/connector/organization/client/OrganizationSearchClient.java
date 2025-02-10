package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
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
        try{
            return organizationApisHolder.getOrganizationSearchControllerApi(accessToken)
                    .crudOrganizationsFindByIpaCode(ipaCode);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info("Organization not found: ipaCode: {}", ipaCode);
                return null;
            }
            throw e;
        }
    }

    public Organization findByOrgFiscalCode(String orgFiscalCode, String accessToken) {
        try{
            return organizationApisHolder.getOrganizationSearchControllerApi(accessToken)
                    .crudOrganizationsFindByOrgFiscalCode(orgFiscalCode);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info("Organization not found: orgFiscalCode: {}", orgFiscalCode);
                return null;
            }
            throw e;
        }
    }

    public Organization findById(Long organizationId, String accessToken) {
        return organizationApisHolder.getOrganizationEntityControllerApi(accessToken)
                .crudGetOrganization(String.valueOf(organizationId));
    }

}
