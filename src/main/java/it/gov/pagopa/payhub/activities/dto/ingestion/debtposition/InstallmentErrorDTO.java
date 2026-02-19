package it.gov.pagopa.payhub.activities.dto.ingestion.debtposition;

import it.gov.pagopa.payhub.activities.dto.ErrorFileDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class InstallmentErrorDTO extends ErrorFileDTO {

    private String[] csvRow;
    private String errorCode;
    private String errorMessage;

    @Override
    public String[] toCsvRow() {
        String[] result = Arrays.copyOf(csvRow, csvRow.length + 2);
        result[result.length - 2] = errorCode;
        result[result.length - 1] = errorMessage;
        return result;
    }

}
