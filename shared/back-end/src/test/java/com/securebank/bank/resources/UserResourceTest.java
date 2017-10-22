package com.securebank.bank.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securebank.bank.model.Transaction;
import org.junit.Test;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class UserResourceTest {

    @Test
    public void name() throws Exception {
        PodamFactoryImpl podamFactory = new PodamFactoryImpl();
        Transaction account = podamFactory.manufacturePojo(Transaction.class);
        System.out.println(new ObjectMapper().writeValueAsString(account));
    }
}
