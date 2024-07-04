package com.springboot.drive.domain.dto.response;

import com.springboot.drive.domain.modal.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResDTO {
    private long id;

    private String email;

    private String avatar;
    private boolean enabled;
    private double storageQuota;
    private RoleUser role;

    public UserResDTO(User user){
        id=user.getId();
        email=user.getEmail();
        avatar=user.getAvatar();
        enabled=user.isEnabled();
        storageQuota=user.getStorageQuota();
        if(user.getRole()!=null){
            role.setId(user.getRole().getId());
            role.setName(user.getRole().getName());
        }
    }

    public UserResDTO(){

    }
    @Getter
    @Setter
    public static class RoleUser{
        private long id;
        private String name;
    }
}
