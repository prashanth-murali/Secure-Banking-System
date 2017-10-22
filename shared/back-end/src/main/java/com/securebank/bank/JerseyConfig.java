package com.securebank.bank;

import com.securebank.bank.services.errors.ApplicationValidationErrorMapper;
import com.securebank.bank.services.errors.GlobalErrorMapper;
import com.securebank.bank.services.errors.NotFoundExceptionMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(GlobalErrorMapper.class);
        register(ApplicationValidationErrorMapper.class);
        register(NotFoundExceptionMapper.class);

        packages("com.securebank.bank.resources");
    }

}
