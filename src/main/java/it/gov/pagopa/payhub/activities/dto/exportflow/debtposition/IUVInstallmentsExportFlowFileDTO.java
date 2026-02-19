package it.gov.pagopa.payhub.activities.dto.exportflow.debtposition;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvIgnore;
import it.gov.pagopa.pu.debtposition.dto.generated.Action;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonEntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import static it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileVersions.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IUVInstallmentsExportFlowFileDTO {

    @CsvBindByName(column = "IUD")
    private String iud;

    @CsvBindByName(column = "codIuv")
    private String iuv;

    @CsvBindByName(column = "tipoIdentificativoUnivoco")
    private PersonEntityType entityType;

    @CsvBindByName(column = "codiceIdentificativoUnivoco")
    private String fiscalCode;

    @CsvBindByName(column = "anagraficaPagatore")
    private String fullName;

    @CsvBindByName(column = "indirizzoPagatore")
    private String address;

    @CsvBindByName(column = "civicoPagatore")
    private String civic;

    @CsvBindByName(column = "capPagatore")
    private String postalCode;

    @CsvBindByName(column = "localitaPagatore")
    private String location;

    @CsvBindByName(column = "provinciaPagatore")
    private String province;

    @CsvBindByName(column = "nazionePagatore")
    private String nation;

    @CsvBindByName(column = "mailPagatore")
    private String email;

    @CsvBindByName(column = "dataEsecuzionePagamento")
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate dueDate;

    @CsvBindByName(column = "importoDovuto")
    private BigDecimal amount;

    @CsvBindByName(column = "commissioneCaricoPa")
    private BigDecimal paCommission;

    @CsvBindByName(column = "tipoDovuto") 
    private String debtPositionTypeCode;

    @CsvBindByName(column = "tipoVersamento")
    private String paymentType;

    @CsvBindByName(column = "causaleVersamento")
    private String remittanceInformation;

    @CsvBindByName(column = "datiSpecificiRiscossione")
    private String legacyPaymentMetadata;

    @CsvIgnore(profiles = {V1_0, V1_1})
    @CsvBindByName(column = "bilancio")
    private String balance;

    @CsvIgnore(profiles = {V1_0, V1_1, V1_2})
    @CsvBindByName(column = "flgGeneraIuv")
    private Boolean generateNotice;

    @CsvIgnore(profiles = {V1_0, V1_1, V1_2, V1_3})
    @CsvBindByName(column = "flagMultiBeneficiario")
    private Boolean flagMultiBeneficiary;

    @CsvIgnore(profiles = {V1_0, V1_1, V1_2, V1_3})
    @CsvBindByName(column = "codiceFiscaleEnteSecondario")
    private String orgFiscalCode2;

    @CsvIgnore(profiles = {V1_0, V1_1, V1_2, V1_3})
    @CsvBindByName(column = "denominazioneEnteSecondario")
    private String orgName2;

    @CsvIgnore(profiles = {V1_0, V1_1, V1_2, V1_3})
    @CsvBindByName(column = "ibanAccreditoEnteSecondario")
    private String orgIban2;

    @CsvIgnore(profiles = {V1_0, V1_1, V1_2, V1_3})
    @CsvBindByName(column = "indirizzoEnteSecondario")
    private String orgAddress2;

    @CsvIgnore(profiles = {V1_0, V1_1, V1_2, V1_3})
    @CsvBindByName(column = "civicoEnteSecondario")
    private String orgCivic2;

    @CsvIgnore(profiles = {V1_0, V1_1, V1_2, V1_3})
    @CsvBindByName(column = "capEnteSecondario")
    private String orgCap2;

    @CsvIgnore(profiles = {V1_0, V1_1, V1_2, V1_3})
    @CsvBindByName(column = "localitaEnteSecondario")
    private String orgLocation2;

    @CsvIgnore(profiles = {V1_0, V1_1, V1_2, V1_3})
    @CsvBindByName(column = "provinciaEnteSecondario")
    private String orgProvince2;

    @CsvIgnore(profiles = {V1_0, V1_1, V1_2, V1_3})
    @CsvBindByName(column = "nazioneEnteSecondario")
    private String orgNation2;

    @CsvIgnore(profiles = {V1_0, V1_1, V1_2, V1_3})
    @CsvBindByName(column = "datiSpecificiRiscossioneEnteSecondario")
    private String orgLegacyPaymentMetadata2;

    @CsvIgnore(profiles = {V1_0, V1_1, V1_2, V1_3})
    @CsvBindByName(column = "causaleVersamentoEnteSecondario")
    private String orgRemittanceInformation2;

    @CsvIgnore(profiles = {V1_0, V1_1, V1_2, V1_3})
    @CsvBindByName(column = "importoVersamentoEnteSecondario")
    private BigDecimal orgAmount2;

    @CsvBindByName(column = "azione")
    private Action action;
}
