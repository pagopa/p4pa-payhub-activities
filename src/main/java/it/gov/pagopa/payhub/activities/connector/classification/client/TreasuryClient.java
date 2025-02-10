package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.TreasuryApisHolder;
import it.gov.pagopa.payhub.activities.connector.classification.mapper.TreasuryRequestMapper;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Lazy
@Service
@Slf4j
public class TreasuryClient {

    private final TreasuryApisHolder treasuryApisHolder;
    private final TreasuryRequestMapper mapper;

    public TreasuryClient(TreasuryApisHolder treasuryApisHolder, TreasuryRequestMapper mapper) {
        this.treasuryApisHolder = treasuryApisHolder;
        this.mapper = mapper;
    }

    public Treasury findByOrganizationIdAndIuf(Long organizationId, String iuf, String accessToken) {
        try {
            return treasuryApisHolder.getTreasurySearchApi(accessToken)
                    .crudTreasuryGetByOrganizationIdAndIuf(organizationId, iuf);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Treasury not found: organizationId: {}, iuf: {}", organizationId, iuf);
            return null;
        }
    }

    public Treasury getBySemanticKey(Long organizationId, String billCode, String billYear, String accessToken) {
        try {
            return treasuryApisHolder.getTreasurySearchApi(accessToken)
                    .crudTreasuryFindBySemanticKey(organizationId, billCode, billYear);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Treasury not found: organizationId: {}, billCode: {}, billYear: {}", organizationId, billCode, billYear);
            return null;
        }
    }

    public Treasury insert(Treasury treasury, String accessToken) {
        return treasuryApisHolder.getTreasuryEntityControllerApi(accessToken)
                .crudCreateTreasury(mapper.map(treasury));
    }

    public Long deleteByOrganizationIdAndBillCodeAndBillYear(Long organizationId, String billCode, String billYear, String accessToken) {
        return treasuryApisHolder.getTreasuryEntityExtendedControllerApi(accessToken)
                .deleteByOrganizationIdAndBillCodeAndBillYear(organizationId, billCode, billYear);
    }

}
