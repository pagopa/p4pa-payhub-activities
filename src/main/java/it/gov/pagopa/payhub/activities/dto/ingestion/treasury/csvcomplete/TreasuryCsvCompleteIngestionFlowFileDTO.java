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
    @CsvBindByName(column = "annoBolletta", required = true)
    private String billYear;

    @Size(max=7)
    @CsvBindByName(column = "codBolletta", required = true)
    private String billCode;

    @Size(max=50)
    @CsvBindByName(column = "enteIpaCode", required = true)
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
    @CsvBindByName(column = "codConto")
    private String accountCode;

    @NotNull
    @Size(max=7)
    @CsvBindByName(column = "codIdDominio")
    private String domainIdCode;

    @NotNull
    @Size(max=3)
    @CsvBindByName(column = "codTipoMovimento")
    private String transactionTypeCode;

    @NotNull
    @Size(max=3)
    @CsvBindByName(column = "codCausale")
    private String remittanceCode;

    @NotNull
    @Size(max=255)
    @CsvBindByName(column = "causale")
    private String remittanceDescription;

    @CsvBindByName(column = "importoCentesimi", required = true)
    private Long billAmountCents;

    @Size(max=10)
    @CsvBindByName(column = "dataBolletta", required = true)
    private String billDate;

    @NotNull
    @Size(max=15)
    @CsvBindByName(column = "dataRicezione")
    private String receptionDate;

    @NotNull
    @Size(max=4)
    @CsvBindByName(column = "annoDocumento")
    private String documentYear;

    @NotNull
    @Size(max=7)
    @CsvBindByName(column = "codDocumento")
    private String documentCode;

    @NotNull
    @Size(max=6)
    @CsvBindByName(column = "codBollo")
    private String sealCode;

    @Size(max=255)
    @CsvBindByName(column = "pspCognome", required = true)
    private String pspLastName;

    @NotNull
    @Size(max=30)
    @CsvBindByName(column = "pspNome")
    private String pspFirstName;

    @NotNull
    @Size(max=50)
    @CsvBindByName(column = "pspIndirizzo")
    private String pspAddress;

    @NotNull
    @Size(max=5)
    @CsvBindByName(column = "pspCodicePostale")
    private String pspPostalCode;

    @NotNull
    @Size(max=40)
    @CsvBindByName(column = "pspCitta")
    private String pspCity;

    @NotNull
    @Size(max=16)
    @CsvBindByName(column = "pspCf")
    private String pspFiscalCode;

    @NotNull
    @Size(max=12)
    @CsvBindByName(column = "pspPiva")
    private String pspVatNumber;

    @NotNull
    @Size(max=5)
    @CsvBindByName(column = "codAbi")
    private String abiCode;

    @NotNull
    @Size(max=5)
    @CsvBindByName(column = "codCab")
    private String cabCode;

    @NotNull
    @Size(max=34)
    @CsvBindByName(column = "codIban")
    private String ibanCode;

    @NotNull
    @Size(max=50)
    @CsvBindByName(column = "codContoAnagrafica")
    private String accountRegistryCode;

    @NotNull
    @Size(max=4)
    @CsvBindByName(column = "aeProvvisorio")
    private String provisionalAe;

    @NotNull
    @Size(max=6)
    @CsvBindByName(column = "codProvvisorio")
    private String provisionalCode;

    @NotNull
    @Size(max=1)
    @CsvBindByName(column = "codTipoConto")
    private String accountTypeCode;

    @NotNull
    @Size(max=10)
    @CsvBindByName(column = "codProcesso")
    private String processCode;

    @NotNull
    @Size(max=4)
    @CsvBindByName(column = "codPgEsecuzione")
    private String executionPgCode;

    @NotNull
    @Size(max=4)
    @CsvBindByName(column = "codPgTrasferimento")
    private String transferPgCode;

    @NotNull
    @CsvBindByName(column = "numPgProcesso")
    private Long processPgNumber;

    @NotNull
    @Size(max=255)
    @CsvBindByName(column = "dataValutaRegione")
    private String regionValueDate;

    @Size(max=3)
    @CsvBindByName(column = "flgRegolarizzata", required = true)
    private String isRegularized;

    @NotNull
    @Size(max=255)
    @CsvBindByName(column = "dataEffettivaSospeso")
    private String actualSuspensionDate;

    @NotNull
    @Size(max=10)
    @CsvBindByName(column = "codGestionaleProvvisorio")
    private String managementProvisionalCode;

    @NotNull
    @Size(max=50)
    @CsvBindByName(column = "endToEndId")
    private String endToEndCode;
}
