package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.connector.auth.AuthzService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.auth.dto.generated.UserOrganizationRoles;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Lazy
@Service
public class IngestionFlowFileEmailDestinationRetrieverService {

    private final AuthzService authzService;
    private final OrganizationService organizationService;

    public IngestionFlowFileEmailDestinationRetrieverService(AuthzService authzService, OrganizationService organizationService) {
        this.authzService = authzService;
        this.organizationService = organizationService;
    }

    public Pair<String[], String[]> retrieveEmailDestinations(IngestionFlowFile ingestionFlowFileDTO) {
        UserInfo userInfoDTO = authzService.getOperatorInfo(ingestionFlowFileDTO.getOperatorExternalId());
        Optional<Organization> organizationDTO = organizationService.getOrganizationById(ingestionFlowFileDTO.getOrganizationId());
        String operatorEmail = userInfoDTO.getOrganizations().stream()
                .filter(r -> r.getOrganizationId().equals(ingestionFlowFileDTO.getOrganizationId()))
                .findFirst()
                .map(UserOrganizationRoles::getEmail)
                .orElse(null);

        String[] to = new String[]{operatorEmail};
        String[] cc = null;
        if (organizationDTO.isPresent() && StringUtils.isNotBlank(organizationDTO.get().getOrgEmail()) &&
                !organizationDTO.get().getOrgEmail().equalsIgnoreCase(operatorEmail)) {
            cc = new String[]{organizationDTO.get().getOrgEmail()};
        }
        return Pair.of(to, cc);
    }
}
