package com.springboot.drive.controller;

import com.springboot.drive.domain.dto.request.ReqFolderDTO;
import com.springboot.drive.domain.dto.response.ResFolderDTO;
import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.*;
import com.springboot.drive.service.ActivityService;
import com.springboot.drive.service.FolderService;
import com.springboot.drive.service.ItemService;
import com.springboot.drive.service.UserService;
import com.springboot.drive.ulti.SecurityUtil;
import com.springboot.drive.ulti.anotation.ApiMessage;
import com.springboot.drive.ulti.anotation.FolderOwnerShip;
import com.springboot.drive.ulti.constant.AccessEnum;
import com.springboot.drive.ulti.constant.ItemTypeEnum;
import com.springboot.drive.ulti.error.InValidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/folders")
public class FolderController {

    private final FolderService folderService;
    private final UserService userService;
    private final ActivityService activityService;
    private final ItemService itemService;
    public FolderController(
            FolderService folderService,
            UserService userService,
            ActivityService activityService,
            ItemService itemService
    ) {
        this.folderService = folderService;
        this.userService = userService;
        this.activityService = activityService;
        this.itemService = itemService;
    }
    @Async
    protected void logActivity(Folder folder, AccessEnum accessType) {
        Activity activity = new Activity();
        if(folder.getParent()!=null){
            Activity parent=activityService.findByItemAndAccessType(folder.getParent(),AccessEnum.CREATE);
            if(parent!=null){
                activity.setParent(parent);
            }
        }
        activity.setItem(folder);
        activity.setActivityType(accessType);
        activityService.save(activity);
    }

    //ADMIN
    @GetMapping
    @ApiMessage(value = "Get all folder with paging")
    @FolderOwnerShip(action = AccessEnum.VIEW)
    public ResponseEntity<ResultPaginationDTO> getAllEnable(
            @Filter Specification<Folder> specification,
            Pageable pageable) {

        return ResponseEntity.ok(folderService.getAllFolderAndFile(specification, pageable,true,false));
    }
    @GetMapping("/enabled")
    @ApiMessage(value = "Get all folder with paging")
    @FolderOwnerShip(action = AccessEnum.VIEW)
    public ResponseEntity<ResultPaginationDTO> getAllDisabled(
            @Filter Specification<Folder> specification,
            Pageable pageable) {
        return ResponseEntity.ok(folderService.getAllFolderAndFile(specification, pageable,true,false));
    }


    @GetMapping("/deleted")
    @ApiMessage(value = "Get all deleted folders")
    @FolderOwnerShip(action = AccessEnum.DELETE)
    public ResponseEntity<ResultPaginationDTO> getAllDeleted(
            @Filter Specification<Folder> specification,
            Pageable pageable) {
        return ResponseEntity.ok(folderService.getAllFolderAndFile(specification, pageable, false,true));
    }
    @GetMapping("/disable")
    @ApiMessage(value = "Get all folder with paging")
    @FolderOwnerShip(action = AccessEnum.VIEW)
    public ResponseEntity<ResultPaginationDTO> getAllDisabledOfUser(
            @Filter Specification<Folder> specification,
            Pageable pageable) {
        return ResponseEntity.ok(folderService.getAllFolderAndFile(specification, pageable,false,false));
    }

    //CLIENT
    @GetMapping("/users")
    @FolderOwnerShip(action = AccessEnum.VIEW)
    @ApiMessage(value = "Get folder with access")
    public ResponseEntity<ResFolderDTO> getAll() throws InValidException {
        String email=SecurityUtil.getCurrentUserLogin().isPresent()?SecurityUtil.getCurrentUserLogin().get() : "";
        User user=userService.findByEmail(email);
        Folder folder=folderService.findByUserAndParent(user);
        return ResponseEntity.ok(new ResFolderDTO(folder));
    }
    @GetMapping("/trash")
    @ApiMessage(value = "Get all folder with paging")
    @FolderOwnerShip(action = AccessEnum.VIEW)
    public ResponseEntity<ResultPaginationDTO> getTrash(
            @Filter Specification<Folder> specification,
            Pageable pageable) {

        return ResponseEntity.ok(folderService.getAllFolderAndFile(specification, pageable,false,false));
    }

    //SHARED

    @GetMapping("/{id}/enabled")
    @ApiMessage(value = "Get a folder")
    @FolderOwnerShip(action = AccessEnum.VIEW)
    public ResponseEntity<ResFolderDTO> getDetailEnabled(
            @PathVariable("id") Long id
    ) throws InValidException {
        Folder folderDB = folderService.findByIdAndEnable(id,true,false);
        if (folderDB == null) {
            throw new InValidException(
                    "Folder with id " + id + " does not exist"
            );
        }
        ResFolderDTO resFolderDTO =new ResFolderDTO(folderDB);
        return ResponseEntity.ok(folderService.getFolderDetails(id,true,false,resFolderDTO));
    }
    @GetMapping("/{id}/disabled")
    @ApiMessage(value = "Get a folder")
    @FolderOwnerShip(action = AccessEnum.VIEW)
    public ResponseEntity<ResFolderDTO> getDetailDisabled(
            @PathVariable("id") Long id
    ) throws InValidException {
        Folder folderDB = folderService.findByIdAndEnable(id,true,false);
        if (folderDB == null) {
            throw new InValidException(
                    "Folder with id " + id + " does not exist"
            );
        }
        ResFolderDTO resFolderDTO =new ResFolderDTO(folderDB);
        return ResponseEntity.ok(folderService.getFolderDetails(id,false,false,resFolderDTO));
    }
    @GetMapping("/{id}/deleted")
    @ApiMessage(value = "Get a folder")
    @FolderOwnerShip(action = AccessEnum.VIEW)
    public ResponseEntity<ResFolderDTO> getDetailDeleted(
            @PathVariable("id") Long id
    ) throws InValidException {
        Folder folderDB = folderService.findByIdAndEnable(id,true,false);
        if (folderDB == null) {
            throw new InValidException(
                    "Folder with id " + id + " does not exist"
            );
        }
        ResFolderDTO resFolderDTO =new ResFolderDTO(folderDB);
        return ResponseEntity.ok(folderService.getFolderDetails(id,false,true,resFolderDTO));
    }

    @PostMapping
    @FolderOwnerShip(action = AccessEnum.CREATE)
    @ApiMessage(value = "Create a new folder")
    public ResponseEntity<ResFolderDTO> create(
            @Valid @RequestBody ReqFolderDTO folderDTO
    ) throws InValidException {
        Folder parent = folderService.findById(folderDTO.getParent().getId());
        if (parent == null && folderDTO.getParent().getId() != 0) {
            throw new InValidException(
                    "Folder with id " + folderDTO.getParent().getId() + " does not exist"
            );
        }


        String email = (SecurityUtil.getCurrentUserLogin().isPresent()) ? SecurityUtil.getCurrentUserLogin().get() :
                null;
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new InValidException(
                    "User cannot create new folder"
            );
        }
        Folder folder = new Folder();
        folder.setUser(user);
        folder.setFolderName(createName(parent,folderDTO.getFolderName()));
        folder.setIsEnabled(folderDTO.isEnabled());
        folder.setIsPublic(folderDTO.isPublic());
        folder.setParent(parent);
        folder.setItemType(ItemTypeEnum.FOLDER);
        folder.setIsDeleted(false);
        Folder folderSaved=folderService.save(folder);

        logActivity(folderSaved,AccessEnum.CREATE);

        return ResponseEntity.ok(new ResFolderDTO(folderSaved));
    }

    @PutMapping
    @ApiMessage(value = "Update a folder")
    @FolderOwnerShip(action = AccessEnum.UPDATE)
    public ResponseEntity<ResFolderDTO> update(
            @Valid @RequestBody ReqFolderDTO folder
    ) throws InValidException {
        Folder folderDB = folderService.findById(folder.getId());
        if (folderDB == null) {
            throw new InValidException(
                    "Folder with id " + folder.getId() + " does not exist"
            );
        }

        folderDB.setFolderName(createName(folderDB.getParent(),folder.getFolderName()));
        folderDB.setIsPublic(folder.isPublic());
        logActivity(folderDB,AccessEnum.UPDATE);

        return ResponseEntity.ok(new ResFolderDTO(folderService.save(folderDB)));
    }

    @DeleteMapping("/{id}/soft-delete")
    @ApiMessage(value = "Soft delete a folder")
    @FolderOwnerShip(action = AccessEnum.SOFT_DELETE)
    public ResponseEntity<Void> deleteSoft(
            @PathVariable("id") Long id
    ) throws InValidException {
        Folder folder = folderService.findByIdAndEnabled(id, true);
        if (folder == null) {
            throw new InValidException(
                    "Folder with id " + id + " does not exist"
            );
        }
        logActivity(folder,AccessEnum.SOFT_DELETE);

        folderService.deleteSoft(folder);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete a folder")
    @FolderOwnerShip(action = AccessEnum.DELETE)
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id
    ) throws InValidException, URISyntaxException {
        Folder folder = folderService.findByIdAndEnabled(id, false);
        if (folder == null) {
            throw new InValidException(
                    "Folder with id " + id + " does not exist"
            );
        }
        logActivity(folder,AccessEnum.DELETE);
        folderService.delete(folder);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/{id}/restore")
    @ApiMessage(value = "Restore a folder")
    @FolderOwnerShip(action = AccessEnum.DELETE)
    public ResponseEntity<ResFolderDTO> restore(
            @PathVariable("id") Long id
    ) throws InValidException {
        Folder folder = folderService.findByIdAndEnabled(id, false);
        if (folder == null) {
            throw new InValidException(
                    "Folder with id " + id + " does not exist"
            );
        }

        return ResponseEntity.ok(new ResFolderDTO(folderService.restore(folder)));
    }

    private String createName(Folder folder,String name){
        List<Folder> folders = folderService.findByNameInFolder(folder,name);

        int maxNumber = 0;
        for (Folder existingFolder : folders) {
            String existingName = existingFolder.getFolderName();
            if (existingName.equals(name)) {
                maxNumber = Math.max(maxNumber, 1);
            } else if (existingName.startsWith(name + " (") && existingName.endsWith(")")) {
                try {
                    int number = Integer.parseInt(existingName.substring(name.length() + 2, existingName.length() - 1));
                    maxNumber = Math.max(maxNumber, number);
                } catch (NumberFormatException e) {
                    // Ignore this exception as it's not a valid numbered folder name
                }
            }
        }

        if(maxNumber==0){
            return name;
        }
        return name + " (" + (maxNumber) + ")";
    }
}
