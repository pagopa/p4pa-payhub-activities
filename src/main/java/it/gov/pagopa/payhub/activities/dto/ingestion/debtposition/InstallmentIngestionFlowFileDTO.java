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

    @CsvBindByName(column = "action", required = true)
    private ActionEnum action;

    @CsvBindByName(column = "draft")
    private Boolean draft;

    @CsvBindByName(column = "iupdOrg")
    private String iupdOrg;

    @CsvBindByName(column = "description", required = true)
    private String description;

    @CsvCustomBindByName(column = "validityDate", converter = CsvOffsetDateTimeConverter.class)
    private OffsetDateTime validityDate;

    @CsvBindByName(column = "multiDebtor")
    private Boolean multiDebtor;

    @CsvCustomBindByName(column = "notificationDate", converter = CsvOffsetDateTimeConverter.class)
    private OffsetDateTime notificationDate;

    @CsvBindByName(column = "paymentOptionIndex", required = true)
    private Integer paymentOptionIndex;

    @CsvBindByName(column = "paymentOptionType", required = true)
    private String paymentOptionType;

    @CsvBindByName(column = "paymentOptionDescription")
    private String paymentOptionDescription;

    @CsvBindByName(column = "iud")
    private String iud;

    @CsvBindByName(column = "iuv")
    private String iuv;

    @CsvBindByName(column = "entityType", required = true)
    private EntityTypeEnum entityType;

    @CsvBindByName(column = "fiscalCode", required = true)
    private String fiscalCode;

    @CsvBindByName(column = "fullName", required = true)
    private String fullName;

    @CsvBindByName(column = "address")
    private String address;

    @CsvBindByName(column = "civic")
    private String civic;

    @CsvBindByName(column = "postalCode")
    private String postalCode;

    @CsvBindByName(column = "location")
    private String location;

    @CsvBindByName(column = "province")
    private String province;

    @CsvBindByName(column = "nation")
    private String nation;

    @CsvBindByName(column = "email")
    private String email;

    @CsvCustomBindByName(column = "dueDate", converter = CsvOffsetDateTimeConverter.class)
    private OffsetDateTime dueDate;

    @CsvBindByName(column = "amount", required = true)
    private BigDecimal amount;

    @CsvBindByName(column = "debtPositionTypeCode", required = true)
    private String debtPositionTypeCode;

    @CsvBindByName(column = "paymentTypeCode")
    private String paymentTypeCode;

    @CsvBindByName(column = "remittanceInformation", required = true)
    private String remittanceInformation;

    @CsvBindByName(column = "legacyPaymentMetadata")
    private String legacyPaymentMetadata;

    @CsvBindByName(column = "flagPagoPaPayment", required = true)
    private Boolean flagPagoPaPayment;

    @CsvBindByName(column = "balance")
    private String balance;

    @CsvBindByName(column = "flagMultiBeneficiary")
    private Boolean flagMultiBeneficiary;

    @CsvBindByName(column = "numberBeneficiary")
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
