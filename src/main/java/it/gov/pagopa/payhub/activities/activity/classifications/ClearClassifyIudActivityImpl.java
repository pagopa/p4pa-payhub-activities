package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Lazy
@Component
public class ClearClassifyIudActivityImpl implements ClearClassifyIudActivity {
    private final ClassificationService classificationService;

    public ClearClassifyIudActivityImpl(ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    public Long deleteClassificationByIud(Long organizationId, String iud) {
        log.info("Deleting classification IUD_NO_RT for organization id: {} and iud: {}", organizationId, iud);
        return classificationService.deleteByOrganizationIdAndIudAndLabel(organizationId, iud, ClassificationsEnum.IUD_NO_RT);
    }
}
