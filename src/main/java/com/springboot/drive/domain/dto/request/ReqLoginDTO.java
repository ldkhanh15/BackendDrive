package com.springboot.drive.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqLoginDTO {
    @NotBlank(message = "Email khong duoc de trong")
    private String username;
    @NotBlank(message = "Password khong duoc de trong")
    private String password;

    public ReqLoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }


}
