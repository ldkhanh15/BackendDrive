package com.springboot.drive.domain.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReqRoleDTO {
    private Long id;
    @NotBlank
    private String name;

    @NotBlank
    private String description;
    @NotNull
    private Boolean active;

    private List<PermissionRole> permissions;
    @Getter
    @Setter
    public static class PermissionRole{
        private Long id;
    }
}
