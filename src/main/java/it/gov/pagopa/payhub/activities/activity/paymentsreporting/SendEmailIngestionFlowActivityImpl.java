package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.IngestionFlowRetrieverService;
import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.SendMailService;
import it.gov.pagopa.payhub.activities.dto.reportingflow.IngestionFlowDTO;
import it.gov.pagopa.payhub.activities.exception.SendMailException;
import it.gov.pagopa.payhub.activities.helper.MailParameterHelper;
import it.gov.pagopa.payhub.activities.model.MailParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


/**
 * Implementation of SendEmailIngestionFlowActivity for send email ingestion flow activity.
 * Sends an email based on the status of a processed file identified by its IngestionFlow ID.
 */
@Slf4j
@Component
public class SendEmailIngestionFlowActivityImpl implements SendEmailIngestionFlowActivity {
    private final IngestionFlowRetrieverService ingestionFlowRetrieverService;
    private final SendMailService sendMailService;
    private final MailParams mailParams;
    private final JavaMailSender javaMailSender;

    public SendEmailIngestionFlowActivityImpl(IngestionFlowRetrieverService ingestionFlowRetrieverService, SendMailService sendMailService, MailParams mailParams, JavaMailSender javaMailSender) {
        this.ingestionFlowRetrieverService = ingestionFlowRetrieverService;
        this.sendMailService = sendMailService;
        this.mailParams = mailParams;
        this.javaMailSender = javaMailSender;
    }

    /**
     * Sends an email based on the process result of the given file ingestionFlow ID.
     *
     * @param ingestionFlowId       the unique identifier of the IngestionFlow record related to the imported file.
     * @param success      true if the process succeeded, false otherwise.
     * @return true if the email was sent successfully, false otherwise.
     */
    @Override
    public boolean sendEmail(String ingestionFlowId, boolean success) {
        // verify if previous operation is success
        if (success){
            try {
                IngestionFlowDTO ingestionFlowDTO = ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId));
                if (ingestionFlowDTO!=null) {
                    mailParams.setIngestionFlowDTO(ingestionFlowDTO);
                    mailParams.setId(ingestionFlowId);
                    mailParams.setMailText(getMailIngestionFlowText(ingestionFlowDTO));
                }
                // get e-mail parameters and send e-mail if there are no errors in parameters
                MailParams params = MailParameterHelper.getMailParams(mailParams);
                if (params.isSuccess()){
                    sendMailService.sendMail(javaMailSender, mailParams);
                    return true;
                }
            } catch (Exception e) {
                throw new SendMailException("Error sending mail for id: "+ingestionFlowId);
            }
        }
        return false;
    }

    /**
     * utility to get specific mail text for ingestion flow activity
     *
     * @param ingestionFlowDTO dto containing
     * @return String containing mail text
     */
    private String getMailIngestionFlowText(IngestionFlowDTO ingestionFlowDTO) {
        Long fileSize = ingestionFlowDTO.getDownloadedFileSize();
        Long totalRowsNumber = ingestionFlowDTO.getTotalRowsNumber();
        String mailText = "Il caricamento del file " + ingestionFlowDTO.getFileName();
        if (fileSize>0 && totalRowsNumber>0) {
            mailText += " è andato a buon fine, tutti i " + totalRowsNumber + " dati presenti sono stati caricati correttamente.";
        }
        else  {
            mailText += " NON è andato a buon fine";
        }
        return mailText;
    }

}

