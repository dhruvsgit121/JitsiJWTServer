package com.example.demo.Service;

//import io.jsonwebtoken.JwtBuilder;

import com.example.demo.Utilities.JitsiJWTUtilities;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
//import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
//import java.util.Base64;
//import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//import java.time.Instant;
//import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
////import io.jsonwebtoken.security.Keys;Keys
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.util.Date;


@Service
public class JwtTokenService {

    @Value("${jwt.secret}")
    private String appSecret;

    @Value("${jwt.appID}")
    private String appID;

    @Value("${jwt.jitsiDomain}")
    private String jitsiDomain;

    @Value("${jwt.expirationOffSet}")
    private int expirationOffset;

    @Value("${jwt.jitsiFullDomain}")
    private String jitsiFullDomain;

    @Value("${jwt.configSettings}")
    private String jitsiConfigSettings;


//    public String generateDoctorJWTToken(String userName, String userEmail) {
//
//        String randomUserID = generateRandomString();
//        String randomRoomID = generateRandomString();
//
//
//        return "";
//    }


    public Map<String, Object> generateUserJWTTokenData(String doctorName, String doctorEmail, String patientName, String patientEmail) {

        String randomDoctorUserID = JitsiJWTUtilities.generateRandomString(10);
        String randomPatientUserID = JitsiJWTUtilities.generateRandomString(10);
        String randomRoomID = JitsiJWTUtilities.generateRandomString(10);
        String audienceID = appID + ":" + randomRoomID;

        Map<String, Object> userTokenData = new HashMap<>();

        String doctorJWTToken = generateDoctorJWTToken(doctorName, doctorEmail, randomDoctorUserID, randomRoomID, audienceID);
        userTokenData.put("doctorJWTToken", doctorJWTToken);
        userTokenData.put("roomID", randomRoomID);
        String doctorVideoConferencingURL = jitsiFullDomain + randomRoomID + "?jwt=" + doctorJWTToken + jitsiConfigSettings;
        userTokenData.put("doctorVideoConferencingURL", doctorVideoConferencingURL);

        String patientJWTToken = generatePatientJWTToken(patientName, patientEmail, randomPatientUserID, randomRoomID, audienceID);
        userTokenData.put("patientJWTToken", patientJWTToken);

        String patientVideoConferencingURL = jitsiFullDomain + randomRoomID + "?jwt=" + patientJWTToken + jitsiConfigSettings;
        userTokenData.put("patientVideoConferencingURL", patientVideoConferencingURL);

        return userTokenData;
    }


    public String generateDoctorJWTToken(String userName, String userEmailID, String userID, String roomID, String audienceID) {
        return generateJWTToken(userName, userEmailID, userID, roomID, audienceID, true);
    }

    public String generatePatientJWTToken(String userName, String userEmailID, String userID, String roomID, String audienceID) {
        return generateJWTToken(userName, userEmailID, userID, roomID, audienceID, false);
    }

    public String generateJWTToken(String userName, String userEmailID, String userID, String roomID, String audienceID, Boolean isModerator) {

        Map<String, Object> claims = getUserClaims(audienceID, roomID, isModerator);
        claims.put("context", createContext(userName, userID, userEmailID, isModerator));

        byte[] apiKeySecretBytes = appSecret.getBytes(StandardCharsets.UTF_8);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, "HMACSHA256");

        // Use JwtBuilder to construct a JWT token
        String token = Jwts.builder()
                .setClaims(claims).claim("role", "participant")
                .signWith(SignatureAlgorithm.HS256, signingKey).setHeaderParam("typ", "JWT")
                .compact();
        return token;
    }


    public Map<String, Object> getUserClaims(String audienceID, String roomID, boolean isModerator) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("aud", audienceID);
        claims.put("iss", appID);
        claims.put("sub", jitsiDomain);
        claims.put("room", roomID); // Room ID for the Jitsi conference
        claims.put("nbf", JitsiJWTUtilities.getCurrentTimeStamp()); // Not Before time in seconds
        claims.put("exp", JitsiJWTUtilities.getExpirationTimeStamp(expirationOffset)); // Expiry time in milliseconds (1 hour)
//        "moderator": true
        claims.put("moderator", isModerator);
        claims.put("role", isModerator ? "moderator" : "participant");
        return claims;
    }


    private Map<String, Object> createContext(String username, String userID, String userEmail, Boolean isModerator) {
        Map<String, Object> context = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("moderator", isModerator);
        user.put("name", username);
        user.put("id", userID);
        user.put("email", userEmail);
        user.put("affiliation", isModerator ? "owner" : "member");
//        "affiliation": "owner"
        context.put("user", user);
//        context.put("group", isModerator ? "moderator" : "viewer");
//        roles: ['moderator']  // Assign the moderator role

        ArrayList<String> roles = new ArrayList<>();
        roles.add(isModerator ? "moderator" : "participant");

        context.put("affiliation", isModerator ? "owner" : "member");
        context.put("role", roles);
        return context;
    }

//    public Map<String, Object> generateJwtToken(String userName, String emailID) throws Exception {
//
//        String randomUserID = generateRandomString();
//        String randomRoomID = generateRandomString();
//
//        Map<String, Object> responseData = new HashMap<>();
//        responseData.put("token", generateJWT(userName, emailID, randomUserID, randomRoomID));
//        responseData.put("roomID", randomRoomID);
//
//        return responseData;
//    }

//    public String generateJWT(String userName, String userEmailID, String userID, String roomID) throws Exception {
//
//        long nbf = Instant.now().getEpochSecond(); // current timestamp
//        long exp = Instant.now().plusSeconds(7200).getEpochSecond(); // 2 hour in the future
//
//        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
//        String payload = "{\"context\":{\"user\":{\"moderator\":true,\"name\":\"" +
//                userName +
//                "\",\"id\":\"" +
//                userID +
//                "\",\"email\":\"" +
//                userEmailID +
//                "\"}},\"aud\":\"" +
//                appID + ":" + roomID +
//                "\",\"iss\":\"" +
//                appID +
//                "\",\"sub\":\"" +
//                jitsiDomain +
//                "\",\"room\":\"" +
//                roomID +
//                "\",\"nbf\":" +
//                nbf +
//                ",\"exp\":" +
//                exp +
//                "}";
//
//        System.out.println("Payload data is " + payload);
//
//        // Encode header and payload to base64url
//        String base64UrlHeader = base64UrlEncode(header);
//        String base64UrlPayload = base64UrlEncode(payload);
//
//        // Concatenate base64url-encoded header and payload with a dot
//        String dataToSign = base64UrlHeader + "." + base64UrlPayload;
//
//        // Generate HMACSHA256 hash
//        String hmacSha256 = calculateHmacSha256(dataToSign, appSecret);
//
//        // Concatenate encoded header, payload, and HMAC to form JWT token
//        String jwtToken = base64UrlHeader + "." + base64UrlPayload + "." + hmacSha256;
//
//        return jwtToken;
//    }


//    private static String base64UrlEncode(String input) {
//        return Base64.getUrlEncoder().encodeToString(input.getBytes());
//    }
//
//    private String calculateHmacSha256(String data, String key) throws Exception {
//        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
//        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
//        sha256Hmac.init(secretKey);
//
//        byte[] hmacBytes = sha256Hmac.doFinal(data.getBytes());
//
//        return Base64.getUrlEncoder().encodeToString(hmacBytes);
//    }


//    public String generateRandomString() {
//        int length = 20;
//        boolean useLetters = true;
//        boolean useNumbers = true;
//        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
//        return generatedString;
//    }
}

