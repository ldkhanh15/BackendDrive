package com.springboot.drive.domain.dto.request;

import com.springboot.drive.ulti.constant.AccessEnum;
import com.springboot.drive.ulti.constant.ItemTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqActivityDTO {

    private AccessEnum activityType;
    private ParentActivity parent;
    private ItemActivity item;
    @Getter
    @Setter
    public static class ParentActivity{
        private long id;
        private AccessEnum activityType;
    }
    @Getter
    @Setter
    public static class ItemActivity{
        private long id;
        private ItemTypeEnum itemType;
    }
}
