package com.jukshio.JioRPSLlib.Networking;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.InvalidKeyException;
/**
 * This is helper class used to generate jwt token.
 * **/
public class JwtGenerator {
    /**
     *This method is to generate jwt token with appid as parameter.
     * Classes uses jsonwebtoken dependency here
     * **/
    public static String getToken(String appId, String secretKey){
        String jws="";
        JSONObject header = new JSONObject();
        JSONObject body = new JSONObject();
        Long timeStamp1 = System.currentTimeMillis();
        long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(timeStamp1);

        String timeStamp = String.valueOf(timeSeconds);
//        Log.e("timeStamp1",timeStamp);

        try {
            header.put("alg",HS256);
            body.put("iss",appId);
            body.put("iat",timeStamp);

            String header1 = String.valueOf(header);
            String payload = String.valueOf(body);

            String encodedHeader = Encoders.BASE64URL.encode(header1.getBytes());
            String encodedPayload =  Encoders.BASE64URL.encode(payload.getBytes());

            String concatenated = encodedHeader + '.' + encodedPayload;

            byte[] signature = hmacSha256( concatenated, secretKey);
            jws = concatenated + '.' + Encoders.BASE64URL.encode(signature);
//            Log.e("jwtToken",jws);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jws;
    }

    private static byte[] hmacSha256(String message, String key) {
        try {
            final String hashingAlgorithm = "HmacSHA256"; //or "HmacSHA1", "HmacSHA512"
            byte[] bytes = hmac(hashingAlgorithm, key.getBytes(), message.getBytes());
            final String messageDigest = bytesToHex(bytes);
//            Log.d("Jukshio", "message digest: " + messageDigest);

            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static byte[] hmac(String algorithm, byte[] key, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(algorithm);
        try {
            mac.init(new SecretKeySpec(key, algorithm));
        } catch (java.security.InvalidKeyException e) {
            e.printStackTrace();
        }
        return mac.doFinal(message);
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0, v; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}