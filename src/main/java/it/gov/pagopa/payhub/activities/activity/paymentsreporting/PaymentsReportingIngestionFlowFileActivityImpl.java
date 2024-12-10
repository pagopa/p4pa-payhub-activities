package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.reportingflow.PaymentsReportingIngestionFlowFileActivityResult;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.FlussoRiversamentoUnmarshallerService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.PaymentsReportingIngestionFlowFileValidatorService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.PaymentsReportingMapperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
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
	private final PaymentsReportingIngestionFlowFileValidatorService paymentsReportingIngestionFlowFileValidatorService;
	private final PaymentsReportingMapperService paymentsReportingMapperService;

	public PaymentsReportingIngestionFlowFileActivityImpl(@Value("${ingestion-flow-file-type:R}")String ingestionflowFileType,
	                                                      IngestionFlowFileDao ingestionFlowFileDao,
	                                                      IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
	                                                      FlussoRiversamentoUnmarshallerService flussoRiversamentoUnmarshallerService,
	                                                      PaymentsReportingIngestionFlowFileValidatorService paymentsReportingIngestionFlowFileValidatorService,
	                                                      PaymentsReportingMapperService paymentsReportingMapperService) {
		this.ingestionflowFileType = ingestionflowFileType;
		this.ingestionFlowFileDao = ingestionFlowFileDao;
		this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
		this.flussoRiversamentoUnmarshallerService = flussoRiversamentoUnmarshallerService;
		this.paymentsReportingIngestionFlowFileValidatorService = paymentsReportingIngestionFlowFileValidatorService;
		this.paymentsReportingMapperService = paymentsReportingMapperService;
	}

	@Override
	public PaymentsReportingIngestionFlowFileActivityResult processFile(Long ingestionFlowFileId) {
		try {
			IngestionFlowFileDTO ingestionFlowFileDTO = findIngestionFlowFileRecord(ingestionFlowFileId);

			File ingestionFlowFile = retrieveFile(ingestionFlowFileDTO);

			Pair<String, List<PaymentsReportingDTO>> pair = parseData(ingestionFlowFile, ingestionFlowFileDTO);

			return new PaymentsReportingIngestionFlowFileActivityResult(List.of(pair.getLeft()), true);
		} catch (Exception e) {
			log.error("Error during PaymentsReportingIngestionFlowFileActivity ingestionFlowFileId {} due to: {}", ingestionFlowFileId, e.getMessage());
			return new PaymentsReportingIngestionFlowFileActivityResult(Collections.emptyList(), false);
		}
	}

	private Pair<String, List<PaymentsReportingDTO>> parseData(File ingestionFlowFile, IngestionFlowFileDTO ingestionFlowFileDTO) {
		CtFlussoRiversamento ctFlussoRiversamento = flussoRiversamentoUnmarshallerService.unmarshal(ingestionFlowFile);
		log.debug("file CtFlussoRiversamento with Id {} parsed successfully ", ctFlussoRiversamento.getIdentificativoFlusso());

		paymentsReportingIngestionFlowFileValidatorService.validateOrganization(ctFlussoRiversamento, ingestionFlowFileDTO);

		List<PaymentsReportingDTO> dtoList = paymentsReportingMapperService.mapToDtoList(ctFlussoRiversamento, ingestionFlowFileDTO);
		return Pair.of(ctFlussoRiversamento.getIdentificativoFlusso(), dtoList);
	}

	private File retrieveFile(IngestionFlowFileDTO ingestionFlowFileDTO) throws IOException {
		List<Path> ingestionFlowFiles = ingestionFlowFileRetrieverService
			.retrieveAndUnzipFile(Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());
		return ingestionFlowFiles.get(0).toFile();
	}

	private IngestionFlowFileDTO findIngestionFlowFileRecord(Long ingestionFlowFileId) {
		IngestionFlowFileDTO ingestionFlowFileDTO = ingestionFlowFileDao.findById(ingestionFlowFileId)
			.orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot found ingestionFlow having id: "+ ingestionFlowFileId));
		if (!ingestionFlowFileDTO.getFlowFileType().equals(ingestionflowFileType)) {
			throw new IllegalArgumentException("invalid ingestionFlow file type");
		}
		return ingestionFlowFileDTO;
	}
}
