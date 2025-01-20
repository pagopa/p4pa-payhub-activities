package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingIngestionFlowFileActivityResult;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.FlussoRiversamentoUnmarshallerService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.PaymentsReportingIngestionFlowFileValidatorService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.PaymentsReportingMapperService;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import lombok.extern.slf4j.Slf4j;
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
	private final PaymentsReportingService paymentsReportingService;

	public PaymentsReportingIngestionFlowFileActivityImpl(
	                                                      IngestionFlowFileDao ingestionFlowFileDao,
	                                                      IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
	                                                      FlussoRiversamentoUnmarshallerService flussoRiversamentoUnmarshallerService,
	                                                      PaymentsReportingIngestionFlowFileValidatorService paymentsReportingIngestionFlowFileValidatorService,
	                                                      PaymentsReportingMapperService paymentsReportingMapperService,
	                                                      PaymentsReportingService paymentsReportingService,
	                                                      IngestionFlowFileArchiverService ingestionFlowFileArchiverService) {
		super(ingestionFlowFileDao, ingestionFlowFileRetrieverService, ingestionFlowFileArchiverService);
		this.flussoRiversamentoUnmarshallerService = flussoRiversamentoUnmarshallerService;
		this.paymentsReportingIngestionFlowFileValidatorService = paymentsReportingIngestionFlowFileValidatorService;
		this.paymentsReportingMapperService = paymentsReportingMapperService;
		this.paymentsReportingService = paymentsReportingService;
	}

	@Override
	protected IngestionFlowFileType getHandledIngestionFlowFileType() {
		return IngestionFlowFileType.PAYMENTS_REPORTING;
	}

	@Override
	protected PaymentsReportingIngestionFlowFileActivityResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFileDTO ingestionFlowFileDTO) {
		List<PaymentsReporting> paymentsReportings = parseData(retrievedFiles.getFirst().toFile(), ingestionFlowFileDTO);
		paymentsReportingService.saveAll(paymentsReportings);

		List<TransferSemanticKeyDTO> transferSemanticKeys = paymentsReportings.stream()
			.map(paymentsReportingMapperService::map2TransferSemanticKeyDto)
			.toList();
		return new PaymentsReportingIngestionFlowFileActivityResult(transferSemanticKeys, true, null);
	}

	@Override
	protected PaymentsReportingIngestionFlowFileActivityResult onErrorResult(Exception e) {
		return new PaymentsReportingIngestionFlowFileActivityResult(Collections.emptyList(), false, e.getMessage());
	}

	/**
	 * Parses the provided file into a {@link CtFlussoRiversamento} object and maps its content into a list
	 * of {@link PaymentsReporting}. Validates the file's organization data.
	 *
	 * @param ingestionFlowFile the file to be parsed
	 * @param ingestionFlowFileDTO the ingestion flow file DTO containing additional context
	 * @return a list of {@link PaymentsReporting} objects
	 * @throws IllegalArgumentException if the file content does not conform to the expected structure
	 */
	private List<PaymentsReporting> parseData(File ingestionFlowFile, IngestionFlowFileDTO ingestionFlowFileDTO) {
		CtFlussoRiversamento ctFlussoRiversamento = flussoRiversamentoUnmarshallerService.unmarshal(ingestionFlowFile);
		log.debug("file CtFlussoRiversamento with Id {} parsed successfully ", ctFlussoRiversamento.getIdentificativoFlusso());

		paymentsReportingIngestionFlowFileValidatorService.validateData(ctFlussoRiversamento, ingestionFlowFileDTO);

		return paymentsReportingMapperService.map2PaymentsReportings(ctFlussoRiversamento, ingestionFlowFileDTO);
	}
}
