package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingIngestionFlowFileActivityResult;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.service.FlowValidatorService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.PaymentsReportingInsertionService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.FlussoRiversamentoUnmarshallerService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.PaymentsReportingMapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Slf4j
@Lazy
@Component
public class PaymentsReportingIngestionFlowFileActivityImpl implements PaymentsReportingIngestionFlowFileActivity {
	private final String ingestionflowFileType;
	private final IngestionFlowFileDao ingestionFlowFileDao;
	private final IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
	private final FlussoRiversamentoUnmarshallerService flussoRiversamentoUnmarshallerService;
	private final FlowValidatorService flowValidatorService;
	private final PaymentsReportingMapperService paymentsReportingMapperService;
	private final PaymentsReportingInsertionService paymentsReportingInsertionService;

	public PaymentsReportingIngestionFlowFileActivityImpl(@Value("${ingestion-flow-file-type:R}")String ingestionflowFileType,
	                                                      IngestionFlowFileDao ingestionFlowFileDao,
	                                                      IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
	                                                      FlussoRiversamentoUnmarshallerService flussoRiversamentoUnmarshallerService,
	                                                      FlowValidatorService flowValidatorService,
	                                                      PaymentsReportingMapperService paymentsReportingMapperService,
	                                                      PaymentsReportingInsertionService paymentsReportingInsertionService) {
		this.ingestionflowFileType = ingestionflowFileType;
		this.ingestionFlowFileDao = ingestionFlowFileDao;
		this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
		this.paymentsReportingInsertionService = paymentsReportingInsertionService;
		this.flussoRiversamentoUnmarshallerService = flussoRiversamentoUnmarshallerService;
		this.flowValidatorService = flowValidatorService;
		this.paymentsReportingMapperService = paymentsReportingMapperService;
	}

	@Override
	public PaymentsReportingIngestionFlowFileActivityResult processFile(Long ingestionFlowFileId) {
		try {
			IngestionFlowFileDTO ingestionFlowFileDTO = ingestionFlowFileDao.findById(ingestionFlowFileId)
				.orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot found ingestionFlow having id: "+ ingestionFlowFileId));
			if (!ingestionFlowFileDTO.getFlowFileType().equals(ingestionflowFileType)) {
				throw new IllegalArgumentException("invalid ingestionFlow file type");
			}

			List<Path> ingestionFlowFiles = ingestionFlowFileRetrieverService
				.retrieveAndUnzipFile(Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());
			File ingestionFlowFile = ingestionFlowFiles.get(0).toFile();

			CtFlussoRiversamento ctFlussoRiversamento = flussoRiversamentoUnmarshallerService.unmarshal(ingestionFlowFile);
			log.debug("file CtFlussoRiversamento with Id {} parsed successfully ", ctFlussoRiversamento.getIdentificativoFlusso());

			flowValidatorService.validateOrganization(ctFlussoRiversamento, ingestionFlowFileDTO);

			PaymentsReportingDTO paymentsReportingDTO = paymentsReportingMapperService.apply(ctFlussoRiversamento, ingestionFlowFileDTO);

			ctFlussoRiversamento.getDatiSingoliPagamenti().forEach(item -> {
				PaymentsReportingDTO updatedDTO = paymentsReportingMapperService.toBuilder(paymentsReportingDTO, item);
				int i = paymentsReportingInsertionService.savePaymentsReporting(updatedDTO);
				System.out.println("saved"+i);
			});
			return new PaymentsReportingIngestionFlowFileActivityResult(List.of(paymentsReportingDTO.getFlowIdentifierCode()), true);
		} catch (Exception e) {
			log.error("Error during PaymentsReportingIngestionFlowFileActivity ingestionFlowFileId {} due to: {}", ingestionFlowFileId, e.getMessage());
			return new PaymentsReportingIngestionFlowFileActivityResult(Collections.emptyList(), false);
		}

	}
}
