package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Lazy
@Service
@Slf4j
public class ReceiptPagoPaEmailConfigurerService {

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
      "debtorName", StringUtils.firstNonBlank(receiptDTO.getDebtor().getFullName(),"-"),
      "cieForMinorsUrl", "https://www.cartaidentita.interno.gov.it/richiedi/rilascio-e-rinnovo-minorenni/",
      "cieUrl", "https://www.cartaidentita.interno.gov.it/richiedi/rilascio-e-rinnovo-in-italia/",
      "cieUrlInfo", "https://www.pagacie.cartaidentita.interno.gov.it",
      "cieUrlFAQ", "https://www.pagacie.cartaidentita.interno.gov.it/faq"
    );
  }
}
