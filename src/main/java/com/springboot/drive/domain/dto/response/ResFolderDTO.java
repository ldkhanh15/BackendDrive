package com.springboot.drive.domain.dto.response;

import com.springboot.drive.domain.modal.File;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.ulti.constant.ItemTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ResFolderDTO {
    private Long itemId;
    private ItemTypeEnum itemType;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private Boolean isPublic;
    private Boolean isEnabled;
    private String folderName;
    private Boolean isDeleted;
    List<FileFolder> files;
    List<SubFolder> subFolders;
    UserFolder user;
    SubFolder parent;

    public ResFolderDTO() {
        files = new ArrayList<>();
        subFolders = new ArrayList<>();
    }
    public void setFolderRoot(Folder folder) {
        if (folder != null) {
            itemId = folder.getItemId();
            itemType = folder.getItemType();
            isEnabled = folder.getIsEnabled();
            isPublic = folder.getIsPublic();
            folderName = folder.getFolderName();
            createdAt = folder.getCreatedAt();
            createdBy = folder.getCreatedBy();
            updatedAt = folder.getUpdatedAt();
            updatedBy = folder.getUpdatedBy();
            isDeleted = folder.getIsDeleted();
            if (folder.getUser() != null) {
                user = new UserFolder();
                user.setId(folder.getUser().getId());
                user.setName(folder.getUser().getName());
                user.setEmail(folder.getUser().getEmail());
            }
            if (folder.getParent() != null) {
                parent = new SubFolder();
                parent.setItemId(folder.getParent().getItemId());
                parent.setFolderName(folder.getParent().getFolderName());
                parent.setIsPublic(folder.getParent().getIsPublic());
                parent.setIsEnabled(folder.getParent().getIsEnabled());
                parent.setCreatedAt(folder.getParent().getCreatedAt());
                parent.setUpdatedAt(folder.getParent().getUpdatedAt());
                parent.setUpdatedBy(folder.getParent().getUpdatedBy());
                parent.setUpdatedAt(folder.getParent().getUpdatedAt());
            }
        }

    }

    public ResFolderDTO(Folder folder) {

        if (folder != null) {
            itemId = folder.getItemId();
            itemType = folder.getItemType();
            isEnabled = folder.getIsEnabled();
            isPublic = folder.getIsPublic();
            folderName = folder.getFolderName();
            createdAt = folder.getCreatedAt();
            createdBy = folder.getCreatedBy();
            updatedAt = folder.getUpdatedAt();
            updatedBy = folder.getUpdatedBy();
            isDeleted = folder.getIsDeleted();
            if (folder.getUser() != null) {
                user = new UserFolder();
                user.setId(folder.getUser().getId());
                user.setName(folder.getUser().getName());
                user.setEmail(folder.getUser().getEmail());
            }
            if (folder.getFiles() != null && !folder.getFiles().isEmpty()) {
                files = folder.getFiles().stream()
                        .map(FileFolder::new)
                        .collect(Collectors.toList());
            }
            if (folder.getSubFolders() != null && !folder.getSubFolders().isEmpty()) {
                subFolders = folder.getSubFolders().stream()
                        .map(SubFolder::new)
                        .collect(Collectors.toList());
            }
            if (folder.getParent() != null) {
                parent = new SubFolder();
                parent.setItemId(folder.getParent().getItemId());
                parent.setFolderName(folder.getParent().getFolderName());
                parent.setIsPublic(folder.getParent().getIsPublic());
                parent.setIsEnabled(folder.getParent().getIsEnabled());
                parent.setCreatedAt(folder.getParent().getCreatedAt());
                parent.setUpdatedAt(folder.getParent().getUpdatedAt());
                parent.setUpdatedBy(folder.getParent().getUpdatedBy());
                parent.setUpdatedAt(folder.getParent().getUpdatedAt());
            }
        }

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileFolder {
        private Long itemId;
        private ItemTypeEnum itemType;
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
        private Boolean isPublic;
        private Boolean isEnabled;
        private Boolean isDeleted;

        public FileFolder(File file) {
            itemId = file.getItemId();
            fileType = file.getFileType();
            fileName = file.getFileName();
            fileSize = file.getFileSize();
            filePath = file.getFilePath();
            downloadCount = file.getDownloadCount();
            viewCount = file.getViewCount();
            createdAt = file.getCreatedAt();
            createdBy = file.getCreatedBy();
            updatedAt = file.getUpdatedAt();
            updatedBy = file.getUpdatedBy();
            isEnabled = file.getIsEnabled();
            isPublic = file.getIsPublic();
            isDeleted = file.getIsDeleted();
            itemType = file.getItemType();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubFolder {
        private Long itemId;
        private Instant createdAt;
        private Instant updatedAt;
        private String createdBy;
        private String updatedBy;
        private Boolean isPublic;
        private Boolean isEnabled;
        private String folderName;
        private Boolean isDeleted;
        private ItemTypeEnum itemType;

        public SubFolder(Folder folder) {
            itemId = folder.getItemId();
            isEnabled = folder.getIsEnabled();
            isPublic = folder.getIsPublic();
            folderName = folder.getFolderName();
            createdAt = folder.getCreatedAt();
            createdBy = folder.getCreatedBy();
            updatedAt = folder.getUpdatedAt();
            updatedBy = folder.getUpdatedBy();
            isDeleted = folder.getIsDeleted();
            itemType = folder.getItemType();
        }
    }

    @Getter
    @Setter
    public static class UserFolder {
        private Long id;
        private String name;
        private String email;
    }
}
