package it.gov.pagopa.payhub.activities.dto.export;

import it.gov.pagopa.payhub.activities.dto.ErrorFileDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
public class ExportFileErrorFileDTO extends ErrorFileDTO {

    @Override
    public String[] toCsvRow() {
        return new String[]{
                getFileName(), getErrorCode(), getErrorMessage()
        };
    }
}
