package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowTypeNotSupportedException;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public abstract class BaseIngestionFlowFileActivity<T extends IngestionFlowFileResult> {

	private final IngestionFlowFileService ingestionFlowFileService;
	private final IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
	private final FileArchiverService fileArchiverService;

	protected BaseIngestionFlowFileActivity(
	                                                      IngestionFlowFileService ingestionFlowFileService,
	                                                      IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
	                                                      FileArchiverService fileArchiverService) {
		this.ingestionFlowFileService = ingestionFlowFileService;
		this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
		this.fileArchiverService = fileArchiverService;
	}

	public T processFile(Long ingestionFlowFileId) {
		log.info("Processing IngestionFlowFile {} using class {}", ingestionFlowFileId, getClass());
		List<Path> retrievedFiles = null;
		try {
			IngestionFlowFile ingestionFlowFileDTO = findIngestionFlowFileRecord(ingestionFlowFileId);

			retrievedFiles = retrieveFiles(ingestionFlowFileDTO);

			T result = handleRetrievedFiles(retrievedFiles, ingestionFlowFileDTO);
			result.setOrganizationId(ingestionFlowFileDTO.getOrganizationId());

			fileArchiverService.archive(ingestionFlowFileDTO);

			return result;
		} finally {
			deletion(retrievedFiles);
		}
	}

	/**
	 * Retrieves the {@link IngestionFlowFile} record for the given ID. If no record is found, throws
	 * an {@link IngestionFlowFileNotFoundException}. Validates the flow file type before returning.
	 *
	 * @param ingestionFlowFileId the ID of the ingestion flow file to retrieve
	 * @return the {@link IngestionFlowFile} corresponding to the given ID
	 * @throws IngestionFlowFileNotFoundException if the record is not found
	 * @throws IllegalArgumentException if the flow file type is invalid
	 */
	private IngestionFlowFile findIngestionFlowFileRecord(Long ingestionFlowFileId) {
		IngestionFlowFile ingestionFlowFileDTO = ingestionFlowFileService.findById(ingestionFlowFileId)
			.orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot found ingestionFlow having id: "+ ingestionFlowFileId));

		if (!(getHandledIngestionFlowFileType()).equals(ingestionFlowFileDTO.getIngestionFlowFileType())) {
			throw new IngestionFlowTypeNotSupportedException("invalid ingestionFlow file type: " + ingestionFlowFileDTO.getIngestionFlowFileType() + " expected " + getHandledIngestionFlowFileType());
		}

		return ingestionFlowFileDTO;
	}

	/**
	 * Retrieves the file associated with the provided {@link IngestionFlowFile} by unzipping and
	 * extracting it from the specified file path.
	 *
	 * @param ingestionFlowFileDTO the ingestion flow file DTO containing file details
	 * @return the extracted {@link List} from the ingestion flow
     */
	private List<Path> retrieveFiles(IngestionFlowFile ingestionFlowFileDTO) {
		return ingestionFlowFileRetrieverService
			.retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());
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

	/** The {@link IngestionFlowFile.IngestionFlowFileTypeEnum} supported */
	protected abstract IngestionFlowFile.IngestionFlowFileTypeEnum getHandledIngestionFlowFileType();

	/** It will process retrieve files */
	protected abstract T handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO);
}
