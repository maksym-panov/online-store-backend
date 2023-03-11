package com.panov.store.utils;

import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;

public final class PasswordEncoder {
    public static String encode(String password) {
        return new String(Base64.encode(password.getBytes()));
    }

    private PasswordEncoder() {}
}
