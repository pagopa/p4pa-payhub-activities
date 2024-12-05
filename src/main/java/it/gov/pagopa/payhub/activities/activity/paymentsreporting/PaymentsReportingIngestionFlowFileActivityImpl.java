package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingIngestionFlowFileActivityResult;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowNotFoundException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.FlussoRiversamentoHandler;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.PaymentsReportingInsertionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Lazy
@Component
public class PaymentsReportingIngestionFlowFileActivityImpl implements PaymentsReportingIngestionFlowFileActivity {
	private final IngestionFlowFileDao ingestionFlowFileDao;
	private final IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
	private final FlussoRiversamentoHandler flussoRiversamentoHandler;
	private final PaymentsReportingInsertionService paymentsReportingInsertionService;

	public PaymentsReportingIngestionFlowFileActivityImpl(IngestionFlowFileDao ingestionFlowFileDao,
	                                                      IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
	                                                      FlussoRiversamentoHandler flussoRiversamentoHandler,
	                                                      PaymentsReportingInsertionService paymentsReportingInsertionService) {
		this.ingestionFlowFileDao = ingestionFlowFileDao;
		this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
		this.flussoRiversamentoHandler = flussoRiversamentoHandler;
		this.paymentsReportingInsertionService = paymentsReportingInsertionService;
	}

	@Override
	public PaymentsReportingIngestionFlowFileActivityResult processFile(Long ingestionFlowFileId) {
		List<String> iufList = new ArrayList<>();
		boolean success = true;

		try {
			IngestionFlowFileDTO ingestionFlowFileDTO = ingestionFlowFileDao.findById(ingestionFlowFileId)
				.orElseThrow(() -> new IngestionFlowNotFoundException("Cannot found ingestionFlow having id: "+ ingestionFlowFileId));

			List<Path> ingestionFlowFiles = ingestionFlowFileRetrieverService.retrieveAndUnzipFile(Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());
			File ingestionFlowFile = ingestionFlowFiles.get(0).toFile();
			CtFlussoRiversamento ctFlussoRiversamento = flussoRiversamentoHandler.handle(ingestionFlowFile);
			log.debug("file CtFlussoRiversamento with Id {} parsed successfully ", ctFlussoRiversamento.getIdentificativoFlusso());
			PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingDTO.builder()
				.ingestionFlowFile(ingestionFlowFileDTO)
				.flowIdentifierCode(ctFlussoRiversamento.getIdentificativoFlusso())
				.build();
			paymentsReportingDTO = paymentsReportingInsertionService.savePaymentsReporting(paymentsReportingDTO);
			iufList.add(paymentsReportingDTO.getFlowIdentifierCode());
		} catch (Exception e) {
			log.error("Error during PaymentsReportingIngestionFlowFileActivity ingestionFlowFileId {} due to: {}", ingestionFlowFileId, e.getMessage());
			success = false;
		}
		return new PaymentsReportingIngestionFlowFileActivityResult(iufList, success);
	}
}
