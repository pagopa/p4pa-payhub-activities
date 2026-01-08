package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;

import java.util.List;


public interface ClassificationService {
    Integer saveAll(List<Classification> classificationDTOList);
    Integer deleteByOrganizationIdAndIufAndLabel(Long organizationId, String iuf, ClassificationsEnum classification);
    Integer deleteBySemanticKey(TransferSemanticKeyDTO transferSemanticKeyDTO);
    Integer deleteBySemanticKeyExcludingLabel(TransferSemanticKeyDTO transferSemanticKeyDTO, ClassificationsEnum label);
    Integer deleteByOrganizationIdAndIudAndLabel(Long organizationId, String iud, ClassificationsEnum classification);
    Integer deleteByOrganizationIdAndTreasuryId(Long organizationId, String treasuryId);
    Integer deleteDuplicates(Long organizationId, String iuv, int transferIndex, Long receiptPaymentAmount, String receiptOrgFiscalCode);
    /**
     * Find Classifications by specified organizationId, iuv and iud.
     *
     * @param organizationId the unique identifier of the organization.
     * @param iuv the unique identifier of the transfer.
     * @param iud the unique identifier of the debt-position.
     * @return list of Classification found.
     */
    List<Classification> findAllByOrganizationIdAndIuvAndIud(Long organizationId, String iuv, String iud);
}
