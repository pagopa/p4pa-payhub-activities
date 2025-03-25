package it.gov.pagopa.payhub.activities.service.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.export.ExportFileErrorDTO;
import it.gov.pagopa.payhub.activities.dto.export.debtposition.ExportFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.exportFlow.ExportFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.exception.exportFlow.InvalidExportFileException;
import it.gov.pagopa.payhub.activities.service.CsvService;
import it.gov.pagopa.payhub.activities.service.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.exportflow.ExportFileErrorArchiverService;
import it.gov.pagopa.payhub.activities.util.Utilities;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class BaseExportFlowFileService<E,F,D,C> {

    private final CsvService csvService;
    private final Class<C> csvRowDtoClass;
    private final ExportFileErrorArchiverService exportFileErrorArchiverService;
    private final FileArchiverService fileArchiverService;
    private final Path workingDirectory;
    private final String relativeFileFolder;
    private final String fileNamePrefix;

    protected BaseExportFlowFileService(CsvService csvService, Class<C> csvRowDtoClass, ExportFileErrorArchiverService exportFileErrorArchiverService, FileArchiverService fileArchiverService, Path workingDirectory, String relativeFileFolder, String fileNamePrefix) {
        this.csvService = csvService;
        this.csvRowDtoClass = csvRowDtoClass;
        this.exportFileErrorArchiverService = exportFileErrorArchiverService;
        this.fileArchiverService = fileArchiverService;
        this.workingDirectory = workingDirectory;
        this.relativeFileFolder = relativeFileFolder;
        this.fileNamePrefix = fileNamePrefix;
    }

    public ExportFlowFileResult executeExport(Long exportFileId){
        int[] pageNumber = {0};
        long exportedRows = 0;
        long errorCount = 0;
        List<ExportFileErrorDTO> errorMessages;

        E exportFileRecord = findExportFileRecord(exportFileId);
        Long organizationId = getOrganizationId(exportFileRecord);
        String profile = getFlowFileVersion(exportFileRecord);
        Path csvFilePath = workingDirectory.resolve(String.valueOf(organizationId)).resolve(relativeFileFolder).resolve(getExportFileName(exportFileId));

        if (checkStatusExportFileRecord(exportFileRecord)){
            try {
                F exportFilter = getExportFilter(exportFileRecord);
                List<C> csvRows = retrieveAndMapPage(exportFileRecord, exportFilter, pageNumber[0]++);
                if (!csvRows.isEmpty()){
                    exportedRows += csvRows.size();
                    csvService.createCsv(csvFilePath, csvRowDtoClass, () -> csvRows, profile);
                }

            } catch (IOException e) {
                errorCount ++;
                errorMessages = new ArrayList<>();
                errorMessages.add(ExportFileErrorDTO.builder()
                        .fileName(getExportFileName(exportFileId))
                        .errorCode("Export Exception")
                        .errorMessage(e.getMessage())
                        .build());

                log.error("Error processing creation csv file with exportFileId {}: {}", exportFileId, e.getMessage());
                archiveErrorFiles(workingDirectory, exportFileId, organizationId, errorMessages);
            }
        }

        ExportFlowFileResult exportFlowFileResult = ExportFlowFileResult.builder()
                .fileName(getExportFileName(exportFileId))
                .exportedRows(exportedRows - errorCount)
                .filePath(csvFilePath.toString())
                .build();

        createZipArchive(exportFlowFileResult);

        return exportFlowFileResult;
    }

    private void createZipArchive(ExportFlowFileResult exportFlowFileResult) {
        try {
            Path tmpZipFilePath = Path.of(exportFlowFileResult.getFilePath())
                    .getParent()
                    .resolve(Utilities.replaceFileExtension(exportFlowFileResult.getFileName(), ".zip"));
            fileArchiverService.compressAndArchive(List.of(Path.of(exportFlowFileResult.getFilePath())), tmpZipFilePath, workingDirectory);
        } catch (IOException e) {
            throw new InvalidExportFileException("Error during compression and archiving: " + e.getMessage());
        }
    }

    /**
     * Archives error files to a ZIP archive and moves the archive to a target directory.
     * This method searches for error files in the specified working directory, compresses them into a ZIP archive,
     * and moves the archive to the target directory. The target directory is constructed using the organization ID,
     * the relative file folder, and the base error folder.
     *
     * @param workingDirectory The working directory where error files are located.
     * @param exportFileId     The ID of the export file, used to generate the ZIP file name.
     * @param organizationId   The ID of the organization, used to construct the target directory.
     * @param errorList        The list of error DTOs, used to determine if there are errors to archive.
     */
    private void archiveErrorFiles(Path workingDirectory, Long exportFileId, Long organizationId, List<ExportFileErrorDTO> errorList) {
        if (errorList.isEmpty()) {
            log.info("No errors to archive for file: {}", getExportFileName(exportFileId));
            return;
        }

        exportFileErrorArchiverService.writeErrors(workingDirectory, getExportFileName(exportFileId), errorList);
        String errorsZipFileName = exportFileErrorArchiverService.archiveErrorFiles(workingDirectory, organizationId, relativeFileFolder, getExportFileName(exportFileId));

        log.info("Error file archived at: {}", errorsZipFileName);
    }

    /**
     * Retrieves the name of the export file based on the export file ID.
     * This method generates the file name using a predefined prefix and the provided export file ID,
     * appending the ".csv" extension.
     *
     * @param exportFileId The ID of the export file.
     * @return The generated export file name as a String.
     */
    protected String getExportFileName(Long exportFileId){
        return fileNamePrefix + "_" + exportFileId + ".csv";
    }

    /**
     * Retrieves a page of data and maps it to a list of CSV DTOs.
     *
     * @param exportFile the export file for which the data is to be retrieved.
     * @param filter     the filter to apply when retrieving the page of data.
     * @param pageNumber the page number to be retrieved.
     * @return a list of CSV DTOs mapped from the retrieved data.
     */
    private List<C> retrieveAndMapPage(E exportFile, F filter, int pageNumber){
        List<D> data = retrievePage(exportFile, filter, pageNumber);
        return data.stream().map(this::map2Csv).toList();
    }

    /**
     * Finds the export file record using the API based on the declared flowFileType.
     *
     * @param exportFileId the ID of the export file to be found
     * @return the export file record
     * @throws ExportFlowFileNotFoundException if the export file is not found
     */
    protected abstract E findExportFileRecord(Long exportFileId);

    /**
     * Retrieves the organization ID associated with the given export file.
     *
     * @param exportFile the export file for which the organization ID is to be retrieved
     * @return the organization ID
     */
    protected abstract Long getOrganizationId(E exportFile);

    /**
     * Retrieves the FlowFileVersion associated with the given export file.
     *
     * @param exportFile the export file for which the FlowFileVersion is to be retrieved
     * @return the FlowFileVersion
     */
    protected abstract String getFlowFileVersion(E exportFile);

    /**
     * Executes a query using the specific API based on the declared type to retrieve a page of data.
     *
     * @param exportFile the export file for which the data is to be retrieved.
     * @param filter     the filter to apply when retrieving the page of data.
     * @param pageNumber the page number to be retrieved.
     * @return a list of data for the specified page.
     */
    protected abstract List<D> retrievePage(E exportFile, F filter, int pageNumber);

    /**
     * Transforms the records obtained from the query into a DTO that represents the CSV.
     *
     * @param retrievedPage the page of records obtained from the query
     * @return the DTO representing the CSV
     */
    protected abstract C map2Csv(D retrievedPage);

    /**
     * Checks the status of the export file record.
     * This method should be implemented by subclasses to provide the specific logic for checking the status.
     *
     * @param exportFile The export file record.
     * @return {@code true} if the status is valid, {@code false} otherwise.
     */
    protected abstract boolean checkStatusExportFileRecord(E exportFile);

    /**
     * Retrieves the export filter for the given export file.
     * This method should be implemented by subclasses to provide the specific filter
     * required for retrieving data in pages. The filter is created once and reused
     * for all page retrievals, ensuring efficiency.
     *
     * @param exportFile The export file record for which to retrieve the filter.
     * @return The export filter of type F.
     */
    protected abstract F getExportFilter(E exportFile);

}
