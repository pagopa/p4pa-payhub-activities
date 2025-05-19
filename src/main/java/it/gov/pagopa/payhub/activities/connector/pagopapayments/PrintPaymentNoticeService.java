package it.gov.pagopa.payhub.activities.connector.pagopapayments;

import it.gov.pagopa.pu.pagopapayments.dto.generated.GeneratedNoticeMassiveFolderDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.NoticeRequestMassiveDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.SignedUrlResultDTO;

/**
 * This interface provides a method for print payment notice on PagoPa service
 */
public interface PrintPaymentNoticeService {

	/**
	 * Retrieve the folderId starting from a list of iuv
	 *
	 * @param noticeRequestMassiveDTO request containing organizationId, list of debtPosition, iuv and an idempotence key
	 * @return folderId
	 */
	GeneratedNoticeMassiveFolderDTO generateMassive(NoticeRequestMassiveDTO noticeRequestMassiveDTO);

	/**
	 * Return SignedUrlResultDTO with signedUrl if folder status is ready and two lists containing all notices processed or in error
	 *
	 * @param organizationId the ID of the organization
	 * @param pdfGeneratedId retrieved from generateMassive
	 * @return SignedUrlResultDTO
	 */
	SignedUrlResultDTO getSignedUrl(Long organizationId, String pdfGeneratedId);
}
