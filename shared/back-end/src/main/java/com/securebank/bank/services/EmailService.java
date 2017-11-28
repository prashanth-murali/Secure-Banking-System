package com.securebank.bank.services;

import com.securebank.bank.resources.TransactionsResource;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailService {
    Logger logger = LoggerFactory.getLogger(TransactionsResource.class);

    private @Value("${sendgrid-api-key}") String sendGridKey;

    public void sendEmail(String toEmailAddress, String emailMessageBody){
        try {
            Email email = new SimpleEmail();
            email.setHostName("smtp.googlemail.com");
            email.setSmtpPort(465);
            email.setAuthenticator(new DefaultAuthenticator("cse545g5@gmail.com", "8xOSl6jwsNQ~64AOLkHAcgUwR"));
            email.setSSLOnConnect(true);
            email.setFrom("user@gmail.com");
            email.setSubject("TestMail");
            email.setMsg(emailMessageBody);
            email.addTo(toEmailAddress);
            email.send();
        } catch (Exception ex) {
            logger.error("error sending email", ex);
        }
    }
}
