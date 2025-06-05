package it.gov.pagopa.payhub.activities.service.exportflow.email;


import io.micrometer.common.util.StringUtils;
import it.gov.pagopa.payhub.activities.connector.auth.AuthzService;
import it.gov.pagopa.payhub.activities.dto.email.TemplatedEmailDTO;
import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.auth.dto.generated.UserOrganizationRoles;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class ExportFileEmailDestinationRetrieverService {

    private final AuthzService authzService;

    public ExportFileEmailDestinationRetrieverService(AuthzService authzService) {
        this.authzService = authzService;
    }

    public TemplatedEmailDTO retrieveEmailDestinations(ExportFile exportFile, Organization organization) {
        UserInfo userInfoDTO = authzService.getOperatorInfo(exportFile.getOperatorExternalId());
        String operatorEmail = userInfoDTO.getOrganizations().stream()
                .filter(r -> r.getOrganizationId().equals(exportFile.getOrganizationId()))
                .findFirst()
                .map(UserOrganizationRoles::getEmail)
                .orElse(null);

        TemplatedEmailDTO templatedEmailDTO = new TemplatedEmailDTO();
        templatedEmailDTO.setTo(new String[]{operatorEmail});
        if (StringUtils.isNotBlank(organization.getOrgEmail()) &&
                !organization.getOrgEmail().equalsIgnoreCase(operatorEmail)) {
            templatedEmailDTO.setCc(new String[]{organization.getOrgEmail()});
        }

        return templatedEmailDTO;
    }
}
