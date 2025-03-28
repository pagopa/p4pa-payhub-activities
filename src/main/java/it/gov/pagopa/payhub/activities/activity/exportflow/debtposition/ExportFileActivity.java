package it.gov.pagopa.payhub.activities.activity.exportflow.debtposition;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.export.debtposition.ExportFileResult;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;

/**
 * Interface for the ExportFileActivity.
 * Defines methods for processing export of files based on an exportFileId.
 */
@ActivityInterface
public interface ExportFileActivity {

    /**
     * Processes an export files based on the provided exportFileId and exportFileTypeEnum.
     *
     * @param exportFileId The identifier related to the export file to process.
     * @param exportFileTypeEnum The identifier representing the type of file entity to be processed in the export operation.
     * @return {@link ExportFileResult} containing the return file path, name, number of exported lines.
     */
    @ActivityMethod
    ExportFileResult executeExport(Long exportFileId, ExportFile.ExportFileTypeEnum exportFileTypeEnum);
}
