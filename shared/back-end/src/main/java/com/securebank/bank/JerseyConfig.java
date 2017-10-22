package com.securebank.bank;

import com.securebank.bank.resources.TransactionsResource;
import com.securebank.bank.resources.UserResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(UserResource.class);
        register(TransactionsResource.class);
    }

}
