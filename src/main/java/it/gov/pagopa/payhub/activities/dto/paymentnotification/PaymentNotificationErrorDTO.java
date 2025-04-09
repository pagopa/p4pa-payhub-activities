package it.gov.pagopa.payhub.activities.dto.paymentnotification;

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
public class PaymentNotificationErrorDTO extends ErrorFileDTO {

    private String iuv;
    private String iud;
    private String workflowStatus;
    private Long rowNumber;

    public PaymentNotificationErrorDTO(String fileName, String iuv, String iud, String workflowStatus, Long rowNumber, String errorCode, String errorMessage) {
        super(fileName, errorCode, errorMessage);
        this.iuv = iuv;
        this.iud = iud;
        this.workflowStatus = workflowStatus;
        this.rowNumber = rowNumber;
    }

    @Override
    public String[] toCsvRow() {
        return new String[]{
                getFileName(), iuv, iud, workflowStatus,
                rowNumber.toString(),
                getErrorCode(), getErrorMessage()
        };
    }
}
