package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.AsyncSendMailService;
import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.IngestionFlowRetrieverService;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowDao;
import it.gov.pagopa.payhub.activities.dto.reportingflow.IngestionFlowDTO;
import it.gov.pagopa.payhub.activities.exception.SendMailException;
import it.gov.pagopa.payhub.activities.helper.MailParameterHelper;
import it.gov.pagopa.payhub.activities.model.MailParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendEmailIngestionFlowActivityImpl implements  SendEmailIngestionFlowActivity {
    private final IngestionFlowRetrieverService ingestionFlowRetrieverService;
    private final AsyncSendMailService asyncSendMailService;
    private final MailParams mailParams;
    private final JavaMailSender javaMailSender;
    private final IngestionFlowDao ingestionFlowDao;

    public SendEmailIngestionFlowActivityImpl(IngestionFlowRetrieverService ingestionFlowRetrieverService, AsyncSendMailService asyncSendMailService, IngestionFlowDao ingestionFlowDao, MailParams mailParams, JavaMailSender javaMailSender) {
        this.ingestionFlowRetrieverService = ingestionFlowRetrieverService;
        this.asyncSendMailService = asyncSendMailService;
        this.ingestionFlowDao = ingestionFlowDao;
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
                }
                // get e-mail parameters
                MailParams params = MailParameterHelper.getMailParams(mailParams);

                // send e-mail if there are no errors in parameters
                if (params.isSuccess()){
                    mailParams.setHtmlText(params.getHtmlText());
                    mailParams.setMailSubject(params.getMailSubject());
                    mailParams.setIngestionFlowId(ingestionFlowId);
                    asyncSendMailService.sendMail(javaMailSender, mailParams);
                    return true;
                }
            } catch (Exception e) {
                throw new SendMailException("Error sending mail for id: "+ingestionFlowId);
            }
        }
        return false;
    }
}
