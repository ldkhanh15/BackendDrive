package com.springboot.drive.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUserDTO {
    private Long id;
    @NotBlank(message = "Email khong duoc de trong")
    private String email;
    @NotBlank(message = "Password khong duoc de trong")
    private String password;

    @NotBlank(message = "Name khong duoc de trong")
    private String name;

    private RoleUser role;

    @Getter
    @Setter
    public static class RoleUser{
        private Long id;
    }

}
