package it.gov.pagopa.payhub.activities.activity.ingestionflow.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsReportingTransferDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentsreporting.PaymentsReportingIngestionFlowFileActivityResult;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.paymentsreporting.FlussoRiversamentoUnmarshallerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.paymentsreporting.PaymentsReportingIngestionFlowFileValidatorService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.paymentsreporting.PaymentsReportingMapperService;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@Slf4j
@Lazy
@Component
public class PaymentsReportingIngestionFlowFileActivityImpl extends BaseIngestionFlowFileActivity<PaymentsReportingIngestionFlowFileActivityResult> implements PaymentsReportingIngestionFlowFileActivity {

	private final FlussoRiversamentoUnmarshallerService flussoRiversamentoUnmarshallerService;
	private final PaymentsReportingIngestionFlowFileValidatorService paymentsReportingIngestionFlowFileValidatorService;
	private final PaymentsReportingMapperService paymentsReportingMapperService;
	private final PaymentsReportingService paymentsReportingService;

	public PaymentsReportingIngestionFlowFileActivityImpl(
	                                                      IngestionFlowFileService ingestionFlowFileService,
	                                                      IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
	                                                      FlussoRiversamentoUnmarshallerService flussoRiversamentoUnmarshallerService,
	                                                      PaymentsReportingIngestionFlowFileValidatorService paymentsReportingIngestionFlowFileValidatorService,
	                                                      PaymentsReportingMapperService paymentsReportingMapperService,
	                                                      PaymentsReportingService paymentsReportingService,
	                                                      FileArchiverService fileArchiverService) {
		super(ingestionFlowFileService, ingestionFlowFileRetrieverService, fileArchiverService);
		this.flussoRiversamentoUnmarshallerService = flussoRiversamentoUnmarshallerService;
		this.paymentsReportingIngestionFlowFileValidatorService = paymentsReportingIngestionFlowFileValidatorService;
		this.paymentsReportingMapperService = paymentsReportingMapperService;
		this.paymentsReportingService = paymentsReportingService;
	}

	@Override
	protected Set<IngestionFlowFile.IngestionFlowFileTypeEnum> getHandledIngestionFlowFileTypes() {
		return Set.of(IngestionFlowFile.IngestionFlowFileTypeEnum.PAYMENTS_REPORTING, IngestionFlowFile.IngestionFlowFileTypeEnum.PAYMENTS_REPORTING_PAGOPA);
	}

	@Override
	protected IngestionFlowFile.IngestionFlowFileTypeEnum getHandledIngestionFlowFileType() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected PaymentsReportingIngestionFlowFileActivityResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {
		Pair<String, List<PaymentsReporting>> version2paymentsReportings = parseData(retrievedFiles.getFirst().toFile(), ingestionFlowFileDTO);
		List<PaymentsReporting> paymentsReportings = version2paymentsReportings.getValue();
		paymentsReportingService.saveAll(paymentsReportings);

		List<PaymentsReportingTransferDTO> transferSemanticKeys = paymentsReportings.stream()
			.map(paymentsReportingMapperService::map).toList();

		PaymentsReporting first = paymentsReportings.getFirst();
		String iuf = first.getIuf(); // The iuf is the same for entire file
		Long organizationId = first.getOrganizationId(); // The organizationId is the same for entire file
		return PaymentsReportingIngestionFlowFileActivityResult.builder()
				.iuf(iuf)
				.organizationId(organizationId)
				.transfers(transferSemanticKeys)
				.fileVersion(version2paymentsReportings.getKey())
				.totalRows(paymentsReportings.size())
				.processedRows(paymentsReportings.size())
				.build();
	}

	/**
	 * Parses the provided file into a {@link CtFlussoRiversamento} object and maps its content into a list
	 * of {@link PaymentsReporting}. Validates the file's organization data.
	 *
	 * @param ingestionFlowFile the file to be parsed
	 * @param ingestionFlowFileDTO the ingestion flow file DTO containing additional context
	 * @return a pair having the xsd version used to parse the file and the list of {@link PaymentsReporting} objects
	 * @throws IllegalArgumentException if the file content does not conform to the expected structure
	 */
	private Pair<String, List<PaymentsReporting>> parseData(File ingestionFlowFile, IngestionFlowFile ingestionFlowFileDTO) {
		CtFlussoRiversamento ctFlussoRiversamento = flussoRiversamentoUnmarshallerService.unmarshal(ingestionFlowFile);
		log.debug("file CtFlussoRiversamento with Id {} parsed successfully ", ctFlussoRiversamento.getIdentificativoFlusso());

		paymentsReportingIngestionFlowFileValidatorService.validateData(ctFlussoRiversamento, ingestionFlowFileDTO);

		return Pair.of("1.0.3",
				paymentsReportingMapperService.map2PaymentsReportings(ctFlussoRiversamento, ingestionFlowFileDTO)
		);
	}
}
