package it.gov.pagopa.payhub.activities.dto.ingestion.debtposition;

import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MultiValuedMap;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallmentIngestionFlowFileDTO {

    @CsvBindByName(column = "azione", required = true, profiles = "legacy")
    @CsvBindByName(column = "action", required = true)
    private ActionEnum action;

    @CsvBindByName(column = "draft")
    private Boolean draft;

    @CsvBindByName(column = "IUPD", profiles = "legacy")
    @CsvBindByName(column = "iupdOrg")
    private String iupdOrg;

    @CsvBindByName(column = "descrizione", required = true, profiles = "legacy")
    @CsvBindByName(column = "description", required = true)
    private String description;

    @CsvBindByName(column = "dataValidita", profiles = "legacy")
    @CsvBindByName(column = "validityDate")
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate validityDate;

    @CsvBindByName(column = "multiDebtor")
    private Boolean multiDebtor;

    @CsvBindByName(column = "dataNotifica", profiles = "legacy")
    @CsvBindByName(column = "notificationDate")
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate notificationDate;

    @CsvBindByName(column = "indiceOpzionePagamento", required = true, profiles = "legacy")
    @CsvBindByName(column = "paymentOptionIndex", required = true)
    private Integer paymentOptionIndex;

    @CsvBindByName(column = "tipoOpzionePagamento", required = true, profiles = "legacy")
    @CsvBindByName(column = "paymentOptionType", required = true)
    private String paymentOptionType;

    @CsvBindByName(column = "descrizioneOpzionePagamento", profiles = "legacy")
    @CsvBindByName(column = "paymentOptionDescription")
    private String paymentOptionDescription;

    @CsvBindByName(column = "iud")
    private String iud;

    @CsvBindByName(column = "codIUV", profiles = "legacy")
    @CsvBindByName(column = "iuv")
    private String iuv;

    @CsvBindByName(column = "tipoIdentificativoUnivoco", required = true, profiles = "legacy")
    @CsvBindByName(column = "entityType", required = true)
    private EntityTypeEnum entityType;

    @CsvBindByName(column = "codiceIdentificativoUnivoco", required = true, profiles = "legacy")
    @CsvBindByName(column = "fiscalCode", required = true)
    private String fiscalCode;

    @CsvBindByName(column = "anagraficaPagatore", required = true, profiles = "legacy")
    @CsvBindByName(column = "fullName", required = true)
    private String fullName;

    @CsvBindByName(column = "indirizzoPagatore", profiles = "legacy")
    @CsvBindByName(column = "address")
    private String address;

    @CsvBindByName(column = "civicoPagatore", profiles = "legacy")
    @CsvBindByName(column = "civic")
    private String civic;

    @CsvBindByName(column = "capPagatore", profiles = "legacy")
    @CsvBindByName(column = "postalCode")
    private String postalCode;

    @CsvBindByName(column = "localitaPagatore", profiles = "legacy")
    @CsvBindByName(column = "location")
    private String location;

    @CsvBindByName(column = "provinciaPagatore", profiles = "legacy")
    @CsvBindByName(column = "province")
    private String province;

    @CsvBindByName(column = "nazionePagatore", profiles = "legacy")
    @CsvBindByName(column = "nation")
    private String nation;

    @CsvBindByName(column = "emailPagatore", profiles = "legacy")
    @CsvBindByName(column = "email")
    private String email;

    @CsvBindByName(column = "dataEsecuzionePagamento", profiles = "legacy")
    @CsvBindByName(column = "dueDate")
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate dueDate;

    @CsvBindByName(column = "importoDovuto", required = true, profiles = "legacy")
    @CsvBindByName(column = "amount", required = true)
    private BigDecimal amount;

    @CsvBindByName(column = "tipoDovuto", required = true, profiles = "legacy")
    @CsvBindByName(column = "debtPositionTypeCode", required = true)
    private String debtPositionTypeCode;

    @CsvBindByName(column = "commissioneNotifica", profiles = "legacy")
    @CsvBindByName(column = "notificationFee")
    private BigDecimal notificationFee;

    @CsvBindByName(column = "causaleVersamento", required = true, profiles = "legacy")
    @CsvBindByName(column = "remittanceInformation", required = true)
    private String remittanceInformation;

    @CsvBindByName(column = "datiSpecificiRiscossione", profiles = "legacy")
    @CsvBindByName(column = "legacyPaymentMetadata")
    private String legacyPaymentMetadata;

    @CsvBindByName(column = "flagGeneraIuv", required = true, profiles = "legacy")
    @CsvBindByName(column = "flagPagoPaPayment", required = true)
    private Boolean flagPagoPaPayment;

    @CsvBindByName(column = "bilancio", profiles = "legacy")
    @CsvBindByName(column = "balance")
    private String balance;

    @CsvBindByName(column = "flagMultiBeneficiario", profiles = "legacy")
    @CsvBindByName(column = "flagMultiBeneficiary")
    private Boolean flagMultiBeneficiary;

    @CsvBindByName(column = "numeroBeneficiari", profiles = "legacy")
    @CsvBindByName(column = "numberBeneficiary")
    private Integer numberBeneficiary;

    @CsvBindAndJoinByName(column = ".*_2", elementType = String.class)
    private MultiValuedMap<String, String> transfer2;

    @CsvBindAndJoinByName(column = ".*_3", elementType = String.class)
    private MultiValuedMap<String, String> transfer3;

    @CsvBindAndJoinByName(column = ".*_4", elementType = String.class)
    private MultiValuedMap<String, String> transfer4;

    @CsvBindAndJoinByName(column = ".*_5", elementType = String.class)
    private MultiValuedMap<String, String> transfer5;

    @CsvBindByName(column = "configurazioniEsecuzione", profiles = "legacy")
    @CsvBindByName(column = "executionConfig")
    private String executionConfig;

    public enum ActionEnum {
        I, M, A
    }

    public enum EntityTypeEnum {
        F, G
    }
}
