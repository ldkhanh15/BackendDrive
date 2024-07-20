package com.springboot.drive.controller.admin;

import com.springboot.drive.domain.dto.request.ReqFolderDTO;
import com.springboot.drive.domain.dto.response.ResFolderDTO;
import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.*;
import com.springboot.drive.service.ActivityService;
import com.springboot.drive.service.FolderService;
import com.springboot.drive.service.ItemService;
import com.springboot.drive.service.UserService;
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
@RequestMapping("/api/v1/admin/folders")
public class FolderAdminController {

    private final FolderService folderService;
    private final UserService userService;
    private final ActivityService activityService;
    private final ItemService itemService;

    public FolderAdminController(
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
        if (folder.getParent() != null) {
            Activity parent = activityService.findByItemAndAccessType(folder.getParent(), AccessEnum.CREATE);
            if (parent != null) {
                activity.setParent(parent);
            }
        }
        activity.setItem(folder);
        activity.setActivityType(accessType);
        activityService.save(activity);
    }

    @GetMapping("/enabled")
    @ApiMessage(value = "Get all folder with paging")
    public ResponseEntity<ResultPaginationDTO> getAllEnabled(
            @Filter Specification<Folder> specification,
            Pageable pageable,
            @RequestParam("query") String query
    ) {
        return ResponseEntity.ok(folderService.getAllFolderRoot(specification, pageable, true, false, query));
    }


    @GetMapping("/deleted")
    @ApiMessage(value = "Get all deleted folders")
    public ResponseEntity<ResultPaginationDTO> getAllDeleted(
            @Filter Specification<Folder> specification,
            Pageable pageable,
            @RequestParam("query") String query
    ) {
        return ResponseEntity.ok(folderService.getAllFolderRoot(specification, pageable, false, true, query));
    }

    @GetMapping("/disabled")
    @ApiMessage(value = "Get all folder with paging")
    public ResponseEntity<ResultPaginationDTO> getAllDisabled(
            @Filter Specification<Folder> specification,
            Pageable pageable,
            @RequestParam("query") String query
    ) {
        return ResponseEntity.ok(folderService.getAllFolderRoot(specification, pageable, false, false, query));
    }

    @GetMapping("/{id}/enabled")
    @ApiMessage(value = "Get a folder")
    public ResponseEntity<ResFolderDTO> getDetailEnabled(
            @PathVariable("id") Long id
    ) throws InValidException {
        Folder folderDB = folderService.findByIdAndEnableAndDeleted(id, true, false);
        if (folderDB == null) {
            throw new InValidException(
                    "Folder with id " + id + " does not exist"
            );
        }
        ResFolderDTO resFolderDTO = new ResFolderDTO(folderDB);
        return ResponseEntity.ok(folderService.getFolderDetails(id, true, false, resFolderDTO));
    }

    @GetMapping("/{id}/disabled")
    @ApiMessage(value = "Get a folder")
    public ResponseEntity<ResFolderDTO> getDetailDisabled(
            @PathVariable("id") Long id
    ) throws InValidException {
        Folder folderDB = folderService.findByIdAndEnableAndDeleted(id, true, false);
        if (folderDB == null) {
            throw new InValidException(
                    "Folder with id " + id + " does not exist"
            );
        }
        ResFolderDTO resFolderDTO = new ResFolderDTO(folderDB);
        return ResponseEntity.ok(folderService.getFolderDetails(id, false, false, resFolderDTO));
    }

    @GetMapping("/{id}/deleted")
    @ApiMessage(value = "Get a folder")
    public ResponseEntity<ResFolderDTO> getDetailDeleted(
            @PathVariable("id") Long id
    ) throws InValidException {
        Folder folderDB = folderService.findByIdAndEnableAndDeleted(id, true, false);
        if (folderDB == null) {
            throw new InValidException(
                    "Folder with id " + id + " does not exist"
            );
        }
        ResFolderDTO resFolderDTO = new ResFolderDTO(folderDB);
        return ResponseEntity.ok(folderService.getFolderDetails(id, false, true, resFolderDTO));
    }


