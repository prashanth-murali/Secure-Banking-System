package com.securebank.bank.services;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

public class Base64Service {
    public String decode(String s) {
        return StringUtils.newStringUtf8(Base64.decodeBase64(s));
    }
    public String encode(String s) {
        return Base64.encodeBase64String(StringUtils.getBytesUtf8(s));
    }
}
