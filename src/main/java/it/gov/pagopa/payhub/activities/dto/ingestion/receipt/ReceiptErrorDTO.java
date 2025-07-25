package it.gov.pagopa.payhub.activities.dto.ingestion.receipt;

import it.gov.pagopa.payhub.activities.dto.ErrorFileDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class ReceiptErrorDTO extends ErrorFileDTO {

    private Long rowNumber;

    public ReceiptErrorDTO(String fileName, Long rowNumber, String errorCode, String errorMessage) {
        super(fileName, errorCode, errorMessage);
        this.rowNumber = rowNumber;
    }

    @Override
    public String[] toCsvRow() {
        return new String[]{
                getFileName(),
                rowNumber.toString(),
                getErrorCode(), getErrorMessage()
        };
    }
}
