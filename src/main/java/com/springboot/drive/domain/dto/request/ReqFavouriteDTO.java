package com.springboot.drive.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqFavouriteDTO {
    private ItemFavourite item;

    @Getter
    @Setter
    public static class ItemFavourite{
        private long id;
    }
}
