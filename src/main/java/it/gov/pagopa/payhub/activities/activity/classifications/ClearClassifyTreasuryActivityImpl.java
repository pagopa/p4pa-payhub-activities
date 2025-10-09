package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Lazy
@Component
public class ClearClassifyTreasuryActivityImpl implements ClearClassifyTreasuryActivity {
    private final ClassificationService classificationService;

    public ClearClassifyTreasuryActivityImpl(ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    @Override
    public Integer deleteClassificationByTreasuryId(Long organizationId, String treasuryId) {
        log.info("Deleting classification for organization id: {} and treasury: {}", organizationId,treasuryId);
        return classificationService.deleteByOrganizationIdAndTreasuryId(organizationId, treasuryId);
    }
}
