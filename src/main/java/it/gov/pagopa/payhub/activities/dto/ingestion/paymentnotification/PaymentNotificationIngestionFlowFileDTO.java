package it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentNotificationIngestionFlowFileDTO {

    @CsvBindByName(column = "IUD", required = true)
    @CsvBindByName(column = "iud", required = true, profiles = "eng")
    private String iud;

    @CsvBindByName(column = "codIuv", required = true)
    @CsvBindByName(column = "iuv", required = true, profiles = "eng")
    private String iuv;

    @CsvBindByName(column = "tipoIdentificativoUnivoco", required = true)
    @CsvBindByName(column = "debtorUniqueIdentifierType", required = true, profiles = "eng")
    private String debtorUniqueIdentifierType;

    @CsvBindByName(column = "codiceIdentificativoUnivoco", required = true)
    @CsvBindByName(column = "debtorUniqueIdentifierCode", required = true, profiles = "eng")
    private String debtorUniqueIdentifierCode;

    @CsvBindByName(column = "anagraficaPagatore", required = true)
    @CsvBindByName(column = "debtorFullName", required = true, profiles = "eng")
    private String debtorFullName;

    @CsvBindByName(column = "indirizzoPagatore")
    @CsvBindByName(column = "debtorAddress", profiles = "eng")
    private String debtorAddress;

    @CsvBindByName(column = "civicoPagatore")
    @CsvBindByName(column = "debtorCivic", profiles = "eng")
    private String debtorCivic;

    @CsvBindByName(column = "capPagatore")
    @CsvBindByName(column = "debtorPostalCode", profiles = "eng")
    private String debtorPostalCode;

    @CsvBindByName(column = "localitaPagatore")
    @CsvBindByName(column = "debtorLocation", profiles = "eng")
    private String debtorLocation;

    @CsvBindByName(column = "provinciaPagatore")
    @CsvBindByName(column = "debtorProvince", profiles = "eng")
    private String debtorProvince;

    @CsvBindByName(column = "nazionePagatore")
    @CsvBindByName(column = "debtorNation", profiles = "eng")
    private String debtorNation;

    @CsvBindByName(column = "e-mailPagatore")
    @CsvBindByName(column = "debtorEmail", profiles = "eng")
    private String debtorEmail;

    @CsvBindByName(column = "dataEsecuzionePagamento", required = true)
    @CsvBindByName(column = "paymentExecutionDate", required = true, profiles = "eng")
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate paymentExecutionDate;

    @CsvBindByName(column = "importoDovutoPagato", required = true)
    @CsvBindByName(column = "amountPaidCents", required = true, profiles = "eng")
    private BigDecimal amountPaidCents;

    @CsvBindByName(column = "commissioneCaricoPa", required = true)
    @CsvBindByName(column = "paCommissionCents", required = true, profiles = "eng")
    private BigDecimal paCommissionCents;

    @CsvBindByName(column = "tipoDovuto", required = true)
    @CsvBindByName(column = "debtPositionTypeOrgCode", required = true, profiles = "eng")
    private String debtPositionTypeOrgCode;

    @CsvBindByName(column = "tipoVersamento", required = true)
    @CsvBindByName(column = "paymentType", required = true, profiles = "eng")
    private String paymentType;

    @CsvBindByName(column = "causaleVersamento", required = true)
    @CsvBindByName(column = "remittanceInformation", required = true, profiles = "eng")
    private String remittanceInformation;

    @CsvBindByName(column = "datiSpecificiRiscossione ", required = true)
    @CsvBindByName(column = "transferCategory", required = true, profiles = "eng")
    private String transferCategory;

    @CsvBindByName(column = "bilancio")
    @CsvBindByName(column = "balance", profiles = "eng")
    private String balance;
}