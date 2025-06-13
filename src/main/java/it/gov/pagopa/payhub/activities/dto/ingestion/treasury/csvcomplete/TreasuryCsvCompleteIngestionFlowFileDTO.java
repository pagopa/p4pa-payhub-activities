package it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreasuryCsvCompleteIngestionFlowFileDTO {

    @Pattern(regexp = "^\\d{4}$", message = "The year must be 4 digits long")
    @CsvBindByName(column = "bill_year", required = true)
    private String billYear;

    @Size(max=7)
    @CsvBindByName(column = "bill_code", required = true)
    private String billCode;

    @CsvBindByName(column = "ingestion_flow_file_code", required = true)
    private Long ingestionFlowFileCode;

    @Size(max=50)
    @CsvBindByName(column = "organization_ipa_code", required = true)
    private String organizationIpaCode;

    @NotNull
    @Size(max=35)
    @CsvBindByName(column = "iuf")
    private String iuf;

    @NotNull
    @Size(max=35)
    @CsvBindByName(column = "iuv")
    private String iuv;

    @NotNull
    @Size(max=7)
    @CsvBindByName(column = "account_code")
    private String accountCode;

    @NotNull
    @Size(max=7)
    @CsvBindByName(column = "domain_id_code")
    private String domainIdCode;

    @NotNull
    @Size(max=3)
    @CsvBindByName(column = "transaction_type_code")
    private String transactionTypeCode;

    @NotNull
    @Size(max=3)
    @CsvBindByName(column = "remittance_code")
    private String remittanceCode;

    @NotNull
    @Size(max=255)
    @CsvBindByName(column = "remittance_description")
    private String remittanceDescription;

    @CsvBindByName(column = "bill_amount_cents", required = true)
    private Long billAmountCents;

    @Size(max=10)
    @CsvBindByName(column = "bill_date", required = true)
    private String billDate;

    @NotNull
    @Size(max=15)
    @CsvBindByName(column = "reception_date")
    private String receptionDate;

    @NotNull
    @Size(max=4)
    @CsvBindByName(column = "document_year")
    private String documentYear;

    @NotNull
    @Size(max=7)
    @CsvBindByName(column = "document_code")
    private String documentCode;

    @NotNull
    @Size(max=6)
    @CsvBindByName(column = "seal_code")
    private String sealCode;

    @Size(max=255)
    @CsvBindByName(column = "psp_last_name", required = true)
    private String pspLastName;

    @NotNull
    @Size(max=30)
    @CsvBindByName(column = "psp_first_name")
    private String pspFirstName;

    @NotNull
    @Size(max=50)
    @CsvBindByName(column = "psp_address")
    private String pspAddress;

    @NotNull
    @Size(max=5)
    @CsvBindByName(column = "psp_postal_code")
    private String pspPostalCode;

    @NotNull
    @Size(max=40)
    @CsvBindByName(column = "psp_city")
    private String pspCity;

    @NotNull
    @Size(max=16)
    @CsvBindByName(column = "psp_fiscal_code")
    private String pspFiscalCode;

    @NotNull
    @Size(max=12)
    @CsvBindByName(column = "psp_vat_number")
    private String pspVatNumber;

    @NotNull
    @Size(max=5)
    @CsvBindByName(column = "abi_code")
    private String abiCode;

    @NotNull
    @Size(max=5)
    @CsvBindByName(column = "cab_code")
    private String cabCode;

    @NotNull
    @Size(max=34)
    @CsvBindByName(column = "iban_code")
    private String ibanCode;

    @NotNull
    @Size(max=50)
    @CsvBindByName(column = "account_registry_code")
    private String accountRegistryCode;

    @NotNull
    @Size(max=4)
    @CsvBindByName(column = "provisional_ae")
    private String provisionalAe;

    @NotNull
    @Size(max=6)
    @CsvBindByName(column = "provisional_code")
    private String provisionalCode;

    @NotNull
    @Size(max=1)
    @CsvBindByName(column = "account_type_code")
    private String accountTypeCode;

    @NotNull
    @Size(max=10)
    @CsvBindByName(column = "process_code")
    private String processCode;

    @NotNull
    @Size(max=4)
    @CsvBindByName(column = "execution_pg_code")
    private String executionPgCode;

    @NotNull
    @Size(max=4)
    @CsvBindByName(column = "transfer_pg_code")
    private String transferPgCode;

    @NotNull
    @CsvBindByName(column = "process_pg_number")
    private Long processPgNumber;

    @NotNull
    @Size(max=255)
    @CsvBindByName(column = "region_value_date")
    private String regionValueDate;

    @Size(max=3)
    @CsvBindByName(column = "is_regularized", required = true)
    private String isRegularized;

    @NotNull
    @Size(max=255)
    @CsvBindByName(column = "actual_suspension_date")
    private String actualSuspensionDate;

    @NotNull
    @Size(max=10)
    @CsvBindByName(column = "management_provisional_code")
    private String managementProvisionalCode;

    @NotNull
    @Size(max=50)
    @CsvBindByName(column = "end_to_end_code")
    private String endToEndCode;
}
