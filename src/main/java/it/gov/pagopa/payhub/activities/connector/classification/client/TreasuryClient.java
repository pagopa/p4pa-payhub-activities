package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
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

    private final ClassificationApisHolder classificationApisHolder;
    private final TreasuryRequestMapper mapper;

    public TreasuryClient(ClassificationApisHolder classificationApisHolder, TreasuryRequestMapper mapper) {
        this.classificationApisHolder = classificationApisHolder;
        this.mapper = mapper;
    }

    public Treasury findByOrganizationIdAndIuf(Long organizationId, String iuf, String accessToken) {
        try {
            return classificationApisHolder.getTreasurySearchApi(accessToken)
                    .crudTreasuryGetByOrganizationIdAndIuf(organizationId, iuf);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Treasury not found: organizationId: {}, iuf: {}", organizationId, iuf);
            return null;
        }
    }

    public Treasury getBySemanticKey(Long organizationId, String billCode, String billYear, String orgBtCode, String orgIstatCode, String accessToken) {
        try {
            return classificationApisHolder.getTreasurySearchApi(accessToken)
                    .crudTreasuryFindBySemanticKey(organizationId, billCode, billYear, orgBtCode, orgIstatCode);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Treasury not found: organizationId: {}, billCode: {}, billYear: {}, orgBtCode: {}, orgIstatCode: {}", organizationId, billCode, billYear, orgBtCode, orgIstatCode);
            return null;
        }
    }

    public Treasury insert(Treasury treasury, String accessToken) {
        return classificationApisHolder.getTreasuryEntityControllerApi(accessToken)
                .crudCreateTreasury(mapper.map(treasury));
    }

    public Long deleteByOrganizationIdAndBillCodeAndBillYearAndOrgBtCodeAndOrgIstatCode(Long organizationId, String billCode, String billYear, String orgBtCode, String orgIstatCode, String accessToken) {
        return classificationApisHolder.getTreasuryEntityExtendedControllerApi(accessToken)
                .deleteByOrganizationIdAndBillCodeAndBillYearAndOrgBtCodeAndOrgIstatCode(organizationId, billCode, billYear, orgBtCode, orgIstatCode);
    }

    public Treasury getById(String treasuryId, String accessToken) {
        return classificationApisHolder.getTreasuryEntityControllerApi(accessToken)
                .crudGetTreasury(treasuryId);
    }
}
