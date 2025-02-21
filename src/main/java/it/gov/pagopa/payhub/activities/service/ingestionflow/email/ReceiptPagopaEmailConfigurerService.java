package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Lazy
@Service
@Slf4j
public class ReceiptPagopaEmailConfigurerService {

  private static final DateTimeFormatter MAILDATETIMEFORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

  private final EmailTemplate receivedReceiptEmailTemplate;

  public ReceiptPagopaEmailConfigurerService(EmailTemplate receivedReceiptEmailTemplate) {
    this.receivedReceiptEmailTemplate = receivedReceiptEmailTemplate;
  }

  public List<String> retrieveRecipients(ReceiptDTO receiptDTO, InstallmentDTO installmentDTO) {
    //finding recipients
    List<String> toList = new ArrayList<>();
    //add debtor email
    Optional.ofNullable(StringUtils.firstNonBlank(
        installmentDTO.getDebtor().getEmail(),
        receiptDTO.getDebtor().getEmail()))
      .ifPresent(toList::add);
    //add payer email
    Optional.ofNullable(StringUtils.firstNonBlank(
        receiptDTO.getPayer().getEmail()))
      .ifPresent(toList::add);

    return toList;
  }

  public EmailDTO configure(ReceiptDTO receiptDTO) {
    Map<String, String> mailParams = getMailParameters(receiptDTO);
    return EmailDTO.builder()
      .params(getMailParameters(receiptDTO))
      .mailSubject(StringSubstitutor.replace(receivedReceiptEmailTemplate.getSubject(), mailParams, "{", "}"))
      .htmlText(Jsoup.clean(
        StringSubstitutor.replace(receivedReceiptEmailTemplate.getBody(), mailParams, "{", "}"),
        "", Safelist.none(), new Document.OutputSettings().prettyPrint(false)))
      .build();
  }

  private Map<String, String> getMailParameters(ReceiptDTO receiptDTO) {
    return Map.of(
      "companyName", StringUtils.firstNonBlank(receiptDTO.getCompanyName(),"-"),
      "orgFiscalCode", receiptDTO.getOrgFiscalCode(),
      "noticeNumber", receiptDTO.getNoticeNumber(),
      "amount", NumberFormat.getCurrencyInstance(Locale.ITALY).format(receiptDTO.getPaymentAmountCents()/100.0),
      "paymentDate", MAILDATETIMEFORMATTER.format(receiptDTO.getPaymentDateTime())
    );
  }
}
