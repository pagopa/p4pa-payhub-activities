package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.payhub.activities.dto.email.FileResourceDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Lazy
@Slf4j
@Service
public class ReceiptClient {
    public static final String DEFAULT_RECEIPT_FILE_EXTENSION = "pdf";
    private final DebtPositionApisHolder debtPositionApisHolder;

    public ReceiptClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public ReceiptDTO createReceipt(String accessToken, ReceiptWithAdditionalNodeDataDTO receipt) {
        return debtPositionApisHolder.getReceiptApi(accessToken).createReceipt(receipt);
    }

    public ReceiptNoPII getByTransferId(String accessToken, Long transferId) {
        try {
            return debtPositionApisHolder.getReceiptNoPiiSearchControllerApi(accessToken)
                .crudReceiptsGetByTransferId(transferId);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("ReceiptDTO not found for TransferId: {}", transferId);
            return null;
        }
    }

   public ReceiptDTO getByReceiptId(String accessToken, Long receiptId) {
        try {
           return debtPositionApisHolder.getReceiptApi(accessToken)
                   .getReceipt(receiptId);
       } catch (HttpClientErrorException.NotFound e) {
           log.info("ReceiptDTO not found for id: {}", receiptId);
           return null;
       }
    }

    public ReceiptNoPII getByPaymentReceiptId(String accessToken, String paymentReceiptId) {
        try {
            return debtPositionApisHolder.getReceiptNoPiiSearchControllerApi(accessToken)
                    .crudReceiptsGetByPaymentReceiptId(paymentReceiptId);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Receipt not found for paymentReceiptId: {}", paymentReceiptId);
            return null;
        }
    }

    public FileResourceDTO getReceiptPdf(String accessToken, Long receiptId, Long organizationId) {
        try {
            ResponseEntity<Resource> resourceResponseEntity = debtPositionApisHolder.getReceiptApi(accessToken)
                    .getReceiptPdfWithHttpInfo(receiptId, organizationId);
            ReceiptDTO receiptDTO = debtPositionApisHolder.getReceiptApi(accessToken)
                    .getReceipt(receiptId);
            String originalFilename = resourceResponseEntity.getHeaders().getContentDisposition().getFilename();
            return FileResourceDTO.builder()
              .resource(resourceResponseEntity.getBody())
              .fileName(buildReceiptFileName(receiptDTO, originalFilename))
              .build();
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Receipt having receiptId [{}] and organizationId [{}] not found", receiptId, organizationId);
            return null;
        }
    }

    private String buildReceiptFileName(ReceiptDTO receiptDTO, String originalFilename) {
        return receiptDTO == null || receiptDTO.getPaymentDateTime() == null ?
                originalFilename :
                receiptDTO.getPaymentDateTime().toLocalDate() + "-" + receiptDTO.getNoticeNumber() + "." + extractReceiptFileExtension(originalFilename);
    }

    private String extractReceiptFileExtension(String originalFilename) {
        if(originalFilename == null)
            return DEFAULT_RECEIPT_FILE_EXTENSION;
        return originalFilename.split("\\.")[originalFilename.lastIndexOf(".")];
    }


}
