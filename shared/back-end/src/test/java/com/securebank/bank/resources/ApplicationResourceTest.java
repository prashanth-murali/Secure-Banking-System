package com.securebank.bank.resources;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.juddi.v3.client.mapping.MockSSLSocketFactory;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

public class ApplicationResourceTest {

    @Before
    public void setUp() throws Exception {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        try {
            schemeRegistry.register(new Scheme("https", 443, new MockSSLSocketFactory()));
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
        DefaultHttpClient httpclient = new DefaultHttpClient(cm);

        Unirest.setHttpClient(httpclient);
    }


//    @Test
//    public void name() throws Exception {
//        HttpResponse<String> stringHttpResponse = Unirest.get(TestConfig.BASE_URL+"/version").asString();
//        System.out.println(stringHttpResponse.getBody());
//    }
}