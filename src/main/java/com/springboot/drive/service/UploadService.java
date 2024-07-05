package com.springboot.drive.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class UploadService {

    @Value("${upload-file.base-uri}")
    private String basePath;

    public void createDirectory(String folder) throws URISyntaxException {
        URI uri = new URI(folder);
        Path path = Paths.get(uri);
        File tmpDir = new File(path.toString());
        if (!tmpDir.isDirectory()) {
            try {
                Files.createDirectory(tmpDir.toPath());
                System.out.println(">>>Create new directory, path= " + folder);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println(">>>>Folder is exist, path= " + folder);
        }

    }

    public String store(MultipartFile file, String folder) throws IOException, URISyntaxException {
        String finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        URI uri = new URI(basePath + folder + "/" + finalName);
        Path path = Paths.get(uri);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }
        return finalName;
    }

    public long getFileLength(String fileName,String folder) throws URISyntaxException {
        URI uri=new URI(basePath+folder + "/" + fileName);
        Path path = Paths.get(uri);
        File tmpDir=new File(path.toString());

        if(!tmpDir.exists()||tmpDir.isDirectory()){
            return 0;
        }
        return tmpDir.length();
    }

    public InputStreamResource getResource(String fileName,String folder) throws FileNotFoundException, URISyntaxException {
        URI uri = new URI(basePath+folder + "/" + fileName);
        Path path = Paths.get(uri);
        File file=new File(path.toString());
        return new InputStreamResource(new FileInputStream(file));
    }
    public void deleteFile(String fileName, String folder) throws URISyntaxException {
        URI uri = new URI(basePath + folder + "/" + fileName);
        Path path = Paths.get(uri);
        try {
            Files.deleteIfExists(path);
            System.out.println(">>>Deleted file, path= " + path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + fileName, e);
        }
    }
    public void renameFile(String oldFileName, String newFileName, String folder) throws URISyntaxException {
        URI oldUri = new URI(basePath + folder + "/" + oldFileName);
        URI newUri = new URI(basePath + folder + "/" + newFileName);
        Path oldPath = Paths.get(oldUri);
        Path newPath = Paths.get(newUri);
        try {
            Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println(">>>Renamed file from " + oldFileName + " to " + newFileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to rename file: " + oldFileName, e);
        }
    }
    public void moveFile(String fileName, String oldFolder, String newFolder) throws URISyntaxException {
        URI oldUri = new URI(basePath + oldFolder + "/" + fileName);
        URI newUri = new URI(basePath + newFolder + "/" + fileName);
        Path oldPath = Paths.get(oldUri);
        Path newPath = Paths.get(newUri);
        try {
            Files.createDirectories(newPath.getParent());
            Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println(">>>Moved file from " + oldFolder + " to " + newFolder);
        } catch (IOException e) {
            throw new RuntimeException("Failed to move file: " + fileName, e);
        }
    }
    public boolean fileExists(String fileName, String folder) throws URISyntaxException {
        URI uri = new URI(basePath + folder + "/" + fileName);
        Path path = Paths.get(uri);
        return Files.exists(path) && !Files.isDirectory(path);
    }

}
