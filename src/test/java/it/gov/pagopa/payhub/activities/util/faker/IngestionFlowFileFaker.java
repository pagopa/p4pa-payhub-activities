package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;

import static it.gov.pagopa.payhub.activities.util.TestUtils.OFFSETDATETIME;

public class IngestionFlowFileFaker {

    public static IngestionFlowFile buildIngestionFlowFile(){
        return TestUtils.getPodamFactory().manufacturePojo(IngestionFlowFile.class)
                .ingestionFlowFileId(1L)
                .organizationId(1L)
                .status(IngestionFlowFile.StatusEnum.PROCESSING)
                .numTotalRows(3L)
                .numCorrectlyImportedRows(2L)
                .creationDate(OFFSETDATETIME)
                .updateDate(OFFSETDATETIME)
                .operatorExternalId("operatorExternalId")
                .filePathName("filePathName")
                .fileName("fileName.csv")
                .pdfGenerated(2L)
                .errorDescription("errorDescription")
                .pspIdentifier("PspId")
                .flowDateTime(OFFSETDATETIME)
                .discardFileName("DiscardFileName")
                .flowFileType(IngestionFlowFile.FlowFileTypeEnum.PAYMENTS_REPORTING)
                .fileSize(100L)
                .fileOrigin("PAGOPA");
    }

}
