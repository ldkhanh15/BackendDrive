package com.springboot.drive.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqFolderDTO {
    private String folderName;

    private FolderParent parent;
    private UserFolder user;
    private boolean enabled;
    private boolean isPublic;
    private String itemType;
    @Getter
    @Setter
    public static class FolderParent{
        private long id;
        private String folderName;
    }
    @Getter
    @Setter
    public static class UserFolder{
        private long id;
        private String name;
        private String email;
    }



}
