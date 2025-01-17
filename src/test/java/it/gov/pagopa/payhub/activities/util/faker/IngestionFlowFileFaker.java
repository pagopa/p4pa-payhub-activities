package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import it.gov.pagopa.pu.organization.dto.generated.Organization;

import java.time.Instant;
import java.time.ZoneId;

public class IngestionFlowFileFaker {


    public static IngestionFlowFileDTO buildIngestionFlowFileDTO(){
        return IngestionFlowFileDTO.builder()
                .ingestionFlowFileId(1L)
                .version(1)
                .org(Organization.builder().organizationId(0L).build())
                .status("status")
                .numTotalRows(3L)
                .numCorrectlyImportedRows(2L)
                .creationDate(Instant.now())
                .lastUpdateDate(Instant.now())
                .flagActive(true)
                .operatorExternalUserId("operatorExternalId")
                .flagSpontaneous(Boolean.TRUE)
                .filePathName("filePathName")
                .fileName("fileName.csv")
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
