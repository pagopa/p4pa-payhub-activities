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

    public OrgSilServiceErrorDTO(String fileName, String ipaCode, String applicationName, Long rowNumber, String errorCode, String errorMessage) {
        super(fileName, rowNumber, errorCode, errorMessage);
        this.ipaCode = ipaCode;
        this.applicationName = applicationName;
    }

    @Override
    public String[] toCsvRow() {
        return new String[]{
                getFileName(), ipaCode,
                applicationName,
                getRowNumber().toString(),
                getErrorCode(), getErrorMessage()
        };
    }
}
