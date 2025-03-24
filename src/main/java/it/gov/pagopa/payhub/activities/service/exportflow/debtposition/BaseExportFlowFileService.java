package it.gov.pagopa.payhub.activities.service.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.export.ErrorExportDTO;
import it.gov.pagopa.payhub.activities.dto.export.debtposition.ExportFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.exportFlow.ExportFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.service.CsvService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class BaseExportFlowFileService<E,D,C> {

    private final CsvService csvService;
    private final Class<C> csvRowDtoClass;
    private final BaseErrorsArchiverService baseErrorsArchiverService;

    protected BaseExportFlowFileService(CsvService csvService, Class<C> csvRowDtoClass, BaseErrorsArchiverService baseErrorsArchiverService) {
        this.csvService = csvService;
        this.csvRowDtoClass = csvRowDtoClass;
        this.baseErrorsArchiverService = baseErrorsArchiverService;
    }

    public ExportFlowFileResult executeExport(Long exportFileId, Path workingDirectory){
        int[] pageNumber = {0};
        long exportedRows = 0;
        long errorCount = 0;
        List<ErrorExportDTO> errorMessages = new ArrayList<>();

        E exportFileRecord = findExportFileRecord(exportFileId);

        Long organizationId = getOrganizationId(exportFileRecord);
        String profile = getFlowFileVersion(exportFileRecord);

        Path csvFilePath = workingDirectory.resolve(String.valueOf(organizationId)).resolve(getRelativeFileFolder()).resolve(getExportFileName(exportFileId));

        try {
            List<C> csvRows;
            do {
                csvRows = retrieveAndMapPage(exportFileRecord, pageNumber[0]++);
                if (!csvRows.isEmpty()){
                    exportedRows += csvRows.size();
                    List<C> finalCsvRows = csvRows;
                    csvService.createCsvFromBean(csvFilePath, csvRowDtoClass, () -> finalCsvRows, profile);
                }

            } while (!csvRows.isEmpty());

        } catch (IOException e) {
            errorCount ++;
            errorMessages.add(ErrorExportDTO.builder()
                    .fileName(getExportFileName(exportFileId))
                    .errorCode("Export Exception")
                    .errorMessage(e.getMessage())
                    .build());

            log.error("Error processing creation csv file with exportFileId {}: {}", exportFileId, e.getMessage());
            archiveErrorFiles(workingDirectory, exportFileId, organizationId, errorMessages);
        }

        return ExportFlowFileResult.builder()
                .fileName(getExportFileName(exportFileId))
                .exportedRows(exportedRows - errorCount)
                .filePath(csvFilePath.toString())
                .build();
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
    private void archiveErrorFiles(Path workingDirectory, Long exportFileId, Long organizationId, List<ErrorExportDTO> errorList) {
        if (errorList.isEmpty()) {
            log.info("No errors to archive for file: {}", getExportFileName(exportFileId));
            return;
        }

        baseErrorsArchiverService.writeErrors(workingDirectory, getExportFileName(exportFileId), errorList);
        String errorsZipFileName = baseErrorsArchiverService.archiveErrorFiles(workingDirectory, organizationId, getRelativeFileFolder(), getExportFileName(exportFileId));

        log.info("Error file archived at: {}", errorsZipFileName);
    }

    /**
     * Retrieves a page of data and maps it to a list of CSV DTOs.
     *
     * @param exportFile the export file for which the data is to be retrieved
     * @param pageNumber the page number to be retrieved
     * @return a list of CSV DTOs mapped from the retrieved data
     */
    private List<C> retrieveAndMapPage(E exportFile, int pageNumber){
        List<D> data = retrievePage(exportFile, pageNumber);
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
     * @param exportFile the export file for which the data is to be retrieved
     * @param pageNumber the page number to be retrieved
     * @return a list of data for the specified page
     */
    protected abstract List<D> retrievePage(E exportFile, int pageNumber);

    /**
     * Transforms the records obtained from the query into a DTO that represents the CSV.
     *
     * @param retrievedPage the page of records obtained from the query
     * @return the DTO representing the CSV
     */
    protected abstract C map2Csv(D retrievedPage);

    /**
     * Retrieves the relative folder path where the export file will be stored.
     * This method should be implemented by subclasses to provide the specific
     * folder structure relative to the working directory.
     *
     * @return The relative folder path as a String.
     */
    protected abstract String getRelativeFileFolder();

    /**
     * Retrieves the name of the export file.
     * This method should be implemented by subclasses to provide the specific
     * file name for the export.
     *
     * @return The export file name as a String.
     */
    protected abstract String getExportFileName(Long exportFileId);
}
