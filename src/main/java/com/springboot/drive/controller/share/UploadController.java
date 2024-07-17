package com.springboot.drive.controller.share;

import com.springboot.drive.domain.dto.response.ResUploadFileDTO;
import com.springboot.drive.service.UploadService;
import com.springboot.drive.ulti.anotation.ApiMessage;
import com.springboot.drive.ulti.error.InValidException;
import com.springboot.drive.ulti.error.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/v1/upload")
public class UploadController {

    @Value("${upload-file.base-uri}")
    private String basePath;
    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUploadFileDTO> upload(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder
    ) throws URISyntaxException, IOException, StorageException {
        if (file == null || file.isEmpty()) {
            throw new StorageException(
                    "File is empty. Please choose a file and try again!"
            );
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName != null && fileName.toLowerCase().endsWith(item));
        if (!isValid) {
            throw new StorageException(
                    "File " + fileName + " is not allowed"
            );
        }
        uploadService.createDirectory(basePath + folder);
        String fileStorage = uploadService.store(file, folder);

        ResUploadFileDTO res = new ResUploadFileDTO(fileStorage, Instant.now());
        return ResponseEntity.ok().body(res);
    }

    @GetMapping
    @ApiMessage(value = "Download fle")
    public ResponseEntity<Resource> download(
            @RequestParam(name = "fileName", required = false) String fileName,
            @RequestParam(name = "folder", required = false) String folder
    ) throws StorageException, URISyntaxException, FileNotFoundException {
        if (fileName == null || folder == null) {
            throw new StorageException(
                    "Missing required parameter : fileName or folder"
            );
        }
        long fileLength = uploadService.getFileLength(fileName, folder);
        if (fileLength == 0) {
            throw new StorageException(
                    "File name: " + fileName + " not found"
            );
        }
        InputStreamResource resource = uploadService.getResource(fileName,folder);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+fileName+"\"")
                .contentLength(uploadService.getFileLength(fileName,folder))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

    }


    @PostMapping("/multipart")
    @ApiMessage("Upload multiple files")
    public ResponseEntity<List<ResUploadFileDTO>> upload(
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestParam("folder") String folder
    ) throws StorageException {
        if (files == null || files.length == 0) {
            throw new StorageException("No files selected. Please choose files and try again!");
        }

        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        List<ResUploadFileDTO> uploadedFiles = Arrays.stream(files)
                .map(file -> {
                    try {
                        if (file.isEmpty()) {
                            throw new StorageException("File is empty. Please choose a file and try again!");
                        }

                        String fileName = file.getOriginalFilename();
                        boolean isValid = allowedExtensions.stream()
                                .anyMatch(item -> fileName != null && fileName.toLowerCase().endsWith(item));

                        if (!isValid) {
                            throw new StorageException("File " + fileName + " is not allowed");
                        }

                        uploadService.createDirectory(basePath + folder);
                        String fileStorage = uploadService.store(file, folder);
                        return new ResUploadFileDTO(fileStorage, Instant.now());

                    } catch (IOException | StorageException | URISyntaxException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(uploadedFiles);
    }

    @DeleteMapping()
    @ApiMessage(value = "Delete a file")
    public ResponseEntity<Void> delete(
            @RequestParam("fileName") String fileName,
            @RequestParam("folder")String folder
    ) throws URISyntaxException, InValidException {
        if(!uploadService.fileExists(fileName, folder)){
            throw new InValidException(
                    "File name "+fileName+" does not exist in "+folder
            );
        }
        uploadService.deleteFile(fileName, folder);
        return ResponseEntity.ok(null);
    }
}
