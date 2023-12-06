package com.github.vssavin.usmancore.security;

import com.github.vssavin.jcrypt.js.JsJCryptStub;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Provides a service doesn't use any encryption algorithm.
 *
 * @author vssavin on 28.11.2023
 */
@Service
@Primary
class NoSecureService extends JsJCryptStub implements SecureService {

    @Override
    public String toString() {
        return "no";
    }

    @Override
    public String getPublicKey() {
        return "";
    }

    @Override
    public String getPublicKey(String id) {
        return "";
    }

    @Override
    public String getPrivateKey() {
        return "";
    }

    @Override
    public String getPrivateKey(String id) {
        return "";
    }

}
