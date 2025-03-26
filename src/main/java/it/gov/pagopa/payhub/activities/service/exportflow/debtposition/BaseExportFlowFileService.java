package it.gov.pagopa.payhub.activities.service.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.export.debtposition.ExportFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;
import it.gov.pagopa.payhub.activities.exception.exportFlow.ExportFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.exception.exportFlow.InvalidExportStatusException;
import it.gov.pagopa.payhub.activities.service.CsvService;
import it.gov.pagopa.payhub.activities.service.FileArchiverService;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public abstract class BaseExportFlowFileService<E,F,D,C> {

    private final CsvService csvService;
    private final Class<C> csvRowDtoClass;
    private final FileArchiverService fileArchiverService;
    private final Path workingDirectory;
    private final String relativeFileFolder;
    private final String fileNamePrefix;

    protected BaseExportFlowFileService(CsvService csvService, Class<C> csvRowDtoClass, FileArchiverService fileArchiverService, Path workingDirectory, String relativeFileFolder, String fileNamePrefix) {
        this.csvService = csvService;
        this.csvRowDtoClass = csvRowDtoClass;
        this.fileArchiverService = fileArchiverService;
        this.workingDirectory = workingDirectory;
        this.relativeFileFolder = relativeFileFolder;
        this.fileNamePrefix = fileNamePrefix;
    }

    public ExportFlowFileResult executeExport(Long exportFileId) {
        int[] pageNumber = {0};
        long[] exportedRows = {0};

        E exportFileRecord = findExportFileRecord(exportFileId);
        ExportFileStatus exportStatus = getExportStatus(exportFileRecord);
        if (exportStatus.equals(ExportFileStatus.PROCESSING)){
            Long organizationId = getOrganizationId(exportFileRecord);
            String profile = getFlowFileVersion(exportFileRecord);
            Path csvFilePath = workingDirectory.resolve(String.valueOf(organizationId)).resolve(relativeFileFolder).resolve(getExportFileName(exportFileId));
            F exportFilter = getExportFilter(exportFileRecord);

        try {
            csvService.createCsv(csvFilePath, csvRowDtoClass, () ->{
                        List<C> csvRows = retrieveAndMapPage(exportFileRecord, exportFilter, pageNumber[0]++);
                        exportedRows[0] += csvRows.size();
                        return csvRows;
                    }
                    , profile);
        } catch (IOException e) {
            throw new NotRetryableActivityException(e.getMessage());
        }

            ExportFlowFileResult exportFlowFileResult = ExportFlowFileResult.builder()
                    .fileName(getExportFileName(exportFileId))
                    .exportedRows(exportedRows[0])
                    .filePath(csvFilePath.toString())
                    .build();

            createZipArchive(exportFlowFileResult);

            return exportFlowFileResult;
        }else {
            throw new InvalidExportStatusException("Export file status wrong during the export process for file ID %s, attempted status %s, actual %s".formatted(exportFileId, ExportFileStatus.PROCESSING, ExportFileStatus.REQUESTED));
        }
    }

    /**
     * Creates a ZIP archive for the specified export result.
     * This method compresses the CSV file into a ZIP archive using the {@link FileArchiverService#compressAndArchive(List, Path, Path)} method.
     *
     * @param exportFlowFileResult The export result.
     * @throws IllegalStateException If an error occurs during compression and archiving due to filesystem or permission issues.
     */
    private void createZipArchive(ExportFlowFileResult exportFlowFileResult) {
        try {
            Path tmpZipFilePath = Path.of(exportFlowFileResult.getFilePath())
                    .getParent()
                    .resolve(Utilities.replaceFileExtension(exportFlowFileResult.getFileName(), ".zip"));
            fileArchiverService.compressAndArchive(List.of(Path.of(exportFlowFileResult.getFilePath())), tmpZipFilePath, workingDirectory);
        } catch (IOException e) {
            throw new IllegalStateException("Error during compression and archiving: " + e.getMessage(), e);
        }
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
     * Retrieves the export filter for the given export file.
     * This method should be implemented by subclasses to provide the specific filter
     * required for retrieving data in pages. The filter is created once and reused
     * for all page retrievals, ensuring efficiency.
     *
     * @param exportFile The export file record for which to retrieve the filter.
     * @return The export filter of type F.
     */
    protected abstract F getExportFilter(E exportFile);

    /**
     * Retrieves the export status of the given export file.
     * This method is an abstract method that must be implemented by subclasses to provide the specific
     * logic for retrieving the export status.
     *
     * @param exportFile The export file for which to retrieve the status.
     * @return The export status of the file.
     */
    protected abstract ExportFileStatus getExportStatus(E exportFile);

}
