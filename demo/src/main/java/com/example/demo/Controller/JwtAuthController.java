package com.example.demo.Controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Collections;

@RestController
public class JwtAuthController {

//    private JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();

    @GetMapping("/jwt-auth")
    public ResponseEntity<?> authenticate(@RequestParam String token) {

        System.out.println("API Cakled");
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey("BFA26C645791DFF128F3EF12517980E")
                    .parseClaimsJws(token)
                    .getBody();

            System.out.println("sysyetme buk");

            String role = claims.get("role", String.class);
            return ResponseEntity.ok(Collections.singletonMap("role", role));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}
