package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.OrganizationSearchClient;
import it.gov.pagopa.pu.organization.dto.generated.CollectionModelOrganization;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Lazy
@Service
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

    private final AuthnService authnService;
    private final OrganizationSearchClient organizationSearchClient;

    public OrganizationServiceImpl(AuthnService authnService, OrganizationSearchClient organizationSearchClient) {
        this.authnService = authnService;
        this.organizationSearchClient = organizationSearchClient;
    }

    @Override
    public Optional<Organization> getOrganizationByFiscalCode(String orgFiscalCode) {
        return Optional.ofNullable(
                organizationSearchClient.findByOrgFiscalCode(orgFiscalCode, authnService.getAccessToken())
        );
    }

    @Override
    public Optional<Organization> getOrganizationByIpaCode(String ipaCode) {
        return Optional.ofNullable(
                organizationSearchClient.findByIpaCode(ipaCode, authnService.getAccessToken())
        );
    }

    @Override
    public Optional<Organization> getOrganizationById(Long organizationId) {
        return Optional.ofNullable(
                organizationSearchClient.findById(organizationId, authnService.getAccessToken())
        );
    }

    @Override
    public List<Organization> getActiveOrganizationsByBrokerId(Long brokerId) {
        CollectionModelOrganization organizations = organizationSearchClient.findActiveOrganizationsByBrokerId(brokerId, authnService.getAccessToken());
        return Objects.requireNonNull(organizations.getEmbedded()).getOrganizations();
    }
}
