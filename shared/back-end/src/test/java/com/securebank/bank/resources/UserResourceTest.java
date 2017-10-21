package com.securebank.bank.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securebank.bank.model.User;
import org.junit.Test;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class UserResourceTest {

    @Test
    public void name() throws Exception {
        PodamFactoryImpl podamFactory = new PodamFactoryImpl();
        User user = podamFactory.manufacturePojo(User.class);
        System.out.println(new ObjectMapper().writeValueAsString(user));
    }
}
