package it.gov.pagopa.payhub.activities.activity.exportflow.debtposition;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.export.debtposition.ExportFlowFileResult;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;

/**
 * Interface for the ExportFlowFileActivity.
 * Defines methods for processing export of files based on an exportFileId.
 */
@ActivityInterface
public interface ExportFlowFileActivity {

    /**
     * Processes an export files based on the provided exportFileId and flowFileTypeEnum.
     *
     * @param exportFileId The identifier related to the export file to process.
     * @param flowFileTypeEnum The identifier representing the type of flow file entity to be processed in the export operation.
     * @return {@link ExportFlowFileResult} containing the return file path, name, number of exported lines.
     */
    @ActivityMethod
    ExportFlowFileResult executeExport(Long exportFileId, ExportFile.FlowFileTypeEnum flowFileTypeEnum);
}
