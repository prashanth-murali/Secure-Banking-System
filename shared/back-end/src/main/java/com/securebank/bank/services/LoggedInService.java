package com.securebank.bank.services;

import com.securebank.bank.model.User;
import com.securebank.bank.services.errors.ApplicationValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Base64;


@Component
public class LoggedInService {

    Logger logger = LoggerFactory.getLogger(LoggedInService.class);

    @Autowired
    Base64Service base64Service;

    @Autowired
    UserRepository userRepository;

    public User getLoggedInUser(String authorization){
        try{


            String base64Credentials = authorization.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
            // credentials = username:password

            String[] pieces = credentials.split(":");
            String username = pieces[0];
            String password = pieces[1];

            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("MD5");
                md.update(password.getBytes());
                password = DatatypeConverter.printHexBinary(md.digest());
            } catch (Exception e) {
                throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "hashing error 1");
            }

            User byUsername = userRepository.findByUsername(username);
            if(byUsername == null){
                logger.info("Unable to find user from Authorization");
                throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid Auth");
            }

            if(password.equals(byUsername.getPassword())){
                logger.info("Returning logged in user: " + byUsername.toString());
                return byUsername;
            }else{
                logger.info("Invalid Password from Authorization");
                throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid Auth");
            }
        }catch (Exception e){
            logger.error("error while getting user from authentication", e);
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid Auth");
        }
    }
}
