package com.springboot.drive.controller.share;

import com.springboot.drive.domain.dto.request.ReqAccessDTO;
import com.springboot.drive.domain.dto.response.ResAccessDTO;
import com.springboot.drive.domain.dto.response.ResEmailDTO;
import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.AccessItem;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.service.AccessService;
import com.springboot.drive.service.EmailService;
import com.springboot.drive.service.ItemService;
import com.springboot.drive.service.UserService;
import com.springboot.drive.ulti.SecurityUtil;
import com.springboot.drive.ulti.anotation.ApiMessage;
import com.springboot.drive.ulti.anotation.ItemOwnerShip;
import com.springboot.drive.ulti.constant.AccessEnum;
import com.springboot.drive.ulti.error.InValidException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/access-items")
public class AccessController {

    private final AccessService accessService;
    private final UserService userService;
    private final ItemService itemService;
    private final EmailService emailService;

    public AccessController(
            AccessService accessService,
            UserService userService,
            ItemService itemService,
            EmailService emailService
    ) {
        this.accessService = accessService;
        this.userService = userService;
        this.itemService = itemService;
        this.emailService = emailService;
    }

    @GetMapping
    @ApiMessage(value = "Get all access of item")
    @ItemOwnerShip
    public ResponseEntity<ResultPaginationDTO> getAllAccessItems(
            @RequestParam("itemId") Long itemId,
            Pageable pageable
    ) throws InValidException {
        Item item = itemService.findById(itemId);
        if (item == null) {
            throw new InValidException(
                    "Item with id " + itemId + " does not exist"
            );
        }

        return ResponseEntity.ok(accessService.getAllAccessItemByItem(item, pageable));
    }

    @PostMapping
    @ApiMessage(value = "create access item")
    @ItemOwnerShip
    public ResponseEntity<ResAccessDTO> createAccess(
           @Valid @RequestBody ReqAccessDTO accessDTO
    ) throws InValidException {
        User user = userService.findByEmail(accessDTO.getUser().getEmail());
        if (user == null) {
            throw new InValidException(
                    "User with email " + accessDTO.getUser().getEmail() + " does not exist"
            );
        }
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email.equalsIgnoreCase(accessDTO.getUser().getEmail())) {
            throw new InValidException(
                    "You cannot add permissions for you"
            );
        }
        Item item = itemService.findById(accessDTO.getItem().getId());
        if (item == null) {
            throw new InValidException(
                    "Item with id " + accessDTO.getItem().getId() + " does not exist"
            );
        }
        AccessItem accessItem = accessService.findByItemAndUserAndAccessType(item, user, accessDTO.getAccessType());
        if (accessItem != null) {
            throw new InValidException(
                    "AccessItem already exists"
            );
        }
        AccessItem acc = new AccessItem();
        acc.setAccessType(accessDTO.getAccessType());
        acc.setUser(user);
        acc.setItem(item);
        if (accessDTO.getAccessType() != AccessEnum.VIEW && item instanceof Folder) {
            this.send((Folder) item, user);
        }
        return ResponseEntity.ok(new ResAccessDTO(accessService.save(acc)));
    }

    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete access item")
    @ItemOwnerShip
    public ResponseEntity<Void> deleteAccess(
            @PathVariable("id") Long id
    ) throws InValidException {
        AccessItem accessItem = accessService.findById(id);
        if (accessItem == null) {
            throw new InValidException(
                    "Access with id " + id + " does not exist"
            );
        }
        accessService.delete(accessItem);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping()
    @ItemOwnerShip
    @ApiMessage(value = "Delete access item")
    public ResponseEntity<Void> deleteAccessById(
            @RequestBody ReqAccessDTO accessDTO
    ) throws InValidException {
        User user = userService.findByEmail(accessDTO.getUser().getEmail());
        if (user == null) {
            throw new InValidException(
                    "User with email " + accessDTO.getUser().getEmail() + " does not exist"
            );
        }
        Item item = itemService.findById(accessDTO.getItem().getId());
        if (item == null) {
            throw new InValidException(
                    "Item with id " + accessDTO.getItem().getId() + " does not exist"
            );
        }
        AccessItem accessItem = accessService.findByItemAndUserAndAccessType(item, user, accessDTO.getAccessType());
        if (accessItem == null) {
            throw new InValidException(
                    "Access with email " + user.getEmail() + " to item " + item.getItemType() + " does not exist"
            );
        }
        accessService.delete(accessItem);
        return ResponseEntity.ok(null);
    }


    @Transactional
    public void send(Folder folder, User user) {
        ResEmailDTO res = new ResEmailDTO(folder, user);
        this.emailService.sendEmailFromTemplate(
                user.getEmail(),
                "Folder shared with you: " + folder.getFolderName(),
                "share",
                res
        );
    }
}
