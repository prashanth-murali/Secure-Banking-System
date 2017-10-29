package com.securebank.bank.services;

import com.securebank.bank.resources.TransactionsResource;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EmailService {
    Logger logger = LoggerFactory.getLogger(TransactionsResource.class);

    private @Value("${sendgrid-api-key}") String sendGridKey;

    public void sendEmail(String toEmailAddress, String emailMessageBody){
        Email from = new Email("fromgroup5@softwaresecurity.xyz");
        String subject = "Bank-Service-Group5";
        Email to = new Email(toEmailAddress);
        Content content = new Content("text/plain", emailMessageBody);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            logger.info(String.valueOf(response.getStatusCode()));
            logger.info(response.getBody());
            logger.info(response.getHeaders().toString());
        } catch (IOException ex) {
            logger.error("error sending email", ex);
        }
    }
}
