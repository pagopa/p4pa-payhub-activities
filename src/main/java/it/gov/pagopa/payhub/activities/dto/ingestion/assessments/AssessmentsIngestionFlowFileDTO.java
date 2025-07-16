package it.gov.pagopa.payhub.activities.dto.ingestion.assessments;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentsIngestionFlowFileDTO {

    @CsvBindByName(column = "nomeAccertamento", required = true)
    private String assessmentName;

    @CsvBindByName(column = "enteIpaCode")
    private String organizationIpaCode;

    @CsvBindByName(column = "codiceEnteTipoDovuto", required = true)
    private String debtPositionTypeOrgCode;

    @CsvBindByName(column = "iuv", required = true)
    private String iuv;

    @CsvBindByName(column = "iud", required = true)
    private String iud;

    @CsvBindByName(column = "codUfficio")
    private String officeCode;

    @CsvBindByName(column = "codCapitolo", required = true)
    private String sectionCode;

    @CsvBindByName(column = "codiceAccertamento")
    private String assessmentCode;

    @CsvBindByName(column = "importoCentesimi", required = true)
    private Long amountCents;

    @CsvBindByName(column = "importoVersato", required = true)
    private Boolean amountSubmitted;



}

