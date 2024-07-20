package com.springboot.drive.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqChangeUserDTO {
    @NotBlank
    private String name;
}
