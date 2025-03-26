package it.gov.pagopa.payhub.activities.dto.ingestion.debtposition;

import it.gov.pagopa.payhub.activities.dto.ErrorFileDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class InstallmentErrorFileDTO extends ErrorFileDTO {

    private String iupdOrg;
    private String iud;
    private String workflowStatus;
    private Long rowNumber;

    public InstallmentErrorFileDTO(String fileName, String iupdOrg, String iud, String workflowStatus, Long rowNumber, String errorCode, String errorMessage) {
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
