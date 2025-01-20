package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.TreasuryApisHolder;
import it.gov.pagopa.payhub.activities.connector.classification.mapper.TreasuryRequestMapper;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class TreasuryClient {

    private final TreasuryApisHolder treasuryApisHolder;

    public TreasuryClient(TreasuryApisHolder classificationApisHolder) {
        this.treasuryApisHolder = classificationApisHolder;
    }

    public Treasury findByOrganizationIdAndIuf(Long organizationId, String iuf, String accessToken) {
        return treasuryApisHolder.getTreasurySearchApi(accessToken)
                .crudTreasuryGetByOrganizationIdAndIuf(organizationId, iuf);
    }
    public Treasury getBySemanticKey(Long organizationId, String billCode, String billYear, String accessToken) {
        return treasuryApisHolder.getTreasurySearchApi(accessToken)
                .crudTreasuryFindBySemanticKey(organizationId, billCode, billYear);
    }

    public Treasury insert(Treasury treasury, String accessToken) {
        return treasuryApisHolder.getTreasuryEntityControllerApi(accessToken)
                .crudCreateTreasury(TreasuryRequestMapper.map(treasury));
    }

    public Long deleteByOrganizationIdAndBillCodeAndBillYear(Long organizationId, String billCode, String billYear, String accessToken) {
        return treasuryApisHolder.getTreasuryEntityExtendedControllerApi(accessToken)
                .deleteByOrganizationIdAndBillCodeAndBillYear(organizationId, billCode, billYear);
    }

}
