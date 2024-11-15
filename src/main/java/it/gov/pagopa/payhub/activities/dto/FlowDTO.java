package it.gov.pagopa.payhub.activities.dto;

import it.gov.pagopa.payhub.activities.dto.organization.OrganizationDTO;
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
public class FlowDTO implements Serializable {

    private Long flowId;
    private int version;
    private OrganizationDTO orgId;
    private RegistryStatusDTO statusId;
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
