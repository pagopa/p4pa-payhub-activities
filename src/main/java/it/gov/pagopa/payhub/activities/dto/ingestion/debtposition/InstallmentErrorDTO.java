package it.gov.pagopa.payhub.activities.dto.ingestion.debtposition;

import it.gov.pagopa.payhub.activities.dto.ErrorFileDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class InstallmentErrorDTO extends ErrorFileDTO {

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
