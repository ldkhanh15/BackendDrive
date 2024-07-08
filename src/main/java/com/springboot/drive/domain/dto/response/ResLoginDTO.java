package com.springboot.drive.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springboot.drive.domain.modal.Role;
import com.springboot.drive.domain.modal.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResLoginDTO {

    @JsonProperty("access_token")
    private String accessToken;
    private UserLogin user;

    public ResLoginDTO() {
        user = new UserLogin();
    }

    public ResLoginDTO(User userDB) {
        if (userDB != null) {
            user = new UserLogin();
            user.setId(userDB.getId());
            user.setName(userDB.getName());
            user.setEmail(userDB.getEmail());
            if (userDB.getRole() != null) {
                user.setRole(userDB.getRole());
            }
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLogin {
        private long id;
        private String email;
        private String name;
        private Role role;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserGetAccount {
        private UserLogin user;

        public UserGetAccount(User userDB) {
            if (userDB != null) {
                user = new UserLogin();
                user.setId(userDB.getId());
                user.setName(userDB.getName());
                user.setEmail(userDB.getEmail());
                if (userDB.getRole() != null) {
                    user.setRole(userDB.getRole());
                }
            }
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInsideToken {
        private long id;
        private String email;
        private String name;
    }
}
