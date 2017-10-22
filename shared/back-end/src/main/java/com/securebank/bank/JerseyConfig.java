package com.securebank.bank;

import com.securebank.bank.resources.ApplicationResource;
import com.securebank.bank.resources.LoginResource;
import com.securebank.bank.resources.TransactionsResource;
import com.securebank.bank.resources.UserResource;
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
        register(ApplicationResource.class);
        register(UserResource.class);
        register(LoginResource.class);
        register(TransactionsResource.class);
    }

}
