package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.TreasuryClient;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Lazy
@Service
@Slf4j
public class TreasuryServiceImpl implements TreasuryService {

    private final TreasuryClient treasuryClient;
    private final AuthnService authnService;

    public TreasuryServiceImpl(TreasuryClient treasuryClient, AuthnService authnService) {
        this.treasuryClient = treasuryClient;
        this.authnService = authnService;
    }



    @Override
    public Optional<Treasury> getByOrganizationIdAndIuf(Long organizationId, String iuf) {
        return Optional.ofNullable(
                treasuryClient.findByOrganizationIdAndIuf(organizationId,iuf, authnService.getAccessToken())
        );
    }

    @Override
    public Optional<Treasury> getBySemanticKey(Long organizationId, String billCode, String billYear) {
        return Optional.ofNullable(
                treasuryClient.getBySemanticKey(organizationId, billCode, billYear, authnService.getAccessToken())
        );
    }

    @Override
    public Optional<Treasury> insert(Treasury treasury) {
        return Optional.ofNullable(
                treasuryClient.insert(treasury, authnService.getAccessToken())
        );
    }

    @Override
    public Long deleteByOrganizationIdAndBillCodeAndBillYear(Long organizationId, String billCode, String billYear) {
        return treasuryClient.deleteByOrganizationIdAndBillCodeAndBillYear(organizationId, billCode, billYear, authnService.getAccessToken());
    }

}
