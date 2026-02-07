package it.gov.pagopa.payhub.activities.dto.ingestion.receipt;

import it.gov.pagopa.payhub.activities.dto.ErrorFileDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
public class ReceiptErrorDTO extends ErrorFileDTO {

    public ReceiptErrorDTO(String fileName, Long rowNumber, String errorCode, String errorMessage) {
        super(fileName, rowNumber, errorCode, errorMessage);
    }

    @Override
    public String[] toCsvRow() {
        return new String[]{
                getFileName(),
                getRowNumber().toString(),
                getErrorCode(), getErrorMessage()
        };
    }
}
