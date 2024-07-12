package com.springboot.drive.controller;

import com.springboot.drive.domain.dto.request.ReqFileDTO;
import com.springboot.drive.domain.dto.response.ResFileDTO;
import com.springboot.drive.domain.dto.response.ResUploadFileDTO;
import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.*;
import com.springboot.drive.service.*;
import com.springboot.drive.ulti.SecurityUtil;
import com.springboot.drive.ulti.anotation.ApiMessage;
import com.springboot.drive.ulti.anotation.FileOwnerShip;
import com.springboot.drive.ulti.anotation.FolderOwnerShip;
import com.springboot.drive.ulti.constant.AccessEnum;
import com.springboot.drive.ulti.constant.ItemTypeEnum;
import com.springboot.drive.ulti.error.InValidException;
import com.springboot.drive.ulti.error.StorageException;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/folders/{folderId}/files")
public class FileController {
    private final FolderService folderService;
    private final FileService fileService;

    private final UserService userService;
    private final ActivityService activityService;
    private final UploadService uploadService;
    @Value("${upload-file.base-uri}")
    private String basePath;

    @Value("${upload-file.file-folder}")
    private String fileFolder;

    public FileController(
            FolderService folderService,
            FileService fileService,
            UploadService uploadService,
            UserService userService,
            ActivityService activityService
    ) {
        this.folderService = folderService;
        this.fileService = fileService;
        this.uploadService = uploadService;
        this.userService = userService;
        this.activityService = activityService;
    }

    @Async
    protected void logActivity(File file, AccessEnum accessType) {
        Activity activity = new Activity();
        if (file.getParent() != null) {
            Activity parent = activityService.findByItemAndAccessType(file.getParent(), AccessEnum.CREATE);
            if (parent != null) {
                activity.setParent(parent);
            }
        }
        activity.setItem(file);
        activity.setActivityType(accessType);
        activityService.save(activity);
    }

    //ADMIN
    @GetMapping
    @FolderOwnerShip(action = AccessEnum.VIEW)
    @ApiMessage(value = "Get Folder")
    public ResponseEntity<ResultPaginationDTO> getAllByFolderEnabled(
            @PathVariable("folderId") Long folderId,
            @Filter Specification<File> specification,
            Pageable pageable
    ) throws InValidException {
        Folder folder = folderService.findById(folderId);
        if (folder == null) {
            throw new InValidException(
                    "Folder with id " + folderId + " does not exist"
            );
        }
        return ResponseEntity.ok(fileService.getAll(specification,pageable,folder, true,false));
    }

    @GetMapping("/disabled")
    @ApiMessage(value = "Get all trash files")
    @FolderOwnerShip(action = AccessEnum.VIEW)
    public ResponseEntity<ResultPaginationDTO> getAllDisabled(
            @PathVariable("folderId") Long folderId,
            @Filter Specification<File> specification,
            Pageable pageable
    ) throws InValidException {
        Folder folder = folderService.findById(folderId);
        if (folder == null) {
            throw new InValidException(
                    "Folder with id " + folderId + " does not exist"
            );
        }
        return ResponseEntity.ok(fileService.getAll(specification,pageable,folder, false,false));
    }
    @GetMapping("/deleted")
    @ApiMessage(value = "Get all trash files")
    @FolderOwnerShip(action = AccessEnum.VIEW)
    public ResponseEntity<ResultPaginationDTO> getAllDeleted(
            @PathVariable("folderId") Long folderId,
            @Filter Specification<File> specification,
            Pageable pageable
    ) throws InValidException {
        Folder folder = folderService.findById(folderId);
        if (folder == null) {
            throw new InValidException(
                    "Folder with id " + folderId + " does not exist"
            );
        }
        return ResponseEntity.ok(fileService.getAll(specification,pageable,folder, false,true));
    }




