package it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice;

import it.gov.pagopa.payhub.activities.dto.ErrorFileDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class OrgSilServiceErrorDTO extends ErrorFileDTO {

    private String ipaCode;
    private String applicationName;
    private Long rowNumber;

    public OrgSilServiceErrorDTO(String fileName, String ipaCode, Long rowNumber, String errorCode, String errorMessage) {
        super(fileName, errorCode, errorMessage);
        this.ipaCode = ipaCode;
        this.rowNumber = rowNumber;
    }

    @Override
    public String[] toCsvRow() {
        return new String[]{
                getFileName(), ipaCode,
                applicationName,
                rowNumber.toString(),
                getErrorCode(), getErrorMessage()
        };
    }
}
