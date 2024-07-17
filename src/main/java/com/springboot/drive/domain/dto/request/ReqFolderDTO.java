package com.springboot.drive.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqFolderDTO {
    private long id;
    @NotBlank
    private String folderName;
    private FolderParent parent;
    private boolean isEnabled;
    private boolean isPublic;
    private String itemType;
    @Getter
    @Setter
    public static class FolderParent{
        private long id;
        private String folderName;
    }



}
