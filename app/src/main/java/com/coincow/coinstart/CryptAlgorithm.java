package com.coincow.coinstart;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by lizuochuan on 24/08/2017.
 */

public class CryptAlgorithm {

    private final static String ALGORIGHM = "HmacSHA256";

    public static String HmacSha256(byte[] data, byte[] key)
    {
        try
        {
            SecretKeySpec signingKey = new SecretKeySpec(key, ALGORIGHM);
            Mac mac = Mac.getInstance(ALGORIGHM);
            mac.init(signingKey);
            return byte2hex(mac.doFinal(data));
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch(InvalidKeyException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static String byte2hex(byte[] b)
    {
        StringBuilder sb = new StringBuilder();
        String str;
        for(int i = 0; b != null && i < b.length; i++)
        {
            str = Integer.toHexString(b[i] & 0xff);
            if(str.length() == 1)
                sb.append('0');
            sb.append(str);
        }

        return sb.toString().toUpperCase();
    }
}
