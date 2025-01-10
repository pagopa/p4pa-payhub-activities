package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public abstract class BaseIngestionFlowFileActivity<T> {

	private final IngestionFlowFileDao ingestionFlowFileDao;
	private final IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
	private final IngestionFlowFileArchiverService ingestionFlowFileArchiverService;

	protected BaseIngestionFlowFileActivity(
	                                                      IngestionFlowFileDao ingestionFlowFileDao,
	                                                      IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
	                                                      IngestionFlowFileArchiverService ingestionFlowFileArchiverService) {
		this.ingestionFlowFileDao = ingestionFlowFileDao;
		this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
		this.ingestionFlowFileArchiverService = ingestionFlowFileArchiverService;
	}

	public T processFile(Long ingestionFlowFileId) {
		log.info("Processing IngestionFlowFile {} using class {}", ingestionFlowFileId, getClass());
		List<Path> retrievedFiles = null;
		try {
			IngestionFlowFileDTO ingestionFlowFileDTO = findIngestionFlowFileRecord(ingestionFlowFileId);

			retrievedFiles = retrieveFiles(ingestionFlowFileDTO);

			T result = handleRetrievedFiles(retrievedFiles, ingestionFlowFileDTO);

			ingestionFlowFileArchiverService.archive(ingestionFlowFileDTO);

			return result;
		} catch (Exception e) {
			log.error("Error during processing of ingestionFlowFileId {} in class {} due to: {}", ingestionFlowFileId, getClass(), e.getMessage());
			return onErrorResult(e);
		} finally {
			deletion(retrievedFiles);
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

		if (!getHandledIngestionFlowFileType().equals(ingestionFlowFileDTO.getFlowFileType())) {
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
	private List<Path> retrieveFiles(IngestionFlowFileDTO ingestionFlowFileDTO) throws IOException {
		return ingestionFlowFileRetrieverService
			.retrieveAndUnzipFile(Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());
	}

	/**
	 * Deletes the specified List of path if it is not null.
	 *
	 * @param pathsToDelete The list of path to delete.
	 */
	private void deletion(List<Path> pathsToDelete) {
		if (pathsToDelete != null && !pathsToDelete.isEmpty()) {
			for (Path pathToDelete : pathsToDelete) {
				try {
					Files.delete(pathToDelete);
				} catch (IOException e) {
					log.warn("Error occurred while deleting file: " + pathToDelete + " " +e.getMessage());
				}
			}
		}
	}

	/** The {@link IngestionFlowFileType} supported */
	protected abstract IngestionFlowFileType getHandledIngestionFlowFileType();

	/** It will process retrieve files */
	protected abstract T handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFileDTO ingestionFlowFileDTO);

	/** It will build the result in case of error */
	protected abstract T onErrorResult(Exception e);
}
