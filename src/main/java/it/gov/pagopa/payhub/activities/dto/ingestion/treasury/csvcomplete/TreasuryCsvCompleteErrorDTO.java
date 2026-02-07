package it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete;

import it.gov.pagopa.payhub.activities.dto.ErrorFileDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class TreasuryCsvCompleteErrorDTO extends ErrorFileDTO {

    private String iuv;
    private String iuf;

    public TreasuryCsvCompleteErrorDTO(String fileName, String iuv, String iuf, Long rowNumber, String errorCode, String errorMessage) {
        super(fileName, rowNumber, errorCode, errorMessage);
        this.iuv = iuv;
        this.iuf = iuf;
    }

    @Override
    public String[] toCsvRow() {
        return new String[]{
                getFileName(), iuv, iuf,
                getRowNumber().toString(),
                getErrorCode(), getErrorMessage()
        };
    }
}
