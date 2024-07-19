package com.springboot.drive.controller.user;

import com.springboot.drive.domain.dto.request.ReqRegisterDTO;
import com.springboot.drive.domain.dto.response.ResUserDTO;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.service.UploadService;
import com.springboot.drive.service.UserService;
import com.springboot.drive.ulti.anotation.ApiMessage;
import com.springboot.drive.ulti.error.InValidException;
import com.springboot.drive.ulti.error.StorageException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user/users")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UploadService uploadService;
    @Value("${upload-file.base-uri}")
    private String basePath;

    @Value("${upload-file.avatar-folder}")
    private String avatarFolder;
    public UserController(UserService userService, PasswordEncoder passwordEncoder, UploadService uploadService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.uploadService = uploadService;
    }

    @PostMapping("/confirm")
    @ApiMessage(value = "Confirm your account")
    public ResponseEntity<ResUserDTO> confirmUser(
            @Valid @RequestBody ReqRegisterDTO userDTO
    ) throws InValidException {
        User userDB = userService.findByEmail(userDTO.getEmail());
        if (userDB == null) {
            throw new InValidException(
                    "Email " + userDTO.getEmail() + " does not exists"
            );
        }
        userDB.setEnabled(true);
        return ResponseEntity.ok(new ResUserDTO(userService.save(userDB)));
    }

    @PostMapping("/forgot-password")
    @ApiMessage(value = "Confirm your account")
    public ResponseEntity<ResUserDTO> forgotPassword(
            @Valid @RequestBody ReqRegisterDTO userDTO
    ) throws InValidException {
        User userDB = userService.findByEmail(userDTO.getEmail());
        if (userDB == null) {
            throw new InValidException(
                    "Email " + userDTO.getEmail() + " does not exists"
            );
        }
        userDB.setEnabled(true);
        return ResponseEntity.ok(new ResUserDTO(userService.save(userDB)));
    }

    @PutMapping
    @ApiMessage(value = "Update a user")
    public ResponseEntity<ResUserDTO> update(
            @Valid @RequestBody ReqRegisterDTO user
    ) throws InValidException {
        User userDB = userService.findById(user.getId());
        if (userDB == null) {
            throw new InValidException(
                    "User with Id " + user.getId() + " does not exists"
            );}
        userDB.setName(user.getName());
        return ResponseEntity.ok( new ResUserDTO(userService.save(userDB)));
    }


    @PostMapping("/change-avatar")
    public ResponseEntity<ResUserDTO> changeAvatar(
            @PathVariable("id") Long id,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws InValidException, StorageException, URISyntaxException, IOException {
        User user = userService.findById(id);
        if (user == null) {
            throw new InValidException(
                    "User with id " + id + " does not exist"
            );
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));
        if (!isValid) {
            throw new StorageException(
                    "File " + fileName + " is not allowed"
            );
        }
        uploadService.createDirectory(basePath + avatarFolder);
        String fileStorage = uploadService.store(file, avatarFolder);

        if(user.getAvatar()!=null){
            uploadService.deleteFile(user.getAvatar(),avatarFolder);
        }
        user.setAvatar(fileStorage);

        return  ResponseEntity.ok(new ResUserDTO(userService.save(user)));

    }
    @PostMapping("/change-password")
    @ApiMessage(value = "Reset user password")
    public ResponseEntity<Void> resetPassword(
            @PathVariable("id") Long id,
            @RequestParam("newPassword") String newPassword
    ) throws InValidException {
        User user = userService.findById(id);
        if (user == null) {
            throw new InValidException("User with id " + id + " does not exist");
        }
        String hashPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashPassword);
        userService.save(user);
        return ResponseEntity.ok(null);
    }


}
