package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptPagopaIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt.ReceiptMapper;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.receipt.ReceiptParserService;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.PaSendRTV2Request;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

/**
 * Implementation of {@link ReceiptPagopaIngestionActivity} for processing receipt pagopa ingestion files.
 * This class handles file retrieval, parsing, archiving, and deletion of receipt pagopa files.
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
    FileArchiverService fileArchiverService,
    ReceiptService receiptService,
    ReceiptMapper receiptMapper

  ) {
    super(ingestionFlowFileService, ingestionFlowFileRetrieverService, fileArchiverService);
    this.receiptParserService = receiptParserService;
    this.receiptService = receiptService;
    this.receiptMapper = receiptMapper;
  }

  @Override
  protected IngestionFlowFile.IngestionFlowFileTypeEnum getHandledIngestionFlowFileType() {
    return IngestionFlowFile.IngestionFlowFileTypeEnum.RECEIPT_PAGOPA;
  }

  @Override
  protected ReceiptPagopaIngestionFlowFileResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {
    int ingestionFlowFilesRetrievedSize = retrievedFiles.size();
    if (ingestionFlowFilesRetrievedSize != 1) {
      throw new InvalidIngestionFileException("Expected 1 file [" + ingestionFlowFileDTO.getFileName() + "], found " + ingestionFlowFilesRetrievedSize);
    }
    Path fileToProcess = retrievedFiles.getFirst();

    //parse receipt file
    Pair<String, ReceiptWithAdditionalNodeDataDTO> version2receiptWithAdditionalNodeDataDTO = parseData(ingestionFlowFileDTO, fileToProcess);
    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = version2receiptWithAdditionalNodeDataDTO.getValue();

    //invoke service to send receipt to debt-position for its persistence and processing
    ReceiptDTO receiptDTO = receiptService.createReceipt(receiptWithAdditionalNodeDataDTO);
    //TODO installment will be retrieved from the response of the createReceipt service, with the implementation of a future task. To use on notifySIL? otherwise it could be removed
    InstallmentDTO installmentDTO = null;

    //set the missing ID in the DTO
    receiptWithAdditionalNodeDataDTO.setReceiptId(receiptDTO.getReceiptId());


    return ReceiptPagopaIngestionFlowFileResult.builder()
            .organizationId(ingestionFlowFileDTO.getOrganizationId())
            .fileVersion(version2receiptWithAdditionalNodeDataDTO.getKey())
            .totalRows(1L)
            .processedRows(1L)
            .receiptDTO(receiptWithAdditionalNodeDataDTO)
            .installmentDTO(installmentDTO)
            .build();
  }

  private Pair<String, ReceiptWithAdditionalNodeDataDTO> parseData(IngestionFlowFile ingestionFlowFileDTO, Path fileToProcess) {
    //parse receipt file
    PaSendRTV2Request paSendRTV2Request = receiptParserService.parseReceiptPagopaFile(fileToProcess, ingestionFlowFileDTO);

    //map to DTO
    ReceiptWithAdditionalNodeDataDTO dto = receiptMapper.map(ingestionFlowFileDTO, paSendRTV2Request);
    return Pair.of("1.0.0", dto);
  }

}
