package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingIngestionFlowFileActivityResult;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Lazy
@Component
public class PaymentsReportingIngestionFlowFileActivityImpl implements PaymentsReportingIngestionFlowFileActivity {
	private final String ingestionflowFileType;
	private final String archiveDirectory;
	private final IngestionFlowFileDao ingestionFlowFileDao;
	private final IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
	private final FlussoRiversamentoUnmarshallerService flussoRiversamentoUnmarshallerService;
	private final PaymentsReportingIngestionFlowFileValidatorService paymentsReportingIngestionFlowFileValidatorService;
	private final PaymentsReportingMapperService paymentsReportingMapperService;
	private final PaymentsReportingDao paymentsReportingDao;
	private final IngestionFlowFileArchiverService ingestionFlowFileArchiverService;

	public PaymentsReportingIngestionFlowFileActivityImpl(@Value("${ingestion-flow-file-type:R}") String ingestionflowFileType,
	                                                      @Value("${archive-relative-path:processed/}") String archiveRelativePathDirectory,
	                                                      IngestionFlowFileDao ingestionFlowFileDao,
	                                                      IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
	                                                      FlussoRiversamentoUnmarshallerService flussoRiversamentoUnmarshallerService,
	                                                      PaymentsReportingIngestionFlowFileValidatorService paymentsReportingIngestionFlowFileValidatorService,
	                                                      PaymentsReportingMapperService paymentsReportingMapperService,
	                                                      PaymentsReportingDao paymentsReportingDao,
	                                                      IngestionFlowFileArchiverService ingestionFlowFileArchiverService) {
		this.ingestionflowFileType = ingestionflowFileType;
		this.archiveDirectory = archiveRelativePathDirectory;
		this.ingestionFlowFileDao = ingestionFlowFileDao;
		this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
		this.flussoRiversamentoUnmarshallerService = flussoRiversamentoUnmarshallerService;
		this.paymentsReportingIngestionFlowFileValidatorService = paymentsReportingIngestionFlowFileValidatorService;
		this.paymentsReportingMapperService = paymentsReportingMapperService;
		this.paymentsReportingDao = paymentsReportingDao;
		this.ingestionFlowFileArchiverService = ingestionFlowFileArchiverService;
	}

	@Override
	public PaymentsReportingIngestionFlowFileActivityResult processFile(Long ingestionFlowFileId) throws IOException {
		Optional<File> retrievedFile = Optional.empty();
		try {
			IngestionFlowFileDTO ingestionFlowFileDTO = findIngestionFlowFileRecord(ingestionFlowFileId);

			retrievedFile = retrieveFile(ingestionFlowFileDTO);

			Pair<String, List<PaymentsReportingDTO>> pair = parseData(retrievedFile, ingestionFlowFileDTO);

			paymentsReportingDao.saveAll(pair.getRight());
			archive(ingestionFlowFileDTO);

			return new PaymentsReportingIngestionFlowFileActivityResult(List.of(pair.getLeft()), true, null);
		} catch (Exception e) {
			log.error("Error during PaymentsReportingIngestionFlowFileActivity ingestionFlowFileId {} due to: {}", ingestionFlowFileId, e.getMessage());
			if (retrievedFile.isPresent()) {
				Files.delete(retrievedFile.get().toPath());
			}
			return new PaymentsReportingIngestionFlowFileActivityResult(Collections.emptyList(), false, e.getMessage());
		}
	}

	/**
	 * Retrieves the {@link IngestionFlowFileDTO} record for the given ID. If no record is found, throws
	 * an {@link IngestionFlowFileNotFoundException}. Validates the flow file type before returning.
	 *
	 * @param ingestionFlowFileId the ID of the ingestion flow file to retrieve
	 * @return the {@link IngestionFlowFileDTO} corresponding to the given ID
	 * @throws IngestionFlowFileNotFoundException if the record is not found
	 * @throws IllegalArgumentException if the flow file type is invalid
	 */
	private IngestionFlowFileDTO findIngestionFlowFileRecord(Long ingestionFlowFileId) {
		IngestionFlowFileDTO ingestionFlowFileDTO = ingestionFlowFileDao.findById(ingestionFlowFileId)
			.orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot found ingestionFlow having id: "+ ingestionFlowFileId));
		if (!ingestionFlowFileDTO.getFlowFileType().equals(ingestionflowFileType)) {
			throw new IllegalArgumentException("invalid ingestionFlow file type");
		}
		return ingestionFlowFileDTO;
	}

	/**
	 * Retrieves the file associated with the provided {@link IngestionFlowFileDTO} by unzipping and
	 * extracting it from the specified file path.
	 *
	 * @param ingestionFlowFileDTO the ingestion flow file DTO containing file details
	 * @return the extracted {@link List} from the ingestion flow
	 * @throws IOException if there is an error during file retrieval or extraction
	 */
	private Optional<File> retrieveFile(IngestionFlowFileDTO ingestionFlowFileDTO) throws IOException {
		List<Path> paths = ingestionFlowFileRetrieverService
			.retrieveAndUnzipFile(Path.of(ingestionFlowFileDTO.getFilePath()), ingestionFlowFileDTO.getFileName());
		return Optional.of(paths.get(0).toFile());
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
	private Pair<String, List<PaymentsReportingDTO>> parseData(Optional<File> ingestionFlowFile, IngestionFlowFileDTO ingestionFlowFileDTO) {
		CtFlussoRiversamento ctFlussoRiversamento = flussoRiversamentoUnmarshallerService.unmarshal(ingestionFlowFile.get());
		log.debug("file CtFlussoRiversamento with Id {} parsed successfully ", ctFlussoRiversamento.getIdentificativoFlusso());

		paymentsReportingIngestionFlowFileValidatorService.validateOrganization(ctFlussoRiversamento, ingestionFlowFileDTO);

		List<PaymentsReportingDTO> dtoList = paymentsReportingMapperService.mapToDtoList(ctFlussoRiversamento, ingestionFlowFileDTO);
		return Pair.of(ctFlussoRiversamento.getIdentificativoFlusso(), dtoList);
	}

	/**
	 * Archives the file specified in the given {@link IngestionFlowFileDTO}. The file is moved to
	 * the archive directory located within the same file path.
	 *
	 * @param ingestionFlowFileDTO the DTO containing details of the file to be archived.
	 * @throws IOException if an error occurs during file movement or directory creation.
	 */
	private void archive(IngestionFlowFileDTO ingestionFlowFileDTO) throws IOException {
		Path originalFilePath = Paths.get(ingestionFlowFileDTO.getFilePath(), ingestionFlowFileDTO.getFileName());
		Path targetDirectory = Paths.get(ingestionFlowFileDTO.getFilePath(), archiveDirectory);
		ingestionFlowFileArchiverService.archive(List.of(originalFilePath), targetDirectory);
	}
}
