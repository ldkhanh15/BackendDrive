package com.springboot.drive.domain.dto.response;

import com.springboot.drive.domain.modal.File;
import com.springboot.drive.domain.modal.Folder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResFileDTO {

    private Long id;
    private String fileType;
    private String fileName;
    private Long fileSize;
    private String filePath;
    private Long downloadCount;
    private Long viewCount;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private UserFile user;
    private ParentFolder parent;
    public ResFileDTO(File file){
        id=file.getItemId();
        fileType=file.getFileType();
        fileName=file.getFileName();
        fileSize=file.getFileSize();
        filePath=file.getFilePath();
        downloadCount=file.getDownloadCount();
        viewCount=file.getViewCount();
        createdAt = file.getCreatedAt();
        createdBy = file.getCreatedBy();
        updatedAt = file.getUpdatedAt();
        updatedBy = file.getUpdatedBy();
        if(file.getUser()!=null){
            user=new UserFile();
            user.setId(file.getUser().getId());
            user.setName(file.getUser().getName());
            user.setEmail(file.getUser().getEmail());

        }
        if(file.getParent()!=null){
            parent=new ParentFolder(file.getParent());
        }
    }
    @Getter
    @Setter
    public static class UserFile {
        private Long id;
        private String name;
        private String email;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ParentFolder {
        private Long itemId;
        private Instant createdAt;
        private Instant updatedAt;
        private String createdBy;
        private String updatedBy;
        private Boolean isPublic;
        private Boolean isEnabled;
        private String folderName;
        public ParentFolder(Folder folder){
            itemId = folder.getItemId();
            isEnabled = folder.getIsEnabled();
            isPublic = folder.getIsPublic();
            folderName = folder.getFolderName();
            createdAt = folder.getCreatedAt();
            createdBy = folder.getCreatedBy();
            updatedAt = folder.getUpdatedAt();
            updatedBy = folder.getUpdatedBy();
        }
    }
}
