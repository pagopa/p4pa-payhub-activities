package it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry;

import com.opencsv.bean.CsvBindByName;
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
    @CsvBindByName(column = "codiceTipoDovuto", required = true)
    private String debtPositionTypeOrgCode;

    @Size(max=64)
    @CsvBindByName(column = "codCapitolo", required = true)
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
    @CsvBindByName(column = "annoEsercizio", required = true)
    private String operatingYear;

    @Size(max=50)
    @CsvBindByName(column = "flgAttivo", required = true)
    private String status;

}

