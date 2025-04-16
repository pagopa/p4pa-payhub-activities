package it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification;

import it.gov.pagopa.payhub.activities.dto.ErrorFileDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class PaymentNotificationErrorDTO extends ErrorFileDTO {

    private String iuv;
    private String iud;
    private Long rowNumber;

    public PaymentNotificationErrorDTO(String fileName, String iuv, String iud, Long rowNumber, String errorCode, String errorMessage) {
        super(fileName, errorCode, errorMessage);
        this.iuv = iuv;
        this.iud = iud;
        this.rowNumber = rowNumber;
    }

    @Override
    public String[] toCsvRow() {
        return new String[]{
                getFileName(), iuv, iud,
                rowNumber.toString(),
                getErrorCode(), getErrorMessage()
        };
    }
}
