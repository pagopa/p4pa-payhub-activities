package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;

import java.time.Instant;
import java.time.ZoneId;

public class IngestionFlowFileFaker {


    public static IngestionFlowFileDTO buildIngestionFlowFileDTO(){
        return IngestionFlowFileDTO.builder()
                .ingestionFlowFileId(1L)
                .version(1)
                .org(OrganizationDTO.builder().build())
                .status("status")
                .iuf("iuf")
                .numTotalRows(3L)
                .numCorrectlyImportedRows(2L)
                .creationDate(Instant.now())
                .lastUpdateDate(Instant.now())
                .flagActive(true)
                .operatorExternalUserId("operatorExternalId")
                .flagSpontaneous(Boolean.TRUE)
                .filePathName("filePathName")
                .fileName("fileName")
                .pdfGenerated(2L)
                .codRequestToken("codRequestToken")
                .codError("codError")
                .pspIdentifier("PspId")
                .flowDateTime(Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .fileSourceCode("FileSourceCode")
                .discardFileName("DiscardFileName")
                .flowFileType(IngestionFlowFileType.PAYMENTS_REPORTING)
                .build();
    }

}
