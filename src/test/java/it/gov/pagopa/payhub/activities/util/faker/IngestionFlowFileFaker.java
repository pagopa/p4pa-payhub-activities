package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;

import java.time.OffsetDateTime;

public class IngestionFlowFileFaker {


    public static IngestionFlowFile buildIngestionFlowFile(){
        return IngestionFlowFile.builder()
                .ingestionFlowFileId(1L)
                .organizationId(0L)
                .status(IngestionFlowFile.StatusEnum.PROCESSING)
                .numTotalRows(3L)
                .numCorrectlyImportedRows(2L)
                .creationDate(OffsetDateTime.now())
                .updateDate(OffsetDateTime.now())
                .operatorExternalId("operatorExternalId")
                .filePathName("filePathName")
                .fileName("fileName.csv")
                .pdfGenerated(2L)
                .codError("codError")
                .pspIdentifier("PspId")
                .flowDateTime(OffsetDateTime.now())
                .discardFileName("DiscardFileName")
                .flowFileType(IngestionFlowFile.FlowFileTypeEnum.PAYMENTS_REPORTING)
                .build();
    }

}
