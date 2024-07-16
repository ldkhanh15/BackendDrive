package com.springboot.drive.domain.dto.response;

import com.springboot.drive.domain.modal.Favourite;
import com.springboot.drive.domain.modal.File;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.ulti.constant.ItemTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
public class ResFavouriteDTO {
    private long id;
    private UserFavourite user;
    private Object item;

    public ResFavouriteDTO(Favourite favourite) {
        id = favourite.getId();
        if (favourite.getUser() != null) {
            user = new UserFavourite();
            user.setId(favourite.getUser().getId());
            user.setEmail(favourite.getUser().getEmail());
            user.setName(favourite.getUser().getName());
        }
        if (favourite.getItem() != null) {
            if (favourite.getItem() instanceof Folder) {
                item = new ResFolderFavouritesDTO((Folder) favourite.getItem());
            } else if (favourite.getItem() instanceof File) {
                item = new ResFileFavouriteDTO((File) favourite.getItem());
            }
        }


    }

    @Getter
    @Setter
    public static class UserFavourite {
        private long id;
        private String name;
        private String email;
    }

    @Getter
    @Setter
    public static class ResFolderFavouritesDTO {
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
        private UserFolderFavourite user;
        public ResFolderFavouritesDTO(Folder folder) {

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
                    user= new UserFolderFavourite();
                    user.setId(folder.getUser().getId());
                    user.setName(folder.getUser().getName());
                    user.setEmail(folder.getUser().getEmail());
                }
            }

        }
    }
    @Getter
    @Setter
    public static class UserFolderFavourite {
        private Long id;
        private String name;
        private String email;
    }
    @Getter
    @Setter
    public static class ResFileFavouriteDTO {

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
        private Boolean isPublic;
        private Boolean isEnabled;
        private Boolean isDeleted;
        private UserFileFavourite user;

        public ResFileFavouriteDTO(File file) {
            if(file!=null){
                id = file.getItemId();
                fileType = file.getFileType();
                fileName = file.getFileName();
                fileSize = file.getFileSize();
                filePath = file.getFilePath();
                downloadCount = file.getDownloadCount();
                viewCount = file.getViewCount();
                isEnabled = file.getIsEnabled();
                isPublic = file.getIsPublic();
                isDeleted = file.getIsDeleted();
                createdAt = file.getCreatedAt();
                createdBy = file.getCreatedBy();
                updatedAt = file.getUpdatedAt();
                updatedBy = file.getUpdatedBy();
                if(file.getUser()!=null){
                    user=new UserFileFavourite();
                    user.setId(file.getUser().getId());
                    user.setName(file.getUser().getName());
                    user.setEmail(file.getUser().getEmail());

                }
            }
        }
    }

    @Getter
    @Setter
    public static class UserFileFavourite {
        private Long id;
        private String name;
        private String email;
    }
}
