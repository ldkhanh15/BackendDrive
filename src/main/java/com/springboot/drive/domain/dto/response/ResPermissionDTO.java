package com.springboot.drive.domain.dto.response;

import com.springboot.drive.domain.modal.Permission;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResPermissionDTO {
    private long id;
    private String description;
    private String apiPath;
    private String method;
    private String module;
    private String name;

    public ResPermissionDTO(Permission permission){
        this.id = permission.getId();
        this.description = permission.getDescription();
        this.apiPath = permission.getApiPath();
        this.method = permission.getMethod();
        this.module = permission.getModule();
        this.name = permission.getName();
    }
}
