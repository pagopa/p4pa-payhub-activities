package it.gov.pagopa.payhub.activities.service.ingestionflow.receipt;


import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.PaSendRTV2Request;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Lazy
@Service
@Slf4j
public class ReceiptParserService {

  private final ReceiptUnmarshallerService receiptUnmarshallerService;

  public ReceiptParserService(ReceiptUnmarshallerService receiptUnmarshallerService) {
    this.receiptUnmarshallerService = receiptUnmarshallerService;
  }

  public PaSendRTV2Request parseReceiptPagopaFile(Path receiptPagopaFilePath, IngestionFlowFile ingestionFlowFileDTO){
    log.debug("Parsing Receipt Pagopa file[{}] ingestionFlowFileId[{}]", receiptPagopaFilePath, ingestionFlowFileDTO.getIngestionFlowFileId());
    return receiptUnmarshallerService.unmarshalReceiptPagopa(receiptPagopaFilePath.toFile());
  }
}
