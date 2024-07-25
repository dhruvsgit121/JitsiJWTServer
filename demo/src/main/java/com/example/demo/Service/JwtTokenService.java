package com.example.demo.Service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.time.Instant;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

@Service
public class JwtTokenService {

    @Value("${jwt.secret}")
    private String appSecret;

    @Value("${jwt.appID}")
    private String appID;

    @Value("${jwt.jitsiDomain}")
    private String jitsiDomain;

    public Map<String, Object> generateJwtToken(String userName, String emailID) throws Exception {

        String randomUserID = generateRandomString();
        String randomRoomID = generateRandomString();

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", generateJWT(userName, emailID, randomUserID, randomRoomID));
        responseData.put("roomID", randomRoomID);

        return responseData;
    }

    public String generateJWT(String userName, String userEmailID,String userID, String roomID) throws Exception {

        long nbf = Instant.now().getEpochSecond(); // current timestamp
        long exp = Instant.now().plusSeconds(7200).getEpochSecond(); // 2 hour in the future

        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"context\":{\"user\":{\"moderator\":true,\"name\":\"" +
                userName +
                "\",\"id\":\"" +
                userID +
                "\",\"email\":\"" +
                userEmailID +
                "\"}},\"aud\":\"" +
                appID + ":" + roomID +
                "\",\"iss\":\"" +
                appID +
                "\",\"sub\":\"" +
                jitsiDomain +
                "\",\"room\":\"" +
                roomID +
                "\",\"nbf\":" +
                nbf +
                ",\"exp\":" +
                exp +
                "}";

        // Encode header and payload to base64url
        String base64UrlHeader = base64UrlEncode(header);
        String base64UrlPayload = base64UrlEncode(payload);

        // Concatenate base64url-encoded header and payload with a dot
        String dataToSign = base64UrlHeader + "." + base64UrlPayload;

        // Generate HMACSHA256 hash
        String hmacSha256 = calculateHmacSha256(dataToSign, appSecret);

        // Concatenate encoded header, payload, and HMAC to form JWT token
        String jwtToken = base64UrlHeader + "." + base64UrlPayload + "." + hmacSha256;

        return jwtToken;
    }


    private static String base64UrlEncode(String input) {
        return Base64.getUrlEncoder().encodeToString(input.getBytes());
    }

    private String calculateHmacSha256(String data, String key) throws Exception {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        sha256Hmac.init(secretKey);

        byte[] hmacBytes = sha256Hmac.doFinal(data.getBytes());

        return Base64.getUrlEncoder().encodeToString(hmacBytes);
    }


    public String generateRandomString() {
        int length = 20;
        boolean useLetters = true;
        boolean useNumbers = true;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
        return generatedString;
    }
}

