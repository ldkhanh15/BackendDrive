package com.springboot.drive.controller;

import com.springboot.drive.domain.dto.request.ReqFolderDTO;
import com.springboot.drive.domain.dto.response.ResFolderDTO;
import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.Activity;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.service.ActivityService;
import com.springboot.drive.service.FolderService;
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

@RestController
@RequestMapping("/api/v1/folders")
public class FolderController {

    private final FolderService folderService;
    private final UserService userService;
    private final ActivityService activityService;

    public FolderController(
            FolderService folderService,
            UserService userService,
            ActivityService activityService
    ) {
        this.folderService = folderService;
        this.userService = userService;
        this.activityService = activityService;
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
    @GetMapping
    @ApiMessage(value = "Get all folder with paging")
    public ResponseEntity<ResultPaginationDTO> getAll(
            @Filter Specification<Folder> specification,
            Pageable pageable) {

        return ResponseEntity.ok(folderService.getAllFolderAndFile(specification, pageable));
    }


//    @GetMapping("/{id}")
//    @ApiMessage(value = "Get folder with access")
//    public ResponseEntity<ResultPaginationDTO> getAll(
//           @PathVariable("id")Long folderId
//           ) throws InValidException {
//        Folder folder=folderService.findById(folderId);
//        if (folder == null){
//            throw new InValidException(
//                    "Folder with id " + folderId+" does not exist"
//            );
//        }
//        String email=SecurityUtil.getCurrentUserLogin().isPresent()?SecurityUtil.getCurrentUserLogin().get() : "";
//        User user=userService.findByEmail(email);
//        return ResponseEntity.ok(folderService.getWithAccess(user.getId(), folderId));
//    }


    @GetMapping("/{id}")
    @ApiMessage(value = "Get a folder")

    public ResponseEntity<ResFolderDTO> getDetail(
            @PathVariable("id") Long id
    ) throws InValidException {
        Folder folderDB = folderService.findById(id);
        if (folderDB == null) {
            throw new InValidException(
                    "Folder with id " + id + " does not exist"
            );
        }
        logActivity(folderDB,AccessEnum.VIEW);
        return ResponseEntity.ok(new ResFolderDTO(folderDB));
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
        Folder folderDB = folderService.findByNameAndRootFolder(folderDTO.getFolderName(), parent);

        if (folderDB != null) {
            throw new InValidException(
                    "Folder with name " + folderDTO.getFolderName() + " already exists"
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
        folder.setFolderName(folderDTO.getFolderName());
        folder.setIsEnabled(folderDTO.isEnabled());
        folder.setIsPublic(folderDTO.isPublic());
        folder.setParent(parent);
        folder.setItemType(ItemTypeEnum.FOLDER);
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
        Folder folderName = folderService.findByNameAndRootFolder(folder.getFolderName(), folderDB.getParent());
        if (folderName != null && folderName.getItemId() == folderDB.getItemId()) {
            throw new InValidException(
                    "Folder with name" + folder.getFolderName() + " already exists"
            );
        }
        folderDB.setFolderName(folder.getFolderName());
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

    @GetMapping("/{id}/restore")
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


}
