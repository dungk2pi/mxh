package com.social.demo.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @Email
    private String email;

    @Size(min = 6, max = 40)
    private String newPassword;

    private Integer token;
}
