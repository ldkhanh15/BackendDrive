package com.springboot.drive.domain.dto.response;

import com.springboot.drive.domain.modal.AccessItem;
import com.springboot.drive.ulti.constant.AccessEnum;
import com.springboot.drive.ulti.constant.ItemTypeEnum;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ResAccessDTO {
    private long id;
    private AccessEnum accessType;
    private UserAccess user;
    private ItemAccess item;

    public ResAccessDTO(AccessItem accessItem) {
        id = accessItem.getId();
        accessType = accessItem.getAccessType();
        if (accessItem.getUser() != null) {
            user = new UserAccess();
            user.setId(accessItem.getUser().getId());
            user.setName(accessItem.getUser().getName());
            user.setEmail(accessItem.getUser().getEmail());
        }
        if (accessItem.getItem() != null) {
            item = new ItemAccess();
            item.setId(accessItem.getItem().getItemId());
            item.setItemType(accessItem.getItem().getItemType());
        }
    }

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
