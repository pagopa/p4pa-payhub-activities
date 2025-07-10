package it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgSilServiceIngestionFlowFileDTO {

    @CsvBindByName(column = "codIpaEnte")
    private String ipaCode;

    @CsvBindByName(column = "nomeApplicazione")
    private String applicationName;

    @CsvBindByName(column = "urlServizio")
    private String serviceUrl;

    @CsvBindByName(column = "tipoServizio")
    private String serviceType;

    @CsvBindByName(column = "flagLegacy")
    private Boolean flagLegacy;

    @CsvBindByName(column = "legacyJwtKid")
    private String legacyJwtKid;

    @CsvBindByName(column = "legacyJwtSubject")
    private String legacyJwtSubject;

    @CsvBindByName(column = "legacyJwtIssuer")
    private String legacyJwtIssuer;

    @CsvBindByName(column = "legacyJwtAlgorithm")
    private String legacyJwtAlgorithm;

    @CsvBindByName(column = "legacyJwtSigningKey")
    private String legacyJwtSigningKey;

    @CsvBindByName(column = "legacyBasicAuthUrl")
    private String legacyBasicAuthUrl;

    @CsvBindByName(column = "legacyBasicUser")
    private String legacyBasicUser;

    @CsvBindByName(column = "legacyBasicPsw")
    private String legacyBasicPsw;
}
