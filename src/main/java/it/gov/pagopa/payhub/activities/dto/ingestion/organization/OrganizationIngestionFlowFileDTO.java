package it.gov.pagopa.payhub.activities.dto.ingestion.organization;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class OrganizationIngestionFlowFileDTO {

  @CsvBindByName(column = "ipa_code", required = true)
  private String ipaCode;

  @CsvBindByName(column = "org_fiscal_code", required = true)
  private String orgFiscalCode;

  @CsvBindByName(column = "org_name", required = true)
  private String orgName;

  @CsvBindByName(column = "org_type_code")
  private String orgTypeCode;

  @CsvBindByName(column = "org_email")
  private String orgEmail;

  @CsvBindByName(column = "iban")
  private String iban;

  @CsvBindByName(column = "postal_iban")
  private String postalIban;

  @CsvBindByName(column = "segregation_code")
  private String segregationCode;

  @CsvBindByName(column = "cbill_inter_bank_code")
  private String cbillInterBankCode;

  @CsvBindByName(column = "org_logo")
  private String orgLogo;

  @CsvBindByName(column = "status", required = true)
  private String status;

  @CsvBindByName(column = "additional_language")
  private String additionalLanguage;

  @CsvBindByName(column = "start_date")
  @CsvDate(value = "yyyy-MM-dd")
  private LocalDateTime startDate;

  @CsvBindByName(column = "broker_cf")
  private String brokerCf;

  @CsvBindByName(column = "io_api_key")
  private String ioApiKey;

  @CsvBindByName(column = "flag_notify_io", required = true)
  private Boolean flagNotifyIo;

  @CsvBindByName(column = "flag_notify_outcome_push", required = true)
  private Boolean flagNotifyOutcomePush;

  @CsvBindByName(column = "send_api_key")
  private String sendApiKey;

  @CsvBindByName(column = "flag_treasury", required = true)
  private Boolean flagTreasury;
}