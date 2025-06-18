package it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    @CsvBindByName(column = "codiceTipoDovuto")
    private String debtPositionTypeOrgCode;

    @NotNull
    @CsvBindByName(column = "codCapitolo")
    private String sectionCode;

    @CsvBindByName(column = "descrizioneCapitolo")
    private String sectionDescription;

    @CsvBindByName(column = "codUfficio")
    private String officeCode;

    @CsvBindByName(column = "descrizioneUfficio")
    private String officeDescription;

    @CsvBindByName(column = "codAccertamento")
    private String assessmentCode;

    @CsvBindByName(column = "descrizioneAccertamento")
    private String assessmentDescription;

    @NotNull
    @CsvBindByName(column = "annoEsercizio")
    private String operatingYear;

    @NotNull
    @CsvBindByName(column = "flgAttivo")
    private String status;

}

