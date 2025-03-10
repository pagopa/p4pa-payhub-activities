package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.OrganizationClient;
import it.gov.pagopa.payhub.activities.connector.organization.client.OrganizationSearchClient;
import it.gov.pagopa.pu.organization.dto.generated.CollectionModelOrganization;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Lazy
@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final AuthnService authnService;
    private final OrganizationSearchClient organizationSearchClient;
    private final OrganizationClient organizationClient;

    public OrganizationServiceImpl(AuthnService authnService, OrganizationSearchClient organizationSearchClient, OrganizationClient organizationClient) {
        this.authnService = authnService;
        this.organizationSearchClient = organizationSearchClient;
        this.organizationClient = organizationClient;
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
    public List<Organization> getOrganizationsByBrokerId(Long brokerId) {
        CollectionModelOrganization organizations = organizationSearchClient.findOrganizationsByBrokerId(brokerId, authnService.getAccessToken());
        return Objects.requireNonNull(organizations.getEmbedded()).getOrganizations();
    }

    @Override
    public String getOrganizationApiKey(Long organizationId, String keyType) {
        String accessToken = authnService.getAccessToken();
        return organizationClient.getOrganizationApiKey(organizationId, keyType, accessToken);
    }
}