    //SHARED
    @GetMapping("/{id}")
    @ApiMessage(value = "Get a file by id")
    @FolderOwnerShip(action = AccessEnum.VIEW)
    @FileOwnerShip(action = AccessEnum.VIEW)
    public ResponseEntity<ResFileDTO> getById(
            @PathVariable("folderId") Long folderId,
            @PathVariable("id") Long id
    ) throws InValidException {
        Folder folder = folderService.findById(folderId);
        if (folder == null) {
            throw new InValidException(
                    "Folder with id " + folderId + " does not exist"
            );
        }
        File file = fileService.findByIdAndParent(id, folder);
        if (file == null) {
            throw new InValidException(
                    "File with id " + id + " does not exist in folder " + folder.getFolderName()
            );
        }
        file.setViewCount(file.getViewCount() + 1);

        logActivity(file, AccessEnum.VIEW);

        return ResponseEntity.ok(new ResFileDTO(fileService.save(file)));
    }

    @PostMapping
    @ApiMessage(value = "Upload a file")
    @FolderOwnerShip(action=AccessEnum.CREATE)
    public ResponseEntity<ResFileDTO> create(
            @PathVariable("folderId") Long folderId,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws InValidException, StorageException, URISyntaxException, IOException {
        Folder folder = folderService.findById(folderId);
        if (folder == null) {
            throw new InValidException(
                    "Folder with id " + folderId + " does not exist"
            );
        }
        if (file == null) {
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
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User user = userService.findByEmail(email);
        File fileDB = new File();
        fileDB.setIsEnabled(true);
        fileDB.setIsPublic(true);
        fileDB.setIsDeleted(false);
        fileDB.setFilePath(fileStorage);
        fileDB.setFileSize(file.getSize());
        fileDB.setFileName(createName(folder,fileName));
        fileDB.setFileType(file.getContentType());
        fileDB.setUser(user);
        fileDB.setParent(folder);
        fileDB.setItemType(ItemTypeEnum.FILE);
        fileDB.setViewCount(0L);
        fileDB.setDownloadCount(0L);

        File fileSaved = fileService.save(fileDB);

        logActivity(fileSaved, AccessEnum.CREATE);

        ResFileDTO res = new ResFileDTO(fileSaved);
        return ResponseEntity.ok().body(res);
    }

    @PutMapping
    @ApiMessage(value = "Rename a file")
    @FileOwnerShip(action = AccessEnum.UPDATE)
    public ResponseEntity<ResFileDTO> rename(
            @PathVariable("id")Long folderId,
            @RequestBody ReqFileDTO file
    ) throws InValidException {
        File fileDB = fileService.findByIdAndEnabled(file.getId(), true);
        if (fileDB == null) {
            throw new InValidException(
                    "File with id " + file.getId() + " does not exist"
            );
        }
        fileDB.setFileName(file.getFileName());
        fileDB.setIsPublic(file.isPublic());


        logActivity(fileDB, AccessEnum.UPDATE);
        return ResponseEntity.ok().body(new ResFileDTO(fileService.save(fileDB)));
    }

    @DeleteMapping("/{id}/soft-delete")
    @ApiMessage(value = "Soft delete a file")
    @FolderOwnerShip(action=AccessEnum.SOFT_DELETE)
    @FileOwnerShip(action=AccessEnum.SOFT_DELETE)
    public ResponseEntity<Void> softDelete(
            @PathVariable("folderId")Long folderId,
            @PathVariable("id") Long id
    ) throws InValidException {
        File fileDB = fileService.findByIdAndEnabled(id, true);
        if (fileDB == null) {
            throw new InValidException(
                    "File with id " + id + " does not exist"
            );
        }
        fileService.softDelete(fileDB);

        logActivity(fileDB, AccessEnum.SOFT_DELETE);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete a file")
    @FolderOwnerShip(action=AccessEnum.DELETE)
    @FileOwnerShip(action=AccessEnum.DELETE)
    public ResponseEntity<Void> delete(
            @PathVariable("folderId")Long folderId,
            @PathVariable Long id
    ) throws InValidException, URISyntaxException {
        File fileDB = fileService.findByIdAndEnabled(id, false);
        if (fileDB == null) {
            throw new InValidException(
                    "File with id " + id + " does not exist"
            );
        }
        if (!uploadService.fileExists(fileDB.getFilePath(), fileFolder)) {
            throw new InValidException(
                    "File name " + fileDB.getFilePath() + " does not exist in " + fileFolder
            );
        }
        uploadService.deleteFile(fileDB.getFilePath(), fileFolder);

        fileService.delete(fileDB);

        logActivity(fileDB, AccessEnum.DELETE);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/{id}/restore")
    @ApiMessage(value = "Restore a file")
    @FolderOwnerShip(action=AccessEnum.UPDATE)
    @FileOwnerShip(action=AccessEnum.UPDATE)
    public ResponseEntity<ResFileDTO> restore(
            @PathVariable("folderId")Long folderId,
            @PathVariable("id") long id
    ) throws InValidException {
        File file = fileService.findByIdAndEnabled(id, false);
        if (file == null) {
            throw new InValidException(
                    "File with id " + id + " does not exist"
            );
        }

        return ResponseEntity.ok(new ResFileDTO(fileService.restore(file)));
    }

    @PostMapping("/{id}/download")
    @ApiMessage(value = "Download file")
    @FolderOwnerShip(action = AccessEnum.VIEW)
    @FileOwnerShip(action = AccessEnum.VIEW)
    public ResponseEntity<Resource> download(
            @PathVariable("folderId")Long folderId,
            @PathVariable("id") Long id
    ) throws StorageException, URISyntaxException, FileNotFoundException, InValidException {

        File file = fileService.findById(id);
        if(file == null) {
            throw new InValidException(
                    "File with id " + id + " does not exist"
            );
        }

        long fileLength = uploadService.getFileLength(file.getFilePath(), fileFolder);
        if (fileLength == 0) {
            throw new StorageException(
                    "File name: " + file.getFilePath() + " not found"
            );
        }
        InputStreamResource resource = uploadService.getResource(file.getFilePath(), fileFolder);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+file.getFilePath()+"\"")
                .contentLength(uploadService.getFileLength(file.getFilePath(), fileFolder))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

    }
    @PostMapping("/multipart")
    @ApiMessage("Upload multiple files")
    @FolderOwnerShip(action = AccessEnum.CREATE)
    public ResponseEntity<List<ResFileDTO>> upload(
            @PathVariable("folderId") Long folderId,
            @RequestParam(value = "files", required = false) MultipartFile[] files

    ) throws StorageException, InValidException {
        Folder folder = folderService.findById(folderId);
        if (folder == null) {
            throw new InValidException(
                    "Folder with id " + folderId + " does not exist"
            );
        }
        if (files == null || files.length == 0) {
            throw new StorageException("No files selected. Please choose files and try again!");
        }


        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        List<ResFileDTO> uploadedFiles = Arrays.stream(files)
                .map(file -> {
                    try {
                        if (file.isEmpty()) {
                            throw new StorageException("File is empty. Please choose a file and try again!");
                        }

                        String fileName = file.getOriginalFilename();
                        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));
                        if (!isValid) {
                            throw new StorageException(
                                    "File " + fileName + " is not allowed"
                            );
                        }
                        uploadService.createDirectory(basePath + fileFolder);
                        String fileStorage = uploadService.store(file, fileFolder);
                        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
                        User user = userService.findByEmail(email);
                        File fileDB = new File();
                        fileDB.setIsEnabled(true);
                        fileDB.setIsPublic(true);
                        fileDB.setIsDeleted(false);
                        fileDB.setFilePath(fileStorage);
                        fileDB.setFileSize(file.getSize());
                        fileDB.setFileName(createName(folder,fileName));
                        fileDB.setFileType(file.getContentType());
                        fileDB.setUser(user);
                        fileDB.setParent(folder);
                        fileDB.setItemType(ItemTypeEnum.FILE);
                        fileDB.setViewCount(0L);
                        fileDB.setDownloadCount(0L);

                        File fileSaved = fileService.save(fileDB);

                        logActivity(fileSaved, AccessEnum.CREATE);
                        return new ResFileDTO(fileSaved);

                    } catch (IOException | StorageException | URISyntaxException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(uploadedFiles);
    }
    private String createName(Folder folder,String name){
        List<File> files = fileService.findByNameInFolder(folder,name);

        if(files == null){
            return name;
        }
        return name+ " ("+(files.size()+1)+")";
    }
}
