package it.gov.pagopa.payhub.activities.dto.ingestion.debtposition;

import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvIgnore;
import it.gov.pagopa.payhub.activities.util.csv.CsvOffsetDateTimeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MultiValuedMap;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallmentIngestionFlowFileDTO {

    @CsvIgnore
    private Long ingestionFlowFileLineNumber;

    @CsvBindByName(column = "azione", required = true)
    private ActionEnum action;

    @CsvBindByName(column = "draft")
    private Boolean draft;

    @CsvBindByName(column = "IUPD")
    private String iupdOrg;

    @CsvBindByName(column = "descrizione", required = true)
    private String description;

    @CsvCustomBindByName(column = "dataValidita", converter = CsvOffsetDateTimeConverter.class)
    private OffsetDateTime validityDate;

    @CsvBindByName(column = "multiDebtor")
    private Boolean multiDebtor;

    @CsvCustomBindByName(column = "dataNotifica", converter = CsvOffsetDateTimeConverter.class)
    private OffsetDateTime notificationDate;

    @CsvBindByName(column = "indiceOpzionePagamento", required = true)
    private Integer paymentOptionIndex;

    @CsvBindByName(column = "tipoOpzionePagamento", required = true)
    private String paymentOptionType;

    @CsvBindByName(column = "descrizioneOpzionePagamento")
    private String paymentOptionDescription;

    @CsvBindByName(column = "IUD")
    private String iud;

    @CsvBindByName(column = "codIUV")
    private String iuv;

    @CsvBindByName(column = "tipoIdentificativoUnivoco", required = true)
    private EntityTypeEnum entityType;

    @CsvBindByName(column = "codiceIdentificativoUnivoco", required = true)
    private String fiscalCode;

    @CsvBindByName(column = "anagraficaPagatore", required = true)
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

    @CsvBindByName(column = "emailPagatore")
    private String email;

    @CsvCustomBindByName(column = "dataEsecuzionePagamento", converter = CsvOffsetDateTimeConverter.class)
    private OffsetDateTime dueDate;

    @CsvBindByName(column = "importoDovuto", required = true)
    private BigDecimal amount;

    @CsvBindByName(column = "tipoDovuto", required = true)
    private String debtPositionTypeCode;

    @CsvBindByName(column = "tipoVersamento")
    private String paymentTypeCode;

    @CsvBindByName(column = "causaleVersamento", required = true)
    private String remittanceInformation;

    @CsvBindByName(column = "datiSpecificiRiscossione")
    private String legacyPaymentMetadata;

    @CsvBindByName(column = "flagGeneraIuv", required = true)
    private Boolean flagPagoPaPayment;

    @CsvBindByName(column = "bilancio")
    private String balance;

    @CsvBindByName(column = "flagMultiBeneficiario")
    private Boolean flagMultiBeneficiary;

    @CsvBindByName(column = "numeroBeneficiari")
    private Integer numberBeneficiary;

    @CsvBindAndJoinByName(column = "*_2", elementType = String.class)
    private MultiValuedMap<String, String> transfer2;

    @CsvBindAndJoinByName(column = "*_3", elementType = String.class)
    private MultiValuedMap<String, String> transfer3;

    @CsvBindAndJoinByName(column = "*_4", elementType = String.class)
    private MultiValuedMap<String, String> transfer4;

    @CsvBindAndJoinByName(column = "*_5", elementType = String.class)
    private MultiValuedMap<String, String> transfer5;


    public enum ActionEnum {
        I, M, A
    }

    public enum EntityTypeEnum {
        F, G
    }
}
