package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.classifications.ClassificationDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;

import java.util.List;

/**
 * Data Access Object interface  for saving payments classification
 */
public interface ClassificationDao {
	/**
	 * save classification
	 *
     * @param classificationDTO dto classification  to save
     */
	void save(ClassificationDTO classificationDTO);

	/**
	 * saveAll classification
	 *
	 * @param classificationDTOList dto List classification to save
	 * @return a list of the saved `ClassificationDTO` objects, potentially with updated
	 *         fields (e.g., generated IDs or timestamps).
	 */
	List<ClassificationDTO> saveAll(List<ClassificationDTO> classificationDTOList);

    /**
     * delete classification
     *
     * @param organizationId organization id
     * @param iuf fow identifier
     * @param classification classification to delete
     * @return true for success deletion
     */
	boolean deleteClassificationByIuf(Long organizationId, String iuf, ClassificationsEnum classification);

	/**
	 * delete classification
	 *
	 * @param transferSemanticKeyDTO the DTO containing semantic keys such as organization ID, IUV, IUR, and transfer index.
	 * @return true for success deletion
	 */
	boolean deleteTransferClassification(TransferSemanticKeyDTO transferSemanticKeyDTO);
}
