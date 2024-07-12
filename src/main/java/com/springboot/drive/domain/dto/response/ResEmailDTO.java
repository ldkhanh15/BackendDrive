package com.springboot.drive.domain.dto.response;

import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.domain.modal.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResEmailDTO {
    public String emailTo;
    public String nameOwner;
    public String emailOwner;
    public String folderName;

    public Long folderId;
    public String avatar;

    public ResEmailDTO(Folder folder, User user){
        emailTo=user.getEmail();
        nameOwner=folder.getUser().getName();
        emailOwner=folder.getUser().getEmail();
        folderName=folder.getFolderName();
        folderId=folder.getItemId();
        avatar=folder.getUser().getAvatar();
    }
}
