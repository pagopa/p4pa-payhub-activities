package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;

import java.util.List;


public interface ClassificationService {
    Integer saveAll(List<Classification> classificationDTOList);
    Integer deleteByOrganizationIdAndIufAndLabel(Long organizationId, String iuf, ClassificationsEnum classification);
    Integer deleteBySemanticKey(TransferSemanticKeyDTO transferSemanticKeyDTO);
    Integer deleteByOrganizationIdAndIudAndLabel(Long organizationId, String iud, ClassificationsEnum classification);
}
