package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;

import java.util.List;


public interface ClassificationService {
    Integer saveAll(List<Classification> classificationDTOList);
    Classification save(Classification classificationDTO);
    Long deleteByOrganizationIdAndIufAndLabel(Long organizationId, String iuf, ClassificationsEnum classification);
    Long deleteBySemanticKey(TransferSemanticKeyDTO transferSemanticKeyDTO);
    Long deleteByOrganizationIdAndIudAndLabel(Long organizationId, String iud, ClassificationsEnum classification);

}
