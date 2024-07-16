package com.springboot.drive.domain.dto.response;


import com.springboot.drive.domain.modal.Permission;
import com.springboot.drive.domain.modal.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResRoleDTO {
    private Long id;
    private String name;

    private String description;
    private Boolean active;

    private List<PermissionRole> permissions;
    public ResRoleDTO(Role role){
        this.id = role.getId();
        this.name = role.getName();
        this.description = role.getDescription();
        this.active = role.isActive();
        if(role.getPermissions()!=null){
            permissions=new ArrayList<>();
            permissions=role.getPermissions().stream().map(PermissionRole::new).toList();
        }
    }
    @Getter
    @Setter

    public static class PermissionRole{
        private Long id;
        private String description;
        private String apiPath;
        private String method;
        private String module;
        private String name;

        public PermissionRole(Permission permission){
            this.id = permission.getId();
            this.description = permission.getDescription();
            this.apiPath = permission.getApiPath();
            this.method = permission.getMethod();
            this.module = permission.getModule();
            this.name = permission.getName();
        }


    }
}
