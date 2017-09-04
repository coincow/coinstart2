package com.coincow.coinstart;

/**
 * Created by lizuochuan on 24/08/2017.
 */

public class YunbiApiHelper {

    private final static String AccessKey = "";
    private final static String PrivateKey = "";

    public static String getSignatureUrl(String baseUrl, String shortApi, String param)
    {
        String signatureUrl = baseUrl + shortApi;
        String signature = createSignature(shortApi, param);
        signatureUrl += "?";
        signatureUrl += "access_key=";
        signatureUrl += AccessKey;
        signatureUrl += "&" + param + "&" + "tonce=" + System.currentTimeMillis();
        signatureUrl += "&" + "signature=" + signature;
        return signatureUrl;
    }

    private static String createSignature(String shortApi, String param)
    {
        String signature;
        String payload = createPayload(shortApi, param);
        signature = CryptAlgorithm.HmacSha256(payload.getBytes(), PrivateKey.getBytes());
        //signature = CryptAlgorithm.byte2hex(signature.getBytes());
        return signature;
    }

    private static String createPayload(String shortApi, String param)
    {
        String payload = "GET|";
        payload += shortApi;
        payload += "|";
        payload += "access_key=";
        payload += AccessKey;
        payload += "&";
        payload += param;
        payload += "&";

        long curMillis = System.currentTimeMillis();
        payload += "tonce=";
        payload += curMillis;

        return payload;
    }
}
