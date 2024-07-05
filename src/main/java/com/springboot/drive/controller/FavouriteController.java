package com.springboot.drive.controller;

import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.Favourite;
import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.service.FavouriteService;
import com.springboot.drive.service.ItemService;
import com.springboot.drive.service.UserService;
import com.springboot.drive.ulti.SecurityUtil;
import com.springboot.drive.ulti.error.InValidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/favourites")
public class FavouriteController {
    private UserService userService;
    private FavouriteService favouriteService;
    private ItemService itemService;
    public FavouriteController(
            UserService userService,
            FavouriteService favouriteService,
            ItemService itemService
    ){
        this.userService = userService;
        this.favouriteService =favouriteService;
        this.itemService = itemService;
    }
    @GetMapping("/{userId}")
    public ResponseEntity<ResultPaginationDTO> getAllOfUser(
            @PathVariable("userId")Long userId,
            @Filter Specification<Favourite> specification,
            Pageable pageable
    ) throws InValidException {
        User user =userService.findById(userId);
        if(user == null){
            throw new InValidException(
                    "User with id " + userId +" does not exist"
            );
        }
        return ResponseEntity.ok(favouriteService.getAllOfUser(userId,specification,pageable));
    }
    @PostMapping()
    public ResponseEntity<Favourite> create(
            @Valid@RequestBody Favourite favourite
    ) throws InValidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()?SecurityUtil.getCurrentUserLogin().get():"";
        User user=userService.findByEmail(email);
        Item item=itemService.findById(favourite.getItem().getItemId());
        Favourite favouriteDB=favouriteService.findByUserAndItem(user,item);

        if(favouriteDB!=null){
            throw new InValidException(
                    "This item already exists in the favorites"
            );
        }

        return ResponseEntity.ok(favouriteService.save(favourite));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id")Long id
    ) throws InValidException {
        Favourite favourite=favouriteService.findById(id);
        if(favourite==null){
            throw new InValidException(
                    "Favourite with id " + id + " does not exist"
            );
        }

        favouriteService.delete(favourite);
        return ResponseEntity.ok(null);
    }
}
