package com.springboot.drive.domain.dto.request;
import com.springboot.drive.ulti.anotation.ValidEnum;
import com.springboot.drive.ulti.constant.AccessEnum;
import com.springboot.drive.ulti.constant.ItemTypeEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqAccessDTO {
    @NotBlank
    @ValidEnum(enumClass = AccessEnum.class, message = "Invalid access type")
    private AccessEnum accessType;
    @NotBlank
    private UserAccess user;
    @NotBlank
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
        private ItemTypeEnum itemType;

    }
}
