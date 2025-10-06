package it.gov.pagopa.payhub.activities.dto.ingestion.treasury.xls;

import it.gov.pagopa.payhub.activities.dto.ErrorFileDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class TreasuryXlsErrorDTO extends ErrorFileDTO {

    private String iuf;
    private Long rowNumber;

    public TreasuryXlsErrorDTO(String fileName, String iuf, Long rowNumber, String errorCode, String errorMessage) {
        super(fileName, errorCode, errorMessage);
        this.iuf = iuf;
        this.rowNumber = rowNumber;
    }

    @Override
    public String[] toCsvRow() {
        return new String[]{
                getFileName(), iuf,
                rowNumber.toString(),
                getErrorCode(), getErrorMessage()
        };
    }
}
