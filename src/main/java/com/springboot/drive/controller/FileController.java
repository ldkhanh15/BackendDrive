package com.springboot.drive.controller;

import com.springboot.drive.domain.dto.response.ResFileDTO;
import com.springboot.drive.domain.dto.response.ResUploadFileDTO;
import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.File;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.service.FileService;
import com.springboot.drive.service.FolderService;
import com.springboot.drive.service.UploadService;
import com.springboot.drive.service.UserService;
import com.springboot.drive.ulti.SecurityUtil;
import com.springboot.drive.ulti.anotation.ApiMessage;
import com.springboot.drive.ulti.constant.ItemTypeEnum;
import com.springboot.drive.ulti.error.InValidException;
import com.springboot.drive.ulti.error.StorageException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/folders/{folderId}/files")
public class FileController {
    private FolderService folderService;
    private FileService fileService;

    private UserService userService;
    private UploadService uploadService;
    @Value("${upload-file.base-uri}")
    private String basePath;

    @Value("${upload-file.file-folder}")
    private String fileFolder;
    public FileController(
            FolderService folderService,
            FileService fileService,
            UploadService uploadService,
            UserService userService
    ) {
        this.folderService = folderService;
        this.fileService=fileService;
        this.uploadService = uploadService;
        this.userService=userService;
    }
    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getAllByFolderEnabled(
            @PathVariable("folderId") Long folderId
    ) throws InValidException {
        Folder folder=folderService.findById(folderId);
        if(folder==null){
            throw new InValidException(
                    "Folder with id " + folderId +" does not exist"
            );
        }
        return ResponseEntity.ok(fileService.getAll(folder,true));
    }
    @GetMapping("/trash")
    public ResponseEntity<ResultPaginationDTO> getAllDisabled(
            @PathVariable("folderId") Long folderId
    ) throws InValidException {
        Folder folder=folderService.findById(folderId);
        if(folder==null){
            throw new InValidException(
                    "Folder with id " + folderId +" does not exist"
            );
        }
        return ResponseEntity.ok(fileService.getAll(folder,false));
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
    @PostMapping
    @ApiMessage(value = "Upload a file")
    public ResponseEntity<ResFileDTO> create(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @PathVariable("folderId") Long folderId
    ) throws InValidException, StorageException, URISyntaxException, IOException {
        Folder folder=folderService.findById(folderId);
        if(folder==null){
            throw new InValidException(
                    "Folder with id " + folderId + " does not exist"
            );
        }
        if(file==null){
            throw new InValidException(
                    "You must be upload a file"
            );
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));
        if (!isValid) {
            throw new StorageException(
                    "File " + fileName + " is not allowed"
            );
        }
        uploadService.createDirectory(basePath + fileFolder);
        String fileStorage = uploadService.store(file, fileFolder);
        String email= SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User user=userService.findByEmail(email);
        File fileDB=new File();
        fileDB.setIsEnabled(true);
        fileDB.setIsPublic(true);
        fileDB.setFilePath(fileStorage);
        fileDB.setFileSize(file.getSize());
        fileDB.setFileName(fileName);
        fileDB.setFileType(file.getContentType());
        fileDB.setUser(user);
        fileDB.setParent(folder);
        fileDB.setItemType(ItemTypeEnum.FILE);
        fileDB.setViewCount(0L);
        fileDB.setDownloadCount(0L);

        File fileSaved=fileService.save(fileDB);
        ResFileDTO res=new ResFileDTO(fileSaved);
        return ResponseEntity.ok().body(res);
    }

    @PutMapping
    @ApiMessage(value = "Rename a file")
    public ResponseEntity<ResFileDTO> rename(
            @RequestBody File file
    ) throws InValidException {
        File fileDB=fileService.findById(file.getItemId());
        if(fileDB==null){
            throw new InValidException(
                    "File with id " + file.getItemId()+" does not exist"
            );
        }
        fileDB.setFileName(file.getFileName());
        return ResponseEntity.ok().body(new ResFileDTO(fileService.save(fileDB)));
    }
    @DeleteMapping("/{id}")
    @ApiMessage(value = "Soft delete a file")
    public ResponseEntity<Void> softDelete(
            @PathVariable Long id
    ) throws InValidException {
        File fileDB=fileService.findById(id);
        if(fileDB==null){
            throw new InValidException(
                    "File with id " + id+" does not exist"
            );
        }

        fileService.softDelete(fileDB);
        return ResponseEntity.ok(null);
    }
    @DeleteMapping("/restore/{id}")
    @ApiMessage(value = "Delete a file")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) throws InValidException, URISyntaxException {
        File fileDB=fileService.findById(id);
        if(fileDB==null){
            throw new InValidException(
                    "File with id " + id+" does not exist"
            );
        }
        if(!uploadService.fileExists(fileDB.getFilePath(), fileFolder)){
            throw new InValidException(
                    "File name "+fileDB.getFilePath()+" does not exist in "+fileFolder
            );
        }
        uploadService.deleteFile(fileDB.getFilePath(), fileFolder);

        fileService.delete(fileDB);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/restore/{id}")
    @ApiMessage(value = "Delete a file")
    public ResponseEntity<ResFileDTO> restore(
            @PathVariable Long id
    ) throws InValidException, URISyntaxException {
        File fileDB=fileService.findById(id);
        if(fileDB==null){
            throw new InValidException(
                    "File with id " + id+" does not exist"
            );
        }


        return ResponseEntity.ok(new ResFileDTO(fileService.restore(fileDB)));
    }

}
