package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class IngestionFlowFileDTO implements Serializable {

    private Long ingestionFlowFileId;
    private String flowFileType;
    private String mappedExternalUserId;
    private int version;
    private OrganizationDTO org;
    private String status;
    private String iuf;
    private Long numTotalRows;
    private Long numCorrectlyImportedRows;
    private Date creationDate;
    private Date lastUpdateDate;
    private boolean flagActive;
    private String operatorName;
    private Boolean flagSpontaneous;
    private String filePath;
    private String fileName;
    private Long pdfGenerated;
    private String codRequestToken;
    private String codError;
    private String discardedFileName;
}
