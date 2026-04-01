package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;

public class ReceiptUtils {

	public static final String DEFAULT_RECEIPT_FILE_EXTENSION = "pdf";

	private ReceiptUtils() {}

	public static String buildReceiptFileName(ReceiptWithAdditionalNodeDataDTO receiptDTO, String originalFilename) {
		return receiptDTO == null || receiptDTO.getPaymentDateTime() == null ?
				originalFilename :
				receiptDTO.getPaymentDateTime().toLocalDate() + "-" + receiptDTO.getNoticeNumber() + "." + extractReceiptFileExtension(originalFilename);
	}

	private static String extractReceiptFileExtension(String originalFilename) {
		if(originalFilename == null)
			return DEFAULT_RECEIPT_FILE_EXTENSION;
		return originalFilename.substring(originalFilename.lastIndexOf(".")+1);
	}

}