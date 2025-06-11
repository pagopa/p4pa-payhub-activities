package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.activity.email.SendEmailActivity;
import it.gov.pagopa.payhub.activities.dto.email.TemplatedEmailDTO;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.ReceiptPagoPaEmailConfigurerService;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Lazy
@Slf4j
@Component
public class ReceiptPagopaSendEmailActivityImpl implements ReceiptPagopaSendEmailActivity {

  private final ReceiptPagoPaEmailConfigurerService receiptPagopaEmailConfigurerService;
  private final SendEmailActivity sendEmailActivity;

  public ReceiptPagopaSendEmailActivityImpl(ReceiptPagoPaEmailConfigurerService receiptPagopaEmailConfigurerService, SendEmailActivity sendEmailActivity) {
    this.receiptPagopaEmailConfigurerService = receiptPagopaEmailConfigurerService;
    this.sendEmailActivity = sendEmailActivity;
  }


  @Override
  public void sendReceiptHandledEmail(ReceiptWithAdditionalNodeDataDTO receiptDTO, InstallmentDTO installmentDTO) {
    if (installmentDTO == null) {
      log.info("Not sending email for receipt id[{}] [{}/{}]: installment is null",
        receiptDTO.getReceiptId(), receiptDTO.getOrgFiscalCode(), receiptDTO.getNoticeNumber());
      return;
    } else {
      log.info("Starting send email for receipt id[{}] [{}/{}] and installment id[{}]",
        receiptDTO.getReceiptId(), receiptDTO.getOrgFiscalCode(), receiptDTO.getNoticeNumber(),
        installmentDTO.getInstallmentId());
    }

    //retrieve recipients
    List<String> recipients = receiptPagopaEmailConfigurerService.retrieveRecipients(receiptDTO, installmentDTO);
    if (recipients.isEmpty()) {
      log.info("Not sending email for receipt id[{}] [{}/{}]: no recipient found",
        receiptDTO.getReceiptId(), receiptDTO.getOrgFiscalCode(), receiptDTO.getNoticeNumber());
      return;
    }

    Map<String, String> params = receiptPagopaEmailConfigurerService.buildTemplateParams(receiptDTO);

    sendEmailActivity.sendTemplatedEmail(new TemplatedEmailDTO(
            EmailTemplateName.INGESTION_PAGOPA_RT, recipients.toArray(new String[0]), null, params
    ));
    //configure email
  }

}