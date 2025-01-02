package it.gov.pagopa.payhub.activities.dto;

import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class IngestionFlowFileDTO implements Serializable {

    private Long ingestionFlowFileId;
    private IngestionFlowFileType flowFileType;
    private int version;
    private OrganizationDTO org;
    private String status;
    private Long numTotalRows;
    private Long numCorrectlyImportedRows;
    private Instant creationDate;
    private Instant lastUpdateDate;
    private boolean flagActive;
    private String operatorExternalUserId;
    private Boolean flagSpontaneous;
    private String filePathName;
    private String fileName;
    private Long pdfGenerated;
    private String codRequestToken;
    private String codError;
    private String pspIdentifier;
    private LocalDateTime flowDateTime;
    private Long fileSize;
    private String fileSourceCode;
    private String discardFileName;

}
