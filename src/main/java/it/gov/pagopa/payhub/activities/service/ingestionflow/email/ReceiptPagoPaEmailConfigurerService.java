package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Lazy
@Service
@Slf4j
public class ReceiptPagoPaEmailConfigurerService {

  private static final DateTimeFormatter MAILDATETIMEFORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

  public List<String> retrieveRecipients(ReceiptWithAdditionalNodeDataDTO receiptDTO, InstallmentDTO installmentDTO) {
    //finding recipients
    List<String> toList = new ArrayList<>();
    //add debtor email
    Optional.ofNullable(StringUtils.firstNonBlank(
        installmentDTO.getDebtor().getEmail(),
        receiptDTO.getDebtor().getEmail()))
      .ifPresent(toList::add);
    //add payer email
    Optional.ofNullable(receiptDTO.getPayer())
            .map(PersonDTO::getEmail)
      .ifPresent(toList::add);

    return toList;
  }

  public Map<String, String> buildTemplateParams(ReceiptWithAdditionalNodeDataDTO receiptDTO) {
    return Map.of(
      "companyName", StringUtils.firstNonBlank(receiptDTO.getCompanyName(),"-"),
      "orgFiscalCode", receiptDTO.getOrgFiscalCode(),
      "noticeNumber", receiptDTO.getNoticeNumber(),
      "amount", NumberFormat.getCurrencyInstance(Locale.ITALY).format(Utilities.longCentsToBigDecimalEuro(receiptDTO.getPaymentAmountCents())),
      "paymentDate", MAILDATETIMEFORMATTER.format(receiptDTO.getPaymentDateTime())
    );
  }
}
