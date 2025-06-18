package it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry;

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
public class AssessmentsRegistryErrorDTO extends ErrorFileDTO {

    private Long rowNumber;
    private String assessmentCode;
    private Long organizationIpaCode;

    public AssessmentsRegistryErrorDTO(String fileName,
                                       Long rowNumber, String assessmentCode, Long organizationIpaCode,
                                       String errorCode, String errorMessage) {
        super(fileName, errorCode, errorMessage);
        this.rowNumber = rowNumber;
        this.assessmentCode = assessmentCode;
        this.organizationIpaCode = organizationIpaCode;
    }

    @Override
    public String[] toCsvRow() {
        return new String[]{
                getFileName(),
                getRowNumber() != null ? String.valueOf(getRowNumber()) : "",
                Objects.requireNonNullElse(getAssessmentCode(), ""),
                getOrganizationIpaCode() != null ? String.valueOf(getOrganizationIpaCode()) : "",
                getErrorCode(), getErrorMessage()
        };
    }
}
