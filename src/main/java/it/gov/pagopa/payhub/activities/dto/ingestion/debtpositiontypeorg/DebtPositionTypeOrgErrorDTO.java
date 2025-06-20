package it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg;

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
public class DebtPositionTypeOrgErrorDTO extends ErrorFileDTO {

    private String debtPositionTypeCode;
    private Long organizationId;
    private Long rowNumber;

    public DebtPositionTypeOrgErrorDTO(String fileName, String debtPositionTypeCode, Long organizationId, Long rowNumber, String errorCode, String errorMessage) {
        super(fileName, errorCode, errorMessage);
        this.debtPositionTypeCode = debtPositionTypeCode;
        this.organizationId = organizationId;
        this.rowNumber = rowNumber;
    }

    @Override
    public String[] toCsvRow() {
        return new String[]{
                getFileName(), debtPositionTypeCode,
                organizationId.toString(),rowNumber.toString(),
                getErrorCode(), getErrorMessage()
        };
    }
}
