package com.springboot.drive.controller;

import com.springboot.drive.domain.dto.response.ResFileDTO;
import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.File;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.service.FileService;
import com.springboot.drive.service.FolderService;
import com.springboot.drive.ulti.error.InValidException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/folders/{folderId}/files")
public class FileController {
    private FolderService folderService;
    private FileService fileService;
    public FileController(FolderService folderService,FileService fileService) {
        this.folderService = folderService;
        this.fileService=fileService;
    }
    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getAllByFolder(
            @PathVariable("folderId") Long folderId
    ) throws InValidException {
        Folder folder=folderService.findById(folderId);
        if(folder==null){
            throw new InValidException(
                    "Folder with id " + folderId +" does not exist"
            );
        }
        return ResponseEntity.ok(fileService.getAll(folder));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResFileDTO> getById(
            @PathVariable("folderId") Long folderId,
            @PathVariable("id") Long id
    ) throws InValidException {
        Folder folder=folderService.findById(folderId);
        if(folder==null){
            throw new InValidException(
                    "Folder with id " + folderId +" does not exist"
            );
        }
        File file=fileService.findByIdAndParent(id,folder);
        if(file==null){
            throw new InValidException(
                    "File with id " + id +" does not exist in folder "+folder.getFolderName()
            );
        }

        return ResponseEntity.ok(new ResFileDTO(file));
    }
//    @PostMapping
//    public ResponseEntity<ResFileDTO> create(
//            @PathVariable("folderId") Long folderId,
//            @Valid @RequestBody File file
//    ) throws InValidException {
//        Folder folder=folderService.findById(folderId);
//        if(folder==null){
//            throw new InValidException(
//                    "Folder with id " + folderId +" does not exist"
//            );
//        }
//
//    }


}
