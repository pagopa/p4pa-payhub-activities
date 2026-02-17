package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.pu.debtposition.dto.generated.SpontaneousForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SpontaneousFormServiceImpl implements SpontaneousFormService {

    private final DebtPositionTypeOrgClient debtPositionTypeOrgClient;
    private final AuthnService authnService;

    public SpontaneousFormServiceImpl(DebtPositionTypeOrgClient debtPositionTypeOrgClient, AuthnService authnService) {
        this.debtPositionTypeOrgClient = debtPositionTypeOrgClient;
        this.authnService = authnService;
    }

    @Override
    public SpontaneousForm findByOrganizationIdAndCode(Long organizationId, String code) {
        log.info("Finding SpontaneousForm by organizationId: {} and code: {}", organizationId, code);
        return debtPositionTypeOrgClient.findSpontaneousFormByOrganizationIdAndCode(organizationId, code, authnService.getAccessToken());
    }

    @Override
    public SpontaneousForm createSpontaneousForm(SpontaneousForm spontaneousForm) {
        log.info("Creating SpontaneousForm with code: {}", spontaneousForm.getCode());
        return debtPositionTypeOrgClient.createSpontaneousForm(spontaneousForm, authnService.getAccessToken());
    }
}


