package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.OrgSilServiceSearchControllerClient;
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
    private final OrganizationSilServiceClient organizationSilServiceClient;
    private final OrgSilServiceSearchControllerClient orgSilServiceSearchControllerClient;


    public OrgSilServiceServiceImpl(AuthnService authnService, OrganizationSilServiceClient organizationSilServiceClient, OrgSilServiceSearchControllerClient orgSilServiceSearchControllerClient) {
        this.authnService = authnService;
        this.organizationSilServiceClient = organizationSilServiceClient;
        this.orgSilServiceSearchControllerClient = orgSilServiceSearchControllerClient;
    }

    @Override
    public OrgSilServiceDTO createOrUpdateOrgSilService(OrgSilServiceDTO orgSilServiceDTO) {
        return organizationSilServiceClient.createOrUpdateOrgSilService(orgSilServiceDTO, authnService.getAccessToken());
    }

    @Override
    public List<OrgSilService> getAllByOrganizationIdAndServiceType(Long organizationId, OrgSilServiceType serviceType) {
        CollectionModelOrgSilService collectionModelOrgSilService = orgSilServiceSearchControllerClient.findAllByOrganizationIdAndServiceType(organizationId,serviceType, authnService.getAccessToken());
        return Objects.requireNonNull(collectionModelOrgSilService.getEmbedded()).getOrgSilServices();
    }
}
