package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.receipt.ReceiptPagopaIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt.ReceiptMapper;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.receipt.ReceiptParserService;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.PaSendRTV2Request;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

/**
 * Implementation of {@link ReceiptPagopaIngestionActivity} for processing OPI treasury ingestion files.
 * This class handles file retrieval, parsing, archiving, and deletion of OPI treasury files.
 */
@Slf4j
@Lazy
@Component
public class ReceiptPagopaIngestionActivityImpl extends BaseIngestionFlowFileActivity<ReceiptPagopaIngestionFlowFileResult> implements ReceiptPagopaIngestionActivity {

  private final ReceiptParserService receiptParserService;
  private final ReceiptService receiptService;
  private final ReceiptMapper receiptMapper;


  public ReceiptPagopaIngestionActivityImpl(
    IngestionFlowFileService ingestionFlowFileService,
    IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
    ReceiptParserService receiptParserService,
    IngestionFlowFileArchiverService ingestionFlowFileArchiverService,
    ReceiptService receiptService,
    ReceiptMapper receiptMapper

  ) {
    super(ingestionFlowFileService, ingestionFlowFileRetrieverService, ingestionFlowFileArchiverService);
    this.receiptParserService = receiptParserService;
    this.receiptService = receiptService;
    this.receiptMapper = receiptMapper;
  }

  @Override
  protected IngestionFlowFile.FlowFileTypeEnum getHandledIngestionFlowFileType() {
    return IngestionFlowFile.FlowFileTypeEnum.RECEIPT_PAGOPA;
  }

  @Override
  protected ReceiptPagopaIngestionFlowFileResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {
    int ingestionFlowFilesRetrievedSize = retrievedFiles.size();
    if (ingestionFlowFilesRetrievedSize != 1) {
      throw new InvalidIngestionFileException("Expected 1 file [" + ingestionFlowFileDTO.getFileName() + "], found " + ingestionFlowFilesRetrievedSize);
    }
    Path fileToProcess = retrievedFiles.getFirst();

    //parse receipt file
    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = parseData(ingestionFlowFileDTO, fileToProcess);

    //invoke service to send receipt to debt-position for its persistence and processing
    ReceiptDTO receiptDTO = receiptService.createReceipt(receiptWithAdditionalNodeDataDTO);

    //set the missing ID in the DTO
    receiptWithAdditionalNodeDataDTO.setReceiptId(receiptDTO.getReceiptId());

    return new ReceiptPagopaIngestionFlowFileResult(receiptWithAdditionalNodeDataDTO);
  }

  private ReceiptWithAdditionalNodeDataDTO parseData(IngestionFlowFile ingestionFlowFileDTO, Path fileToProcess) {
    //parse receipt file
    PaSendRTV2Request paSendRTV2Request = receiptParserService.parseReceiptPagopaFile(fileToProcess, ingestionFlowFileDTO);

    //map to DTO
    return receiptMapper.map(paSendRTV2Request);
  }

}
