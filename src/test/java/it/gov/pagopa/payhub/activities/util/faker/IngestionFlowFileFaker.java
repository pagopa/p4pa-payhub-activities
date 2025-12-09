package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFileStatus;

import static it.gov.pagopa.payhub.activities.util.TestUtils.OFFSETDATETIME;

public class IngestionFlowFileFaker {

    public static IngestionFlowFile buildIngestionFlowFile(){
        return TestUtils.getPodamFactory().manufacturePojo(IngestionFlowFile.class)
                .ingestionFlowFileId(1L)
                .organizationId(1L)
                .status(IngestionFlowFileStatus.PROCESSING)
                .numTotalRows(3L)
                .numCorrectlyImportedRows(2L)
                .creationDate(OFFSETDATETIME)
                .updateDate(OFFSETDATETIME)
                .operatorExternalId("operatorExternalId")
                .filePathName("filePathName")
                .fileName("fileName.zip")
                .pdfGenerated(2L)
                .errorDescription("errorDescription")
                .pspIdentifier("PspId")
                .flowDateTime(OFFSETDATETIME)
                .discardFileName("DiscardFileName")
                .ingestionFlowFileType(IngestionFlowFile.IngestionFlowFileTypeEnum.PAYMENTS_REPORTING)
                .fileSize(100L)
                .operatorExternalId("OPERATORID")
                .fileOrigin("PAGOPA")
                .fileVersion("FILEVERSION");
    }

}
