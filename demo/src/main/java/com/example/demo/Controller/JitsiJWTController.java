package com.example.demo.Controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class JitsiJWTController {

    @GetMapping("/gettoken")
    public String getJWTToken() {
        return "Hello World!!!";
    }

}
