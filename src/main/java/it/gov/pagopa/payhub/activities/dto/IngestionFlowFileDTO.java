package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngestionFlowFileDTO implements Serializable {

    private Long flowId;
    private int version;
    private OrganizationDTO orgId;
    private String status;
    private String iuf;
    private Long numTotalRows;
    private Long numCorrectlyImportedRows;
    private Date creationDate;
    private Date lastChangeDate;
    private boolean flagActive;
    private String operatorName;
    private Boolean flagSpontaneous;
    private String filePathName;
    private String fileName;
    private Long pdfGenerated;
    private String codRequestToken;
    private String codError;
}
