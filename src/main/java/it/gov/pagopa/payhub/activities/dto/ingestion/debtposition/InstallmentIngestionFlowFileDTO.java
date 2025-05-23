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

import static it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileVersions.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallmentIngestionFlowFileDTO {

    @CsvBindByName(column = "IUPD", profiles = V2_0)
    @CsvBindByName(column = "iupdOrg", profiles = V2_0)
    private String iupdOrg;

    @CsvBindByName(column = "descrizionePosizioneDebitoria", profiles = V2_0)
    @CsvBindByName(column = "description", profiles = V2_0)
    private String description;

    @CsvBindByName(column = "dataValidita", profiles = {V1_4, V2_0})
    @CsvBindByName(column = "validityDate", profiles = V2_0)
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate validityDate;

    @CsvBindByName(column = "multiDebtor")
    private Boolean multiDebtor;

    @CsvBindByName(column = "dataNotifica", profiles = V2_0)
    @CsvBindByName(column = "notificationDate", profiles = V2_0)
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate notificationDate;

    @CsvBindByName(column = "indiceOpzionePagamento", profiles = {V1_4, V2_0})
    @CsvBindByName(column = "paymentOptionIndex", profiles = V2_0)
    private Integer paymentOptionIndex;

    @CsvBindByName(column = "tipoOpzionePagamento", profiles = V2_0)
    @CsvBindByName(column = "paymentOptionType", profiles = V2_0)
    private String paymentOptionType;

    @CsvBindByName(column = "descrizioneOpzionePagamento", profiles = V2_0)
    @CsvBindByName(column = "paymentOptionDescription", profiles = V2_0)
    private String paymentOptionDescription;

    @CsvBindByName(column = "iud", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    private String iud;

    @CsvBindByName(column = "codIUV", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    @CsvBindByName(column = "iuv", profiles = V2_0)
    private String iuv;

    @CsvBindByName(column = "tipoIdentificativoUnivoco", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    @CsvBindByName(column = "entityType", profiles = V2_0)
    private EntityTypeEnum entityType;

    @CsvBindByName(column = "codiceIdentificativoUnivoco", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    @CsvBindByName(column = "fiscalCode", profiles = V2_0)
    private String fiscalCode;

    @CsvBindByName(column = "anagraficaPagatore", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    @CsvBindByName(column = "fullName", profiles = V2_0)
    private String fullName;

    @CsvBindByName(column = "indirizzoPagatore", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    @CsvBindByName(column = "address", profiles = V2_0)
    private String address;

    @CsvBindByName(column = "civicoPagatore", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    @CsvBindByName(column = "civic", profiles = V2_0)
    private String civic;

    @CsvBindByName(column = "capPagatore", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    @CsvBindByName(column = "postalCode", profiles = V2_0)
    private String postalCode;

    @CsvBindByName(column = "localitaPagatore", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    @CsvBindByName(column = "location", profiles = V2_0)
    private String location;

    @CsvBindByName(column = "provinciaPagatore", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    @CsvBindByName(column = "province", profiles = V2_0)
    private String province;

    @CsvBindByName(column = "nazionePagatore", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    @CsvBindByName(column = "nation", profiles = V2_0)
    private String nation;

    @CsvBindByName(column = "emailPagatore", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    @CsvBindByName(column = "email", profiles = V2_0)
    private String email;

    @CsvBindByName(column = "dataEsecuzionePagamento", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    @CsvBindByName(column = "dueDate", profiles = V2_0)
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate dueDate;

    @CsvBindByName(column = "importoDovuto", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    @CsvBindByName(column = "amount", profiles = V2_0)
    private BigDecimal amount;

    @CsvBindByName(column = "tipoDovuto", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    @CsvBindByName(column = "debtPositionTypeCode", profiles = V2_0)
    private String debtPositionTypeCode;

    @CsvBindByName(column = "commissioneNotifica", profiles = V2_0)
    @CsvBindByName(column = "notificationFee", profiles = V2_0)
    private BigDecimal notificationFee;

    @CsvBindByName(column = "causaleVersamento", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    @CsvBindByName(column = "remittanceInformation", profiles = V2_0)
    private String remittanceInformation;

    @CsvBindByName(column = "datiSpecificiRiscossione", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    @CsvBindByName(column = "legacyPaymentMetadata", profiles = V2_0)
    private String legacyPaymentMetadata;

    @CsvBindByName(column = "flagGeneraIuv", profiles = {V1_3, V1_4, V2_0})
    @CsvBindByName(column = "flagPagoPaPayment", profiles = V2_0)
    private Boolean flagPagoPaPayment;

    @CsvBindByName(column = "bilancio", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    @CsvBindByName(column = "balance", profiles = V2_0)
    private String balance;

    @CsvBindByName(column = "flagMultiBeneficiario", profiles = {V1_4, V2_0})
    @CsvBindByName(column = "flagMultiBeneficiary", profiles = V2_0)
    private Boolean flagMultiBeneficiary;

    @CsvBindByName(column = "numeroBeneficiari", profiles = V2_0)
    @CsvBindByName(column = "numberBeneficiary", profiles = V2_0)
    private Integer numberBeneficiary;

    @CsvBindAndJoinByName(column = ".*_2", elementType = String.class, profiles = {V1_4, V2_0})
    private MultiValuedMap<String, String> transfer2;

    @CsvBindAndJoinByName(column = ".*_3", elementType = String.class, profiles = V2_0)
    private MultiValuedMap<String, String> transfer3;

    @CsvBindAndJoinByName(column = ".*_4", elementType = String.class, profiles = V2_0)
    private MultiValuedMap<String, String> transfer4;

    @CsvBindAndJoinByName(column = ".*_5", elementType = String.class, profiles = V2_0)
    private MultiValuedMap<String, String> transfer5;

    @CsvBindByName(column = "configurazioniEsecuzione", profiles = V2_0)
    @CsvBindByName(column = "executionConfig", profiles = V2_0)
    private String executionConfig;

    @CsvBindByName(column = "azione", profiles = {V1_0, V1_1, V1_2, V1_3, V1_4, V2_0})
    @CsvBindByName(column = "action", profiles = V2_0)
    private ActionEnum action;

    @CsvBindByName(column = "draft", profiles = V2_0)
    private Boolean draft;

    public enum ActionEnum {
        I, M, A
    }

    public enum EntityTypeEnum {
        F, G
    }
}
