package it.gov.pagopa.payhub.activities.dto.ingestion.assessments;

import it.gov.pagopa.payhub.activities.dto.ErrorFileDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class AssessmentsErrorDTO extends ErrorFileDTO {

    private String assessmentCode;
    private String organizationIpaCode;

    public AssessmentsErrorDTO(String fileName,
                               Long rowNumber, String assessmentCode, String organizationIpaCode,
                               String errorCode, String errorMessage) {
        super(fileName, rowNumber, errorCode, errorMessage);
        this.assessmentCode = assessmentCode;
        this.organizationIpaCode = organizationIpaCode;
    }

    @Override
    public String[] toCsvRow() {
        return new String[]{
                getFileName(),
                getRowNumber() != null ? String.valueOf(getRowNumber()) : "",
                Objects.requireNonNullElse(getAssessmentCode(), ""),
                Objects.requireNonNullElse(getOrganizationIpaCode(), ""),
                getErrorCode(), getErrorMessage()
        };
    }
}
