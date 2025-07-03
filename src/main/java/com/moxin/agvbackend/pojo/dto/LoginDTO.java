package com.moxin.agvbackend.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class LoginDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String captcha;

    @NotBlank
    private String uuid;
}
