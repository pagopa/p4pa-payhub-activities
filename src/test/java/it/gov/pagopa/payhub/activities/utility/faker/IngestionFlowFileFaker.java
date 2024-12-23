package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;

import java.time.ZoneId;
import java.util.Date;

public class IngestionFlowFileFaker {

    public static final Date now = new Date();

    public static IngestionFlowFileDTO buildIngestionFlowFileDTO(){
        return IngestionFlowFileDTO.builder()
                .ingestionFlowFileId(1L)
                .version(1)
                .org(OrganizationDTO.builder().build())
                .status("status")
                .iuf("iuf")
                .numTotalRows(3L)
                .numCorrectlyImportedRows(2L)
                .creationDate(now)
                .lastUpdateDate(now)
                .flagActive(true)
                .operatorName("operatorName")
                .flagSpontaneous(Boolean.TRUE)
                .filePath("filePathName")
                .fileName("fileName")
                .pdfGenerated(2L)
                .codRequestToken("codRequestToken")
                .codError("codError")
                .pspIdentifier("PspId")
                .flowDateTime(now.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .fileSourceCode("FileSourceCode")
                .discardFileName("DiscardFileName")
                .flowFileType(IngestionFlowFileType.PAYMENTS_REPORTING)
                .build();
    }

}
