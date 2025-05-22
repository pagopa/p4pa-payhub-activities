package it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype;

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
public class DebtPositionTypeErrorDTO extends ErrorFileDTO {

    private String debtPositionTypeCode;
    private String brokerCf;
    private Long rowNumber;

    public DebtPositionTypeErrorDTO(String fileName, String debtPositionTypeCode, String brokerCf, Long rowNumber, String errorCode, String errorMessage) {
        super(fileName, errorCode, errorMessage);
        this.debtPositionTypeCode = debtPositionTypeCode;
        this.brokerCf = brokerCf;
        this.rowNumber = rowNumber;
    }

    @Override
    public String[] toCsvRow() {
        return new String[]{
                getFileName(), debtPositionTypeCode,
                brokerCf,rowNumber.toString(),
                getErrorCode(), getErrorMessage()
        };
    }
}
