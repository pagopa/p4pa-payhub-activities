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

    public DebtPositionTypeOrgErrorDTO(String fileName, String debtPositionTypeCode, Long organizationId, Long rowNumber, String errorCode, String errorMessage) {
        super(fileName, rowNumber, errorCode, errorMessage);
        this.debtPositionTypeCode = debtPositionTypeCode;
        this.organizationId = organizationId;
    }

    @Override
    public String[] toCsvRow() {
        return new String[]{
                getFileName(), debtPositionTypeCode,
                organizationId.toString(),getRowNumber().toString(),
                getErrorCode(), getErrorMessage()
        };
    }
}
