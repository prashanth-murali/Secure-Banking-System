package com.securebank.bank.resources;

import com.securebank.bank.services.EmailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationResourceTest {

    @Autowired
    EmailService emailService;

    @Test
    public void name() throws Exception {
        emailService.sendEmail("jkieley@asu.edu", "test1");
    }
}