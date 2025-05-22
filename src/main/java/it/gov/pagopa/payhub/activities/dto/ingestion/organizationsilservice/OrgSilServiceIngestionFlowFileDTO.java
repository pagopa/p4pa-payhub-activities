package it.gov.pagopa.payhub.activities.dto.ingestion.organizationsilservice;

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

    @CsvBindByName(column = "ipa_code")
    private String ipaCode;

    @CsvBindByName(column = "application_name")
    private String applicationName;

    @CsvBindByName(column = "service_url")
    private String serviceUrl;

    @CsvBindByName(column = "service_type")
    private String serviceType;

    @CsvBindByName(column = "flag_legacy")
    private Boolean flagLegacy;

}

