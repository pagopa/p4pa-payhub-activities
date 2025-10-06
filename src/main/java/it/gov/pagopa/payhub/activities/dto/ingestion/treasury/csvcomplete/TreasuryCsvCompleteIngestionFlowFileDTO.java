package it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete.TreasuryCsvCompleteIngestionFlowFileVersions.V1_0;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreasuryCsvCompleteIngestionFlowFileDTO {

    @Pattern(regexp = "^\\d{4}$", message = "The year must be 4 digits long")
    @CsvBindByName(column = "annoBolletta", required = true, profiles = {V1_0})
    private String billYear;

    @Size(max=7)
    @CsvBindByName(column = "codBolletta", required = true, profiles = {V1_0})
    private String billCode;

    @Size(max=10)
    @CsvBindByName(column = "codEnteBT", profiles = {V1_0})
    private String orgBtCode;

    @Size(max=10)
    @CsvBindByName(column = "codIstatEnte", profiles = {V1_0})
    private String orgIstatCode;

    @Size(max=50)
    @CsvBindByName(column = "enteIpaCode", required = true, profiles = {V1_0})
    private String organizationIpaCode;

    @NotNull
    @Size(max=35)
    @CsvBindByName(column = "iuf", profiles = {V1_0})
    private String iuf;

    @NotNull
    @Size(max=35)
    @CsvBindByName(column = "iuv", profiles = {V1_0})
    private String iuv;

    @NotNull
    @Size(max=7)
    @CsvBindByName(column = "codConto", profiles = {V1_0})
    private String accountCode;

    @NotNull
    @Size(max=7)
    @CsvBindByName(column = "codIdDominio", profiles = {V1_0})
    private String domainIdCode;

    @NotNull
    @Size(max=3)
    @CsvBindByName(column = "codTipoMovimento", profiles = {V1_0})
    private String transactionTypeCode;

    @NotNull
    @Size(max=3)
    @CsvBindByName(column = "codCausale", profiles = {V1_0})
    private String remittanceCode;

    @NotNull
    @Size(max=255)
    @CsvBindByName(column = "causale", profiles = {V1_0})
    private String remittanceDescription;

    @CsvBindByName(column = "importoCentesimi", required = true, profiles = {V1_0})
    private Long billAmountCents;

    @Size(max=10)
    @CsvBindByName(column = "dataBolletta", required = true, profiles = {V1_0})
    private String billDate;

    @NotNull
    @Size(max=15)
    @CsvBindByName(column = "dataRicezione", profiles = {V1_0})
    private String receptionDate;

    @NotNull
    @Size(max=4)
    @CsvBindByName(column = "annoDocumento", profiles = {V1_0})
    private String documentYear;

    @NotNull
    @Size(max=7)
    @CsvBindByName(column = "codDocumento", profiles = {V1_0})
    private String documentCode;

    @NotNull
    @Size(max=6)
    @CsvBindByName(column = "codBollo", profiles = {V1_0})
    private String sealCode;

    @Size(max=255)
    @CsvBindByName(column = "pspCognome", required = true, profiles = {V1_0})
    private String pspLastName;

    @NotNull
    @Size(max=30)
    @CsvBindByName(column = "pspNome", profiles = {V1_0})
    private String pspFirstName;

    @NotNull
    @Size(max=50)
    @CsvBindByName(column = "pspIndirizzo", profiles = {V1_0})
    private String pspAddress;

    @NotNull
    @Size(max=5)
    @CsvBindByName(column = "pspCodicePostale", profiles = {V1_0})
    private String pspPostalCode;

    @NotNull
    @Size(max=40)
    @CsvBindByName(column = "pspCitta", profiles = {V1_0})
    private String pspCity;

    @NotNull
    @Size(max=16)
    @CsvBindByName(column = "pspCf", profiles = {V1_0})
    private String pspFiscalCode;

    @NotNull
    @Size(max=12)
    @CsvBindByName(column = "pspPiva", profiles = {V1_0})
    private String pspVatNumber;

    @NotNull
    @Size(max=5)
    @CsvBindByName(column = "codAbi", profiles = {V1_0})
    private String abiCode;

    @NotNull
    @Size(max=5)
    @CsvBindByName(column = "codCab", profiles = {V1_0})
    private String cabCode;

    @NotNull
    @Size(max=34)
    @CsvBindByName(column = "codIban", profiles = {V1_0})
    private String ibanCode;

    @NotNull
    @Size(max=50)
    @CsvBindByName(column = "codContoAnagrafica", profiles = {V1_0})
    private String accountRegistryCode;

    @NotNull
    @Size(max=4)
    @CsvBindByName(column = "aeProvvisorio", profiles = {V1_0})
    private String provisionalAe;

    @NotNull
    @Size(max=6)
    @CsvBindByName(column = "codProvvisorio", profiles = {V1_0})
    private String provisionalCode;

    @NotNull
    @Size(max=1)
    @CsvBindByName(column = "codTipoConto", profiles = {V1_0})
    private String accountTypeCode;

    @NotNull
    @Size(max=10)
    @CsvBindByName(column = "codProcesso", profiles = {V1_0})
    private String processCode;

    @NotNull
    @Size(max=4)
    @CsvBindByName(column = "codPgEsecuzione", profiles = {V1_0})
    private String executionPgCode;

    @NotNull
    @Size(max=4)
    @CsvBindByName(column = "codPgTrasferimento", profiles = {V1_0})
    private String transferPgCode;

    @NotNull
    @CsvBindByName(column = "numPgProcesso", profiles = {V1_0})
    private Long processPgNumber;

    @NotNull
    @Size(max=255)
    @CsvBindByName(column = "dataValutaRegione", profiles = {V1_0})
    private String regionValueDate;

    @Size(max=3)
    @CsvBindByName(column = "flgRegolarizzata", required = true, profiles = {V1_0})
    private String isRegularized;

    @NotNull
    @Size(max=255)
    @CsvBindByName(column = "dataEffettivaSospeso", profiles = {V1_0})
    private String actualSuspensionDate;

    @NotNull
    @Size(max=10)
    @CsvBindByName(column = "codGestionaleProvvisorio", profiles = {V1_0})
    private String managementProvisionalCode;

    @NotNull
    @Size(max=50)
    @CsvBindByName(column = "endToEndId", profiles = {V1_0})
    private String endToEndCode;
}
