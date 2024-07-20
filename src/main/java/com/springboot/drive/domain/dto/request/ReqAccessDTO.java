package com.springboot.drive.domain.dto.request;

import com.springboot.drive.ulti.anotation.ValidEnum;
import com.springboot.drive.ulti.constant.AccessEnum;
import com.springboot.drive.ulti.constant.ItemTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqAccessDTO {
    @NotNull
    @ValidEnum(enumClass = AccessEnum.class, message = "Invalid access type")
    private AccessEnum accessType;
    @NotNull
    private UserAccess user;
    @NotNull
    private ItemAccess item;

    @Getter
    @Setter
    public static class UserAccess {
        private long id;
        private String email;
        private String name;
    }

    @Getter
    @Setter
    public static class ItemAccess {
        private long id;
    }
}
