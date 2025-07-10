package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.OrgSilServiceSearchClient;
import it.gov.pagopa.payhub.activities.connector.organization.client.OrganizationSilServiceClient;
import it.gov.pagopa.pu.organization.dto.generated.CollectionModelOrgSilService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Lazy
@Service
@Slf4j
public class OrgSilServiceServiceImpl implements OrgSilServiceService {

    private final AuthnService authnService;
    private final OrgSilServiceSearchClient orgSilServiceSearchClient;
    private final OrganizationSilServiceClient organizationSilServiceClient;


    public OrgSilServiceServiceImpl(AuthnService authnService, OrgSilServiceSearchClient orgSilServiceSearchClient, OrganizationSilServiceClient organizationSilServiceClient) {
        this.authnService = authnService;
        this.orgSilServiceSearchClient = orgSilServiceSearchClient;
        this.organizationSilServiceClient = organizationSilServiceClient;
    }

    @Override
    public List<OrgSilService> getOrgSilServiceByOrgIdAndServiceType(Long orgId, OrgSilServiceType serviceType) {
        CollectionModelOrgSilService orgSilServices = orgSilServiceSearchClient.findAllByOrganizationIdAndServiceType(orgId, serviceType, authnService.getAccessToken());
        return Objects.requireNonNull(orgSilServices.getEmbedded()).getOrgSilServices();
    }

    @Override
    public OrgSilServiceDTO createOrUpdateOrgSilService(OrgSilServiceDTO orgSilServiceDTO) {
        return organizationSilServiceClient.createOrUpdateOrgSilService(orgSilServiceDTO, authnService.getAccessToken());
    }
}
