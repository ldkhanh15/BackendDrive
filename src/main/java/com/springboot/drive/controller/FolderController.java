package com.springboot.drive.controller;

import com.springboot.drive.domain.dto.request.ReqFolderDTO;
import com.springboot.drive.domain.dto.response.ResFolderDTO;
import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.service.FolderService;
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
@RequestMapping("/api/v1/folders")
public class FolderController {

    private final FolderService folderService;
    private final UserService userService;
    public FolderController(FolderService folderService,UserService userService) {
        this.folderService = folderService;
        this.userService = userService;
    }

    @GetMapping
    @ApiMessage(value = "Get all folder with paging")
    public ResponseEntity<ResultPaginationDTO> getAll(
            @Filter Specification<Folder> specification,
            Pageable pageable) {

        return ResponseEntity.ok(folderService.getAllFolderAndFile(specification, pageable));
    }


    @GetMapping("/{id}")
    @ApiMessage(value = "Get a folder")
    public ResponseEntity<ResFolderDTO> getDetail(
            @PathVariable("id") Long id
    ) throws InValidException {
        Folder folderDB = folderService.findById(id);
        if (folderDB == null || !folderDB.getIsEnabled()) {
            throw new InValidException(
                    "User with id " + id + " does not exist"
            );
        }

        return ResponseEntity.ok(new ResFolderDTO(folderDB));
    }

    @PostMapping
    @ApiMessage(value = "Create a new folder")
    public ResponseEntity<Folder> create(
            @Valid @RequestBody ReqFolderDTO folderDTO
    ) throws InValidException {
        Folder parent=folderService.findById(folderDTO.getParent().getId());
        if(parent==null &&  folderDTO.getParent().getId()!=0) {
            throw new InValidException(
                    "Folder with id " + folderDTO.getParent().getId()+" does not exist"
            );
        }
        Folder folderDB=folderService.findByNameAndRootFolder(folderDTO.getFolderName(),parent);

        if (folderDB != null) {
            throw new InValidException(
                    "Folder with name " + folderDTO.getFolderName() + " already exists"
            );
        }

        String email= (SecurityUtil.getCurrentUserLogin().isPresent()) ? SecurityUtil.getCurrentUserLogin().get() :
                null;
        User user=userService.findByEmail(email);
        if (user==null){
            throw new InValidException(
                    "User cannot create new folder"
            );
        }
        Folder folder=new Folder();
        folder.setUser(user);
        folder.setFolderName(folderDTO.getFolderName());
        folder.setIsEnabled(folderDTO.isEnabled());
        folder.setIsPublic(folderDTO.isPublic());
        folder.setParent(parent);
        return ResponseEntity.ok(folderService.save(folder));
    }

    @PutMapping
    @ApiMessage(value = "Update a folder")
    public ResponseEntity<Folder> update(
            @Valid @RequestBody Folder folder
    ) throws InValidException {
        Folder folderDB=folderService.findById(folder.getItemId());
        if (folderDB == null) {
            throw new InValidException(
                    "Folder with id" + folder.getItemId() + " does not exist"
            );
        }
        Folder folderName=folderService.findByNameAndRootFolder(folder.getFolderName(),folder.getParent());
        if (folderName != null) {
            throw new InValidException(
                    "Folder with name" + folder.getFolderName() + " already exists"
            );
        }
        folderDB.setFolderName(folder.getFolderName());
        folderDB.setIsPublic(folder.getIsPublic());
        folderDB.setIsPublic(folder.getIsPublic());
        return ResponseEntity.ok(folderService.save(folder));
    }

    @DeleteMapping("/{id}")
    @ApiMessage(value = "Soft delete a folder")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id
    ) throws InValidException {
        Folder folder=folderService.findById(id);
        if (folder == null) {
            throw new InValidException(
                    "Folder with id " + id + " does not exist"
            );
        }
        folderService.delete(folder);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/{id}")
    @ApiMessage(value = "Soft delete a folder")
    public ResponseEntity<Void> restore(
            @PathVariable("id") Long id
    ) throws InValidException {
        Folder folder=folderService.findById(id);
        if (folder == null) {
            throw new InValidException(
                    "Folder with id " + id + " does not exist"
            );
        }
        folderService.restore(folder);
        return ResponseEntity.ok(null);
    }
}
