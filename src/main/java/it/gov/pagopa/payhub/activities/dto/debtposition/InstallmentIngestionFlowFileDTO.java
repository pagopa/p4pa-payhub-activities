package it.gov.pagopa.payhub.activities.dto.debtposition;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallmentIngestionFlowFileDTO {

    @CsvIgnore
    private Long ingestionFlowFileLineNumber;

    @CsvBindByName(column = "action", required = true)
    private ActionEnum action;

    @CsvBindByName(column = "draft", required = true)
    private Boolean draft;

    @CsvBindByName(column = "iupdOrg", required = true)
    private String iupdOrg;

    @CsvBindByName(column = "description", required = true)
    private String description;

    @CsvBindByName(column = "validityDate", required = true)
    private OffsetDateTime validityDate;

    @CsvBindByName(column = "multiDebtor", required = true)
    private Boolean multiDebtor;

    @CsvBindByName(column = "notificationDate", required = true)
    private OffsetDateTime notificationDate;

    @CsvBindByName(column = "paymentOptionIndex", required = true)
    private Integer paymentOptionIndex;

    @CsvBindByName(column = "paymentOptionType", required = true)
    private String paymentOptionType;

    @CsvBindByName(column = "paymentOptionDescription", required = true)
    private String paymentOptionDescription;

    @CsvBindByName(column = "iud", required = true)
    private String iud;

    @CsvBindByName(column = "iuv", required = true)
    private String iuv;

    @CsvBindByName(column = "entityType", required = true)
    private EntityTypeEnum entityType;

    @CsvBindByName(column = "fiscalCode", required = true)
    private String fiscalCode;

    @CsvBindByName(column = "fullName", required = true)
    private String fullName;

    @CsvBindByName(column = "address", required = true)
    private String address;

    @CsvBindByName(column = "civic", required = true)
    private String civic;

    @CsvBindByName(column = "postalCode", required = true)
    private String postalCode;

    @CsvBindByName(column = "location", required = true)
    private String location;

    @CsvBindByName(column = "province", required = true)
    private String province;

    @CsvBindByName(column = "nation", required = true)
    private String nation;

    @CsvBindByName(column = "email", required = true)
    private String email;

    @CsvBindByName(column = "dueDate", required = true)
    private OffsetDateTime dueDate;

    @CsvBindByName(column = "amount", required = true)
    private BigDecimal amount;

    @CsvBindByName(column = "debtPositionTypeCode", required = true)
    private String debtPositionTypeCode;

    @CsvBindByName(column = "paymentTypeCode", required = true)
    private String paymentTypeCode;

    @CsvBindByName(column = "remittanceInformation", required = true)
    private String remittanceInformation;

    @CsvBindByName(column = "legacyPaymentMetadata", required = true)
    private String legacyPaymentMetadata;

    @CsvBindByName(column = "flagPagoPaPayment", required = true)
    private Boolean flagPagoPaPayment;

    @CsvBindByName(column = "balance", required = true)
    private String balance;

    @CsvBindByName(column = "flagMultiBeneficiary", required = true)
    private Boolean flagMultiBeneficiary;

    @CsvBindByName(column = "numberBeneficiary")
    private Integer numberBeneficiary;

    @CsvBindByName(column = "orgFiscalCode_2")
    private String orgFiscalCode_2;

    @CsvBindByName(column = "orgName_2")
    private String orgName_2;

    @CsvBindByName(column = "iban_2")
    private String iban_2;

    @CsvBindByName(column = "orgAddress_2")
    private String orgAddress_2;

    @CsvBindByName(column = "orgCivic_2")
    private String orgCivic_2;

    @CsvBindByName(column = "orgPostCode_2")
    private String orgPostCode_2;

    @CsvBindByName(column = "orgCity_2")
    private String orgCity_2;

    @CsvBindByName(column = "orgProvince_2")
    private String orgProvince_2;

    @CsvBindByName(column = "orgNation_2")
    private String orgNation_2;

    @CsvBindByName(column = "orgRemittanceInformation_2")
    private String orgRemittanceInformation_2;

    @CsvBindByName(column = "amount_2")
    private BigDecimal amount_2;

    @CsvBindByName(column = "category_2")
    private String category_2;

    @CsvBindByName(column = "orgFiscalCode_3")
    private String orgFiscalCode_3;

    @CsvBindByName(column = "orgName_3")
    private String orgName_3;

    @CsvBindByName(column = "iban_3")
    private String iban_3;

    @CsvBindByName(column = "amount_3")
    private BigDecimal amount_3;

    @CsvBindByName(column = "category_3")
    private String category_3;

    @CsvBindByName(column = "orgFiscalCode_4")
    private String orgFiscalCode_4;

    @CsvBindByName(column = "orgName_4")
    private String orgName_4;

    @CsvBindByName(column = "iban_4")
    private String iban_4;

    @CsvBindByName(column = "amount_4")
    private BigDecimal amount_4;

    @CsvBindByName(column = "category_4")
    private String category_4;

    @CsvBindByName(column = "orgFiscalCode_5")
    private String orgFiscalCode_5;

    @CsvBindByName(column = "orgName_5")
    private String orgName_5;

    @CsvBindByName(column = "iban_5")
    private String iban_5;

    @CsvBindByName(column = "amount_5")
    private BigDecimal amount_5;

    @CsvBindByName(column = "category_5")
    private String category_5;

    public enum ActionEnum {
        I, M, A
    }

    public enum EntityTypeEnum {
        F, G
    }
}
