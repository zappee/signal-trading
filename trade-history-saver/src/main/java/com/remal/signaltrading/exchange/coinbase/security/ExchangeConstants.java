package com.remal.signaltrading.exchange.coinbase.security;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;

public class ExchangeConstants {

    public static Mac mac;

    static {
        try {
            mac = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private ExchangeConstants() {
    }
}
