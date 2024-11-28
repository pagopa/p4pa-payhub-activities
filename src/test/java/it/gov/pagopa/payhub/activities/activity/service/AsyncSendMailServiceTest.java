package it.gov.pagopa.payhub.activities.activity.service;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.AsyncSendMailService;
import it.gov.pagopa.payhub.activities.model.MailParams;
import it.gov.pagopa.payhub.activities.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AsyncSendMailServiceTest {
    @Mock
    AsyncSendMailService asyncSendMailService;

    private MailParams mailParams;
    private JavaMailSender javaMailSender;

    @BeforeEach
    void init() {
        javaMailSender = new JavaMailSenderImpl();
        mailParams = new MailParams();
    }

    @Test
    void asyncSendMail() {
        boolean result = true;
        mailParams = getMailParams();
        try {
            asyncSendMailService.sendMail(javaMailSender, mailParams);
        }
        catch (Exception e){
            result = false;
        }
        assertTrue(result);

    }

    private MailParams getMailParams() {
        DateFormat parser = new SimpleDateFormat("EEE, MMM dd yyyy, hh:mm:ss");
        String actualDate = parser.format(new Date());
        String mailText = "text of the mail";

        Map<String,String> map = new HashMap<>();
        map.put(Constants.MAIL_TEXT, mailText);
        map.put(Constants.ACTUAL_DATE,actualDate);
        map.put(Constants.FILE_NAME, "filename");

        mailParams.setEmailFromAddress("test@test.com");
        mailParams.setEmailFromName("test");
        mailParams.setParams(map);
        return mailParams;
    }

}
