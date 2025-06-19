package it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentsRegistryIngestionFlowFileDTO {

    @CsvBindByName(column = "enteIpaCode")
    private String organizationIpaCode;

    @Size(max=256)
    @NotNull
    @CsvBindByName(column = "codiceTipoDovuto")
    private String debtPositionTypeOrgCode;

    @Size(max=64)
    @NotNull
    @CsvBindByName(column = "codCapitolo")
    private String sectionCode;

    @Size(max=512)
    @CsvBindByName(column = "descrizioneCapitolo")
    private String sectionDescription;

    @Size(max=64)
    @CsvBindByName(column = "codUfficio")
    private String officeCode;

    @Size(max=512)
    @CsvBindByName(column = "descrizioneUfficio")
    private String officeDescription;

    @Size(max=64)
    @CsvBindByName(column = "codAccertamento")
    private String assessmentCode;

    @Size(max=512)
    @CsvBindByName(column = "descrizioneAccertamento")
    private String assessmentDescription;

    @Size(max=4)
    @NotNull
    @CsvBindByName(column = "annoEsercizio")
    private String operatingYear;

    @Size(max=50)
    @NotNull
    @CsvBindByName(column = "flgAttivo")
    private String status;

}

