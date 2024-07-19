package com.springboot.drive.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqPassword {
    @NotBlank(message = "Old password must not be empty")
    private String oldPassword;
    @NotBlank(message = "New password must not be empty")
    private String newPassword;
}
