package com.example.demo.Controller;

import com.example.demo.POJO.UserData;
import com.example.demo.Service.JwtTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class JitsiJWTController {

    @Autowired
    private JwtTokenService jwtTokenService;

    @PostMapping("/generatejwttoken")
    public ResponseEntity<?> generateJitsiJWTToken(@Valid @RequestBody UserData userData, BindingResult result) throws  Exception {
        // Handle the postRequest object here

        if (result.hasErrors()) {
            // Map to store validation errors
            Map<String, String> validationErrors = new HashMap<>();
            result.getFieldErrors().forEach(fieldError -> {
                validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(validationErrors);
        }

        Map<String, Object> userTokenData = jwtTokenService.generateUserJWTTokenData(userData.doctorname, userData.doctoremail, userData.patientname, userData.patientemail);
        Map<String, Object> response = new HashMap<>();
        response.putAll(userTokenData);
        response.put("timestamp", LocalDateTime.now());

        // Process the validated User object (save to database, etc.)
        // For demonstration purposes, just returning the saved user object
        return ResponseEntity.ok(response);

    }
}
