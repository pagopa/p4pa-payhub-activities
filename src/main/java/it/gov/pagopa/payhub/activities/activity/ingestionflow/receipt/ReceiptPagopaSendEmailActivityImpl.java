package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.activity.email.SendEmailActivity;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.BrokerService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.email.FileResourceDTO;
import it.gov.pagopa.payhub.activities.dto.email.TemplatedEmailDTO;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.ReceiptPagoPaEmailConfigurerService;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import it.gov.pagopa.pu.organization.dto.generated.BrokerConfiguration;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
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
  private final BrokerService brokerService;

  public static final String DEFAULT_RECEIPT_FILE_EXTENSION = "pdf";

  public ReceiptPagopaSendEmailActivityImpl(ReceiptPagoPaEmailConfigurerService receiptPagopaEmailConfigurerService, ReceiptService receiptService, OrganizationService organizationService, BrokerService brokerService, SendEmailActivity sendEmailActivity) {
    this.receiptPagopaEmailConfigurerService = receiptPagopaEmailConfigurerService;
    this.receiptService = receiptService;
    this.organizationService = organizationService;
    this.sendEmailActivity = sendEmailActivity;
    this.brokerService = brokerService;
  }


  @Override
  public void sendReceiptHandledEmail(ReceiptWithAdditionalNodeDataDTO receiptDTO, InstallmentDTO installmentDTO) {
      if(receiptDTO.getOrgFiscalCode().startsWith("UNKNOWN_")) {
          log.info("Not sending email for receipt id [{}]: organization fiscal code is UNKNOWN",
                  receiptDTO.getReceiptId());
          return;
      }

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
    Optional<Broker> brokerOpt = Optional.ofNullable(brokerService.getBrokerById(organization.get().getBrokerId()));

    Long organizationId = organization.get().getOrganizationId();
    String mailSenderAddress = Optional.ofNullable(brokerService.getBrokerConfigurationsById(organization.get().getBrokerId()))
            .map(BrokerConfiguration::getMailSenderAddress)
            .orElse(null);
    FileResourceDTO attachment = receiptService.getReceiptPdf(receiptDTO.getReceiptId(), organizationId);
    attachment.setFileName(buildReceiptFileName(receiptDTO, attachment.getFileName()));
    sendEmailActivity.sendTemplatedEmail(
            brokerOpt.map(Broker::getBrokerId).orElse(null),
            new TemplatedEmailDTO(
                    EmailTemplateName.INGESTION_PAGOPA_RT,
                    mailSenderAddress,
                    recipients.toArray(new String[0]),
                    null,
                    params,
                    attachment
            )
    );
    //configure email
  }

  private static String buildReceiptFileName(ReceiptWithAdditionalNodeDataDTO receiptDTO, String originalFilename) {
    return receiptDTO.getPaymentDateTime() == null ?
            originalFilename :
            receiptDTO.getPaymentDateTime().toLocalDate() + "-" + receiptDTO.getNoticeNumber() + "." + extractReceiptFileExtension(originalFilename);
  }

  private static String extractReceiptFileExtension(String originalFilename) {
    if(originalFilename == null)
      return DEFAULT_RECEIPT_FILE_EXTENSION;
    return originalFilename.substring(originalFilename.lastIndexOf(".")+1);
  }

}