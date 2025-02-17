package it.gov.pagopa.payhub.activities.dto.debtposition;

import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileErroDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class InstallmentErrorDTO extends IngestionFlowFileErroDTO implements Serializable {

    private String iupdOrg;
    private String iud;
    private String workflowStatus;
    private Long rowNumber;

    public InstallmentErrorDTO(String fileName, String iupdOrg, String iud, String workflowStatus, Long rowNumber, String errorCode, String errorMessage) {
        super(fileName, errorCode, errorMessage);
        this.iupdOrg = iupdOrg;
        this.iud = iud;
        this.workflowStatus = workflowStatus;
        this.rowNumber = rowNumber;
    }

    @Override
    public String[] toCsvRow() {
        return new String[]{
                getFileName(), iupdOrg, iud, workflowStatus,
                rowNumber.toString(),
                getErrorCode(), getErrorMessage()
        };
    }
}
