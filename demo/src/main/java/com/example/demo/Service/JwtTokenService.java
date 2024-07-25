package com.example.demo.Service;


//import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
//import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.time.Instant;
import javax.crypto.*;
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

//    private String secret = appSecret; // Replace with your actual secret key

    public String generateToken() {
//        Map<String, Object> claims = new HashMap<>();
//
//        // Example payload
//        claims.put("moderator", false);
//        claims.put("name", "rdhrukqdjklf");
//        claims.put("id", "google-oauth2|113sqdmklfjklf478484782959569367141400");
//        claims.put("email", "dhrusqmnadnfaf,v.sgit@gmail.com");
//
//        // Example JWT token validity
//        Date now = new Date();
//        Date validity = new Date(now.getTime() + 3600 * 1000); // 1 hour
//
//        Map<String, Object> context = new HashMap<>();
//        context.put("id", "google-oauth2|113sqdmklfjklf478484782959569367141400");
//        claims.put("context", context);


//        {
//            "context": {
//            "user": {
//                "moderator": true,
//                        "name": "rahulljagger",
//                        "id": "google-oauth2|113482959569367141400",
//                        "email": "dhruv.sgit@gmail.com"
//            }
//        },
//            "aud": "62DD457D4003A243CF38E1C887053600:demo110",
//                "iss": "62DD457D4003A243CF38E1C887053600",
//                "sub": "jitsiehrc1.publicvm.com",
//                "room": "demo110",
//                "nbf": 1721763000,
//                "exp": 17308016273
//        }


        String randomUserID = generateRandomString();
        String randomRoomID = generateRandomString();

        String audience = appID + ":" + randomRoomID;

        String username = "dhruvgdjdkjd";
        String userEmail = "dhruv.jdsjfk@gmail.com";

        //String username, String userID, String userEmail, Boolean isM

        long nbf = Instant.now().getEpochSecond(); // current timestamp
        long exp = Instant.now().plusSeconds(7200).getEpochSecond(); // 2 hour in the future


        Map<String, Object> claims = new HashMap<>();
        claims.put("context", createContext(username, randomUserID, userEmail, true));
        claims.put("aud", audience);
        claims.put("iss", appID);
        claims.put("sub", jitsiDomain);
        claims.put("room", randomRoomID); // Room ID for the Jitsi conference
        claims.put("nbf", nbf); // Not Before time in seconds
        claims.put("exp", exp); // Expiry time in milliseconds (1 hour)


//        SecretKey key = Keys.hmacShaKeyFor(appSecret.getBytes());
//
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(now)
//                .setExpiration(expiryDate)
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();


        byte[] apiKeySecretBytes = appSecret.getBytes(StandardCharsets.UTF_8);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, "HMACSHA256");

        // Use JwtBuilder to construct a JWT token
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signingKey).setHeaderParam("typ", "JWT")
                .compact();
        return token;
//
//        JwtBuilder builder = Jwts.builder()
//                .setClaims(claims)
//                .signWith(SignatureAlgorithm.HS256, appSecret).setHeaderParam("typ", "JWT"); // Signing the JWT with the secret key
//
//        return builder.compact();

//        JwtBuilder builder = Jwts.builder()
//                .setClaims(claims).setHeaderParam("alg", "HS256").setHeaderParam("typ", "JWT")
//                .signWith(SignatureAlgorithm.HS256, appSecret);

////        {
////            "typ": "JWT",
////                "alg": "HS256"
//        }

//        return builder.compact();

//        return Jwts.builder()
//                .setClaims(claims).setHeaderParam("typ", "JWT")  // Setting header parameters
//                .setHeaderParam("alg", "HS256")
////                .setAudience("62DD457D4003A243CF38E1C887053600:demo8")
////                .setIssuer("62DD457D4003A243CF38E1C887053600")
////                .setSubject("jitsiehrc1.publicvm.com")
////                .setIssuedAt(now)
////                .setExpiration(validity)
//                .signWith(SignatureAlgorithm.HS256, appSecret)
//                .compact();
    }


    private Map<String, Object> createContext(String username, String userID, String userEmail, Boolean isModerator) {
        Map<String, Object> context = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("moderator", isModerator);
        user.put("name", username);
        user.put("id", userID);
        user.put("email", userEmail);
        context.put("user", user);
        return context;
    }

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

        System.out.println("Payload data is " + payload);

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

