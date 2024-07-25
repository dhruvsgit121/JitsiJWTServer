package com.example.demo.POJO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserData {

    @NotBlank(message = "Username is required")
    public String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @NotNull(message = "Email can not be null.")
    public String email;

}
