package it.gov.pagopa.payhub.activities.activity.ingestionflow.treasury.xls;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.files.xls.*;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.xls.TreasuryXlsProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@Lazy
@Component
public class TreasuryXlsIngestionActivityImpl
		extends BaseIngestionFlowFileActivity<TreasuryIufIngestionFlowFileResult>
		implements TreasuryXlsIngestionActivity {

	private final XlsService<TreasuryXlsIngestionFlowFileDTO, TreasuryIufIngestionFlowFileResult> xlsService;
	private final TreasuryXlsProcessingService treasuryXlsProcessingService;

	/**
	 * Constructor to initialize dependencies for XLS treasury ingestion.
	 *
	 * @param ingestionFlowFileService          DAO for accessing ingestion flow file records.
	 * @param ingestionFlowFileRetrieverService Service for retrieving and unzipping ingestion flow files.
	 * @param fileArchiverService  				Service for archiving files.
	 * @param treasuryXlsProcessingService      Service for processing treasury XLS files.
	 */
	protected TreasuryXlsIngestionActivityImpl(
			IngestionFlowFileService ingestionFlowFileService,
			IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
			FileArchiverService fileArchiverService,
			TreasuryXlsProcessingService treasuryXlsProcessingService,
			TreasuryXlsServiceImpl xlsService
	) {
		super(ingestionFlowFileService, ingestionFlowFileRetrieverService, fileArchiverService);
		this.treasuryXlsProcessingService = treasuryXlsProcessingService;
		this.xlsService = xlsService;
	}

	@Override
	protected IngestionFlowFile.IngestionFlowFileTypeEnum getHandledIngestionFlowFileType() {
		return IngestionFlowFile.IngestionFlowFileTypeEnum.TREASURY_XLS;
	}

	@Override
	protected TreasuryIufIngestionFlowFileResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {
		if (retrievedFiles.size() > 1) {
			String msg = String.format(
					"Multiple files [%s] found for ingestion flow file ID %s. Only the first file will be processed.",
					retrievedFiles.size(), ingestionFlowFileDTO.getIngestionFlowFileId());
			log.error(msg);
			throw new InvalidIngestionFileException(msg);
		}

		Path filePath = retrievedFiles.getFirst();
		Path workingDirectory = filePath.getParent();
		log.info("Processing file: {}", filePath);

		try {
			return xlsService.readXls(
						filePath,
						xslIterator ->
								treasuryXlsProcessingService.processTreasuryXls(
										xslIterator,
										ingestionFlowFileDTO,
										workingDirectory
								)
					);

		} catch (Exception e) {
			log.error("Error processing file {}: {}", filePath, e.getMessage(), e);
			throw new InvalidIngestionFileException(
					String.format("Error processing file %s: %s", filePath, e.getMessage()));
		}
	}

}
