package com.springboot.drive.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqPermissionDTO {

    private long id;

    private String description;
    @NotBlank
    private String apiPath;
    @NotBlank
    private String method;
    @NotBlank
    private String module;
    @NotBlank
    private String name;

}
