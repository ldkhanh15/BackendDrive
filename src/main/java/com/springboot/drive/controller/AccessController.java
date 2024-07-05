package com.springboot.drive.controller;

import com.springboot.drive.domain.dto.response.ResAccessDTO;
import com.springboot.drive.domain.modal.AccessItem;
import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.service.AccessService;
import com.springboot.drive.service.ItemService;
import com.springboot.drive.service.UserService;
import com.springboot.drive.ulti.anotation.ApiMessage;
import com.springboot.drive.ulti.error.InValidException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/access-items")
public class AccessController {

    private AccessService accessService;
    private UserService userService;
    private ItemService itemService;
    public AccessController(AccessService accessService,UserService userService,ItemService itemService) {
        this.accessService = accessService;
        this.userService = userService;
        this.itemService=itemService;
    }

    @PostMapping
    @ApiMessage(value = "create access item")
    public ResponseEntity<ResAccessDTO> createAccess(
        @RequestBody ResAccessDTO accessDTO
    ) throws InValidException {
        User user = userService.findByEmail(accessDTO.getUser().getEmail());
        if (user == null){
            throw new InValidException(
                    "User with email " + accessDTO.getUser().getEmail()+" does not exist"
            );
        }
        Item item=itemService.findById(accessDTO.getItem().getId());
        if (item == null){
            throw new InValidException(
                    "Item with id " + accessDTO.getItem().getId()+" does not exist"
            );
        }
        AccessItem accessItem =new AccessItem();
        accessItem.setAccessType(accessDTO.getAccessType());
        accessItem.setUser(user);
        accessItem.setItem(item);

        return ResponseEntity.ok(new ResAccessDTO(accessService.save(accessItem)));
    }

    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete access item")
    public ResponseEntity<Void> deleteAccess(
            @PathVariable("id")Long id
    ) throws InValidException {
        AccessItem accessItem =accessService.findById(id);
        if(accessItem==null){
            throw new InValidException(
                    "Access with id " + id + " does not exist"
            );
        }
        accessService.delete(accessItem);
        return ResponseEntity.ok(null);
    }

}
