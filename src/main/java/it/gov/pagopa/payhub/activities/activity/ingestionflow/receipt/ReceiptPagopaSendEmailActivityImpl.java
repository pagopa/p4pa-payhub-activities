package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.activity.email.SendEmailActivity;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.email.AttachmentDTO;
import it.gov.pagopa.payhub.activities.dto.email.TemplatedEmailDTO;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.ReceiptPagoPaEmailConfigurerService;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Slf4j
@Component
public class ReceiptPagopaSendEmailActivityImpl implements ReceiptPagopaSendEmailActivity {

  private final ReceiptPagoPaEmailConfigurerService receiptPagopaEmailConfigurerService;
  private final ReceiptService receiptService;
  private final OrganizationService organizationService;
  private final SendEmailActivity sendEmailActivity;

  public ReceiptPagopaSendEmailActivityImpl(ReceiptPagoPaEmailConfigurerService receiptPagopaEmailConfigurerService, ReceiptService receiptService, OrganizationService organizationService, SendEmailActivity sendEmailActivity) {
    this.receiptPagopaEmailConfigurerService = receiptPagopaEmailConfigurerService;
    this.receiptService = receiptService;
    this.organizationService = organizationService;
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

    Optional<Organization> organization = organizationService.getOrganizationByFiscalCode(receiptDTO.getOrgFiscalCode());
    if (organization.isEmpty()) {
      log.info("Not sending email for receipt id[{}] [{}/{}]: organization not found", receiptDTO.getReceiptId(), receiptDTO.getOrgFiscalCode(), receiptDTO.getNoticeNumber());
      return;
    }

    Long organizationId = organization.get().getOrganizationId();
    File receiptPdf = receiptService.getReceiptPdf(receiptDTO.getReceiptId(), organizationId);

    AttachmentDTO attachment = new AttachmentDTO(receiptPdf, receiptPdf.getName());
    sendEmailActivity.sendTemplatedEmail(new TemplatedEmailDTO(
        EmailTemplateName.INGESTION_PAGOPA_RT, recipients.toArray(new String[0]), null, params, attachment)
    );
    //configure email
  }

}