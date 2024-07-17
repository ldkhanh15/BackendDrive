package com.springboot.drive.controller.user;

import com.springboot.drive.domain.dto.request.ReqFavouriteDTO;
import com.springboot.drive.domain.dto.response.ResFavouriteDTO;
import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.Favourite;
import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.service.FavouriteService;
import com.springboot.drive.service.ItemService;
import com.springboot.drive.service.UserService;
import com.springboot.drive.ulti.SecurityUtil;
import com.springboot.drive.ulti.anotation.ApiMessage;
import com.springboot.drive.ulti.error.InValidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/favourites")
public class FavouriteController {
    private final UserService userService;
    private final FavouriteService favouriteService;
    private final ItemService itemService;
    public FavouriteController(
            UserService userService,
            FavouriteService favouriteService,
            ItemService itemService
    ){
        this.userService = userService;
        this.favouriteService =favouriteService;
        this.itemService = itemService;
    }
    @GetMapping
    @ApiMessage(value = "Get all favourite items of user")
    public ResponseEntity<ResultPaginationDTO> getFavouriteOfUser(
            @Filter Specification<Favourite> specification,
            Pageable pageable
    ) throws InValidException {
        String email=SecurityUtil.getCurrentUserLogin().isPresent()?SecurityUtil.getCurrentUserLogin().get() : "";
        User user=userService.findByEmail(email);
        if(user == null){
            throw new InValidException(
                    "User with email " + email +" does not exist"
            );
        }
        return ResponseEntity.ok(favouriteService.getAllOfUser(user.getId(),specification,pageable));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResultPaginationDTO> getAllOfUser(
            @PathVariable("id")Long userId,
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
    @ApiMessage(value = "Add item to favorites")
    public ResponseEntity<ResFavouriteDTO> create(
            @Valid@RequestBody ReqFavouriteDTO favourite
    ) throws InValidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()?SecurityUtil.getCurrentUserLogin().get():"";
        User user=userService.findByEmail(email);
        Item item=itemService.findById(favourite.getItem().getId());
        if(item==null){
            throw new InValidException(
                    "Item with id "+favourite.getItem().getId()+" does not exist"
            );
        }
        Favourite favouriteDB=favouriteService.findByUserAndItem(user,item);

        if(favouriteDB!=null){
            throw new InValidException(
                    "This item already exists in the favorites"
            );
        }
        Favourite f=new Favourite();
        f.setItem(item);
        f.setUser(user);
        return ResponseEntity.ok(new ResFavouriteDTO(favouriteService.save(f)));
    }
    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete item from favorites")
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
