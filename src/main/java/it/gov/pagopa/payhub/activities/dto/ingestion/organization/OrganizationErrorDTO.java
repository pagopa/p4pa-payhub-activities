package it.gov.pagopa.payhub.activities.dto.ingestion.organization;

import it.gov.pagopa.payhub.activities.dto.ErrorFileDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class OrganizationErrorDTO extends ErrorFileDTO {

    private String ipaCode;
    private Long rowNumber;

    public OrganizationErrorDTO(String fileName, String ipaCode, Long rowNumber, String errorCode, String errorMessage) {
        super(fileName, errorCode, errorMessage);
        this.ipaCode = ipaCode;
        this.rowNumber = rowNumber;
    }

    @Override
    public String[] toCsvRow() {
        return new String[]{
                getFileName(), ipaCode,
                rowNumber.toString(),
                getErrorCode(), getErrorMessage()
        };
    }
}
