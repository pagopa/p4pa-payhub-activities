package it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification;

import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MultiValuedMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendNotificationIngestionFlowFileDTO {

  @CsvBindByName(column = "organizationId", required = true)
  private Long organizationId;

  @CsvBindByName(column = "paProtocolNumber", required = true)
  private String paProtocolNumber;

  @CsvBindByName(column = "notificationFeePolicy", required = true)
  private String notificationFeePolicy;

  @CsvBindByName(column = "physicalCommunicationType", required = true)
  private String physicalCommunicationType;

  @CsvBindByName(column = "senderDenomination")
  private String senderDenomination;

  @CsvBindByName(column = "senderTaxId")
  private String senderTaxId;

  @CsvBindByName(column = "amount")
  private BigDecimal amount;

  @CsvBindByName(column = "paymentExpirationDate")
  @CsvDate(value = "yyyy-MM-dd")
  private LocalDate paymentExpirationDate;

  @CsvBindByName(column = "taxonomyCode")
  private String taxonomyCode;

  @CsvBindByName(column = "paFee")
  private Integer paFee;

  @CsvBindByName(column = "vat")
  private Integer vat;

  @CsvBindByName(column = "pagoPaIntMode")
  private String pagoPaIntMode;

  @CsvBindByName(column = "recipientType", required = true)
  private String recipientType;

  @CsvBindByName(column = "taxId", required = true)
  private String taxId;

  @CsvBindByName(column = "denomination", required = true)
  private String denomination;

  @CsvBindByName(column = "address", required = true)
  private String address;

  @CsvBindByName(column = "zip")
  private String zip;

  @CsvBindByName(column = "municipality", required = true)
  private String municipality;

  @CsvBindByName(column = "province")
  private String province;

  @CsvBindByName(column = "digitalDomicileAddress")
  private String digitalDomicileAddress;

  @CsvBindByName(column = "digitalDomicileType")
  private String digitalDomicileType;

  @CsvBindAndJoinByName(column = "payment.*_1", elementType = String.class)
  private MultiValuedMap<String, String> payment;

  @CsvBindAndJoinByName(column = "attachment.*_1", elementType = String.class)
  private MultiValuedMap<String, String> attachment;

  @CsvBindAndJoinByName(column = "payment.*_2", elementType = String.class)
  private MultiValuedMap<String, String> payment2;

  @CsvBindAndJoinByName(column = "attachment.*_2", elementType = String.class)
  private MultiValuedMap<String, String> attachment2;

  @CsvBindAndJoinByName(column = "payment.*_3", elementType = String.class)
  private MultiValuedMap<String, String> payment3;

  @CsvBindAndJoinByName(column = "attachment.*_3", elementType = String.class)
  private MultiValuedMap<String, String> attachment3;

  @CsvBindAndJoinByName(column = "payment.*_4", elementType = String.class)
  private MultiValuedMap<String, String> payment4;

  @CsvBindAndJoinByName(column = "attachment.*_4", elementType = String.class)
  private MultiValuedMap<String, String> attachment4;

  @CsvBindAndJoinByName(column = "payment.*_5", elementType = String.class)
  private MultiValuedMap<String, String> payment5;

  @CsvBindAndJoinByName(column = "attachment.*_5", elementType = String.class)
  private MultiValuedMap<String, String> attachment5;

  @CsvBindAndJoinByName(column = "document.*_1", elementType = String.class, required = true)
  private MultiValuedMap<String, String> document;

  @CsvBindAndJoinByName(column = "document.*_2", elementType = String.class)
  private MultiValuedMap<String, String> document2;

  @CsvBindAndJoinByName(column = "document.*_3", elementType = String.class)
  private MultiValuedMap<String, String> document3;

  @CsvBindAndJoinByName(column = "document.*_4", elementType = String.class)
  private MultiValuedMap<String, String> document4;

  @CsvBindAndJoinByName(column = "document.*_5", elementType = String.class)
  private MultiValuedMap<String, String> document5;

}
