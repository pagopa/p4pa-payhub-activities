package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Lazy
@Component
public class ClearClassifyIufActivityImpl implements ClearClassifyIufActivity {
    private final ClassificationService classificationService;

    public ClearClassifyIufActivityImpl(ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    @Override
    public Integer deleteClassificationByIuf(Long organizationId, String iuf) {
        log.info("Deleting classification TES_NO_IUF_OR_IUV for organization id: {} and iuf: {}", organizationId,iuf);
        Integer tesNoIufOrIudDeleted =classificationService.deleteByOrganizationIdAndIufAndLabel(organizationId, iuf, ClassificationsEnum.TES_NO_IUF_OR_IUV);
        log.info("Deleting classification TES_NO_MATCH for organization id: {} and iuf: {}", organizationId,iuf);
        Integer tesNoMatchDeleted = classificationService.deleteByOrganizationIdAndIufAndLabel(organizationId, iuf, ClassificationsEnum.TES_NO_MATCH);
        return tesNoIufOrIudDeleted + tesNoMatchDeleted;
    }
}
