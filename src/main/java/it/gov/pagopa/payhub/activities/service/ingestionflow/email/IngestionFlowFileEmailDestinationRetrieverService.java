package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.connector.auth.AuthzService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.apache.commons.lang3.StringUtils;
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

    public void configure(IngestionFlowFileDTO ingestionFlowFileDTO, EmailDTO emailDTO) {
        UserInfo userInfoDTO = authzService.getOperatorInfo(ingestionFlowFileDTO.getOperatorExternalUserId());
        Optional<Organization> organizationDTO = organizationService.getOrganizationByIpaCode(ingestionFlowFileDTO.getOrg().getIpaCode());

        emailDTO.setTo(new String[]{userInfoDTO.getEmail()});
        if (organizationDTO.isPresent() && StringUtils.isNotBlank(organizationDTO.get().getAdminEmail()) &&
                !organizationDTO.get().getAdminEmail().equalsIgnoreCase(userInfoDTO.getEmail())) {
            emailDTO.setCc(new String[]{organizationDTO.get().getAdminEmail()});
        }
    }
}
