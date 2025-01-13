package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingIngestionFlowFileActivityResult;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.FlussoRiversamentoUnmarshallerService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.PaymentsReportingIngestionFlowFileValidatorService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.PaymentsReportingMapperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Slf4j
@Lazy
@Component
public class PaymentsReportingIngestionFlowFileActivityImpl extends BaseIngestionFlowFileActivity<PaymentsReportingIngestionFlowFileActivityResult> implements PaymentsReportingIngestionFlowFileActivity {

	private final FlussoRiversamentoUnmarshallerService flussoRiversamentoUnmarshallerService;
	private final PaymentsReportingIngestionFlowFileValidatorService paymentsReportingIngestionFlowFileValidatorService;
	private final PaymentsReportingMapperService paymentsReportingMapperService;
	private final PaymentsReportingDao paymentsReportingDao;

	public PaymentsReportingIngestionFlowFileActivityImpl(
	                                                      IngestionFlowFileDao ingestionFlowFileDao,
	                                                      IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
	                                                      FlussoRiversamentoUnmarshallerService flussoRiversamentoUnmarshallerService,
	                                                      PaymentsReportingIngestionFlowFileValidatorService paymentsReportingIngestionFlowFileValidatorService,
	                                                      PaymentsReportingMapperService paymentsReportingMapperService,
	                                                      PaymentsReportingDao paymentsReportingDao,
	                                                      IngestionFlowFileArchiverService ingestionFlowFileArchiverService) {
		super(ingestionFlowFileDao, ingestionFlowFileRetrieverService, ingestionFlowFileArchiverService);
		this.flussoRiversamentoUnmarshallerService = flussoRiversamentoUnmarshallerService;
		this.paymentsReportingIngestionFlowFileValidatorService = paymentsReportingIngestionFlowFileValidatorService;
		this.paymentsReportingMapperService = paymentsReportingMapperService;
		this.paymentsReportingDao = paymentsReportingDao;
	}

	@Override
	protected IngestionFlowFileType getHandledIngestionFlowFileType() {
		return IngestionFlowFileType.PAYMENTS_REPORTING;
	}

	@Override
	protected PaymentsReportingIngestionFlowFileActivityResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFileDTO ingestionFlowFileDTO) {
		Pair<String, List<PaymentsReportingDTO>> pair = parseData(retrievedFiles.getFirst().toFile(), ingestionFlowFileDTO);
		paymentsReportingDao.saveAll(pair.getRight());
		return new PaymentsReportingIngestionFlowFileActivityResult(List.of(pair.getLeft()), true, null);
	}

	@Override
	protected PaymentsReportingIngestionFlowFileActivityResult onErrorResult(Exception e) {
		return new PaymentsReportingIngestionFlowFileActivityResult(Collections.emptyList(), false, e.getMessage());
	}

	/**
	 * Parses the provided file into a {@link CtFlussoRiversamento} object and maps its content into a list
	 * of {@link PaymentsReportingDTO}. Validates the file's organization data.
	 *
	 * @param ingestionFlowFile the file to be parsed
	 * @param ingestionFlowFileDTO the ingestion flow file DTO containing additional context
	 * @return a {@link Pair} containing the flow file identifier and the list of {@link PaymentsReportingDTO}
	 * @throws IllegalArgumentException if the file content does not conform to the expected structure
	 */
	private Pair<String, List<PaymentsReportingDTO>> parseData(File ingestionFlowFile, IngestionFlowFileDTO ingestionFlowFileDTO) {
		CtFlussoRiversamento ctFlussoRiversamento = flussoRiversamentoUnmarshallerService.unmarshal(ingestionFlowFile);
		log.debug("file CtFlussoRiversamento with Id {} parsed successfully ", ctFlussoRiversamento.getIdentificativoFlusso());

		paymentsReportingIngestionFlowFileValidatorService.validateData(ctFlussoRiversamento, ingestionFlowFileDTO);

		List<PaymentsReportingDTO> dtoList = paymentsReportingMapperService.mapToDtoList(ctFlussoRiversamento, ingestionFlowFileDTO);
		return Pair.of(ctFlussoRiversamento.getIdentificativoFlusso(), dtoList);
	}
}
