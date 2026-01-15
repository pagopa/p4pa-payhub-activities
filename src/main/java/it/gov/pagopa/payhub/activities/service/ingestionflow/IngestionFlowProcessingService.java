package it.gov.pagopa.payhub.activities.service.ingestionflow;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ErrorFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.exception.organization.OrganizationNotFoundException;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public abstract class IngestionFlowProcessingService<C, R extends IngestionFlowFileResult, E extends ErrorFileDTO> {

    private final ErrorArchiverService<E> errorArchiverService;
    protected final OrganizationService organizationService;
    private final FileExceptionHandlerService fileExceptionHandlerService;

    /**
     * Processes the input Iterator and readerExceptions in order to:
     * * iterate over the rows, consuming them if valid
     * * update the counters
     *
     * @param iterator          Stream of installment ingestion flow file DTOs to be processed.
     * @param readerExceptions  A list which will collect the exceptions thrown during iterator processing
     * @param ingestionFlowFileResult The result where to update the counters
     * @param errorList The error list to update, write and archive
     * @param ingestionFlowFile Metadata of the ingestion file containing details about the ingestion process.
     * @param workingDirectory  The directory where error files will be written if processing fails.
     */
    protected void process(Iterator<C> iterator,
                        List<CsvException> readerExceptions,
                        R ingestionFlowFileResult,
                        IngestionFlowFile ingestionFlowFile,
                        List<E> errorList,
                        Path workingDirectory) {
        long processedRows = 0;
        long totalRows = 0;
        int[] previousReaderExceptionSize = {0};

        while (iterator.hasNext()) {
            totalRows = processReaderExceptions(readerExceptions, ingestionFlowFile, previousReaderExceptionSize, errorList, totalRows);

            totalRows++;

            try {
                if (consumeRow(totalRows, iterator.next(), ingestionFlowFileResult, errorList, ingestionFlowFile)) {
                    processedRows++;
                }
            } catch (Exception e){
                log.error("Not handled exception during IngestionFlowFile processing: ingestionFlowFileId {}, lineNumber {}",
                        ingestionFlowFile.getIngestionFlowFileId(), totalRows, e);
                FileExceptionHandlerService.ErrorDetails errorDetails = fileExceptionHandlerService.mapExceptionToErrorCodeAndMessage(e.getMessage());
                errorList.add(buildErrorDto(ingestionFlowFile.getFileName(), totalRows, FileErrorCode.PROCESSING_ERROR.name(), errorDetails.getErrorMessage()));
            }
        }
        totalRows = processReaderExceptions(readerExceptions, ingestionFlowFile, previousReaderExceptionSize, errorList, totalRows);

        String errorsZipFileName = archiveErrorFiles(ingestionFlowFile, workingDirectory, errorList);

        ingestionFlowFileResult.setTotalRows(totalRows);
        ingestionFlowFileResult.setProcessedRows(processedRows);
        ingestionFlowFileResult.setErrorDescription(errorsZipFileName != null ? "Some rows have failed" : null);
        ingestionFlowFileResult.setDiscardedFileName(errorsZipFileName);
    }

    /** Function to consume single record, it will return true if the record has been correctly processed, false otherwise */
    protected abstract boolean consumeRow(long lineNumber, C row, R ingestionFlowFileResult, List<E> errorList, IngestionFlowFile ingestionFlowFile);
    /** Function to build an instance of the ErrorDTO with the configured params */
    protected abstract E buildErrorDto(String fileName, long lineNumber, String errorCode, String message);

    private long processReaderExceptions(List<CsvException> readerExceptions, IngestionFlowFile ingestionFlowFile, int[] previousReaderExceptionSize, List<E> errorList, long totalRows) {
        int previousSize = previousReaderExceptionSize[0];
        if (readerExceptions.size() > previousSize) {
            List<CsvException> newExceptions = readerExceptions.subList(previousSize, readerExceptions.size());

            newExceptions.forEach(e -> {
                FileExceptionHandlerService.ErrorDetails errorDetails = fileExceptionHandlerService.mapCsvExceptionToErrorCodeAndMessage(e);

                errorList.add(buildErrorDto(
                        ingestionFlowFile.getFileName(),
                        e.getLineNumber(),
                        errorDetails.getErrorCode(),
                        errorDetails.getErrorMessage()
                ));
            });

            long distinctLineCount = newExceptions.stream()
                    .map(CsvException::getLineNumber)
                    .distinct()
                    .count();

            totalRows += distinctLineCount;
            previousReaderExceptionSize[0] = readerExceptions.size();
        }
        return totalRows;
    }

    private String archiveErrorFiles(IngestionFlowFile ingestionFlowFile, Path workingDirectory, List<E> errorList) {
        if (errorList.isEmpty()) {
            log.info("No errors to archive for file: {}", ingestionFlowFile.getFileName());
            return null;
        }

        errorArchiverService.writeErrors(workingDirectory, ingestionFlowFile, errorList);
        String errorsZipFileName = errorArchiverService.archiveErrorFiles(workingDirectory, ingestionFlowFile);
        log.info("Error file archived at: {}", errorsZipFileName);

        return errorsZipFileName;
    }

    protected String getIpaCodeByOrganizationId(Long organizationId){
        Optional<Organization> organizationOptional = organizationService.getOrganizationById(organizationId);
        if (organizationOptional.isEmpty()){
            String errorMessage = String.format("[%s] Organization with id %s not found", FileErrorCode.ORGANIZATION_NOT_FOUND, organizationId);
            log.error(errorMessage);
            throw new OrganizationNotFoundException(errorMessage);
        } else {
            return organizationOptional.get().getIpaCode();
        }
    }
}

