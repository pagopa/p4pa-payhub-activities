package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.TreasuryClient;
import it.gov.pagopa.payhub.activities.connector.classification.mapper.TreasuryMapper;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class TreasuryServiceImpl implements TreasuryService {
    private final TreasuryClient treasuryClient;
    private final AuthnService authnService;
    private final TreasuryMapper mapper;

    public TreasuryServiceImpl(TreasuryClient treasuryClient, AuthnService authnService, TreasuryMapper mapper) {
        this.treasuryClient = treasuryClient;
        this.authnService = authnService;
        this.mapper = mapper;
    }

    @Override
    public TreasuryIuf getByOrganizationIdAndIuf(Long organizationId, String iuf) {
        return mapper.map2Iuf(treasuryClient.findByOrganizationIdAndIuf(organizationId,iuf, authnService.getAccessToken()));
    }

    @Override
    public Treasury insert(Treasury treasury) {
        return treasuryClient.insert(treasury, authnService.getAccessToken());
    }

    @Override
    public Long deleteByOrganizationIdAndBillCodeAndBillYear(Long organizationId, String billCode, String billYear) {
        return treasuryClient.deleteByOrganizationIdAndBillCodeAndBillYear(organizationId, billCode, billYear, authnService.getAccessToken());
    }

    @Override
    public Treasury getById(String treasuryId) {
        return treasuryClient.getById(treasuryId, authnService.getAccessToken());
    }
}
