package it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry;

import com.opencsv.bean.CsvBindByName;
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

    @CsvBindByName(column = "codiceTipoDovuto", required = true)
    private String debtPositionTypeOrgCode;

    @CsvBindByName(column = "codCapitolo", required = true)
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

    @CsvBindByName(column = "annoEsercizio", required = true)
    private String operatingYear;

    @CsvBindByName(column = "flgAttivo", required = true)
    private String status;

}

