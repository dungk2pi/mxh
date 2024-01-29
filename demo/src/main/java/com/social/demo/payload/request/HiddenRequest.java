package com.social.demo.payload.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HiddenRequest {
    @NotBlank
    String id;
}