    @PostMapping("/{folderId}")
    @ApiMessage(value = "Create a new folder")
    @FolderOwnerShip(action = AccessEnum.CREATE)
    public ResponseEntity<ResFolderDTO> create(
            @PathVariable("folderId")Long folderId,
            @Valid @RequestBody ReqFolderDTO folderDTO
    ) throws InValidException {
        Folder parent = folderService.findById(folderDTO.getParent().getId());
        if (parent == null && folderDTO.getParent().getId() != 0) {
            throw new InValidException(
                    "Folder with id " + folderDTO.getParent().getId() + " does not exist"
            );
        }
        Folder folder = new Folder();
        folder.setUser(parent.getUser());
        folder.setFolderName(createName(parent, folderDTO.getFolderName()));
        folder.setIsEnabled(folderDTO.isEnabled());
        folder.setIsPublic(folderDTO.isPublic());
        folder.setParent(parent);
        folder.setItemType(ItemTypeEnum.FOLDER);
        folder.setIsDeleted(false);
        Folder folderSaved = folderService.save(folder);
        logActivity(folderSaved, AccessEnum.CREATE);
        return ResponseEntity.ok(new ResFolderDTO(folderSaved));
    }

    @PutMapping("/{folderId}")
    @ApiMessage(value = "Update a folder")
    @FolderOwnerShip(action = AccessEnum.UPDATE)
    public ResponseEntity<ResFolderDTO> update(
            @PathVariable("folderId") Long folderId,
            @Valid @RequestBody ReqFolderDTO folder
    ) throws InValidException {
        Folder folderDB = folderService.findById(folder.getId());
        if (folderDB == null) {
            throw new InValidException(
                    "Folder with id " + folder.getId() + " does not exist"
            );
        }
        folderDB.setFolderName(createName(folderDB.getParent(), folder.getFolderName()));
        folderDB.setIsPublic(folder.isPublic());
        logActivity(folderDB, AccessEnum.UPDATE);

        return ResponseEntity.ok(new ResFolderDTO(folderService.save(folderDB)));
    }

    @DeleteMapping("/{id}/soft-delete")
    @ApiMessage(value = "Soft delete a folder")
    public ResponseEntity<Void> deleteSoft(
            @PathVariable("id") Long id
    ) throws InValidException {
        Folder folder = folderService.findByIdAndEnableAndDeleted(id, true, false);
        if (folder == null) {
            throw new InValidException(
                    "Folder with id " + id + " does not exist"
            );
        }
        folderService.deleteSoft(folder);
        logActivity(folder, AccessEnum.SOFT_DELETE);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete a folder")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id
    ) throws InValidException, URISyntaxException {
        Folder folder = folderService.findByIdAndEnableAndDeleted(id, false, false);
        if (folder == null) {
            throw new InValidException(
                    "Folder with id " + id + " does not exist"
            );
        }
        folderService.delete(folder);
        logActivity(folder, AccessEnum.DELETE);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/{id}/restore")
    @ApiMessage(value = "Restore a folder")
    public ResponseEntity<ResFolderDTO> restore(
            @PathVariable("id") Long id
    ) throws InValidException {
        Folder folder = folderService.findByIdAndEnableAndDeleted(id, false, false);
        if (folder == null) {
            throw new InValidException(
                    "Folder with id " + id + " does not exist"
            );
        }

        return ResponseEntity.ok(new ResFolderDTO(folderService.restore(folder)));
    }

    private String createName(Folder folder, String name) {
        List<Folder> folders = folderService.findByNameInFolder(folder, name);

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

        if (maxNumber == 0) {
            return name;
        }
        return name + " (" + (maxNumber) + ")";
    }
}
