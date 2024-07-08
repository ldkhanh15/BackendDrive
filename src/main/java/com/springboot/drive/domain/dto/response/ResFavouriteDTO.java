package com.springboot.drive.domain.dto.response;

import com.springboot.drive.domain.modal.Favourite;
import com.springboot.drive.domain.modal.File;
import com.springboot.drive.domain.modal.Folder;
import lombok.Getter;
import lombok.Setter;


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
                ResFolderDTO res = new ResFolderDTO((Folder) favourite.getItem());
                item = res;
            } else if (favourite.getItem() instanceof File) {
                ResFileDTO res = new ResFileDTO((File) favourite.getItem());
                item = res;
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


}
