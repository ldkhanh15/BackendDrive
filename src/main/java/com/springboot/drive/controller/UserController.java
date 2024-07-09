package com.springboot.drive.controller;

import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.dto.response.ResUserDTO;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.service.UploadService;
import com.springboot.drive.service.UserService;
import com.springboot.drive.ulti.anotation.ApiMessage;
import com.springboot.drive.ulti.error.InValidException;
import com.springboot.drive.ulti.error.StorageException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
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

    @GetMapping
    @ApiMessage(value = "Get all user with paging")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> specification,
            Pageable pageable) {

        return ResponseEntity.ok(userService.getAllUserPaging(specification, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage(value = "Get a new user")
    public ResponseEntity<ResUserDTO> getDetailUser(
            @PathVariable("id") Long id
    ) throws InValidException {
        User userDB = userService.findById(id);
        if (userDB == null || !userDB.isEnabled()) {
            throw new InValidException(
                    "User with id " + id + " does not exist"
            );
        }
        ResUserDTO resUserDTO = new ResUserDTO(userDB);
        return ResponseEntity.ok(resUserDTO);
    }

    @PostMapping
    @ApiMessage(value = "Create a new user")
    public ResponseEntity<ResUserDTO> createUser(
            @Valid @RequestBody User user
    ) throws InValidException {
        User userDB = userService.findByEmail(user.getEmail());
        if (userDB != null) {
            throw new InValidException(
                    "Email " + user.getEmail() + " already exists"
            );
        }
        String hashPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        user.setEnabled(true);
        ResUserDTO userDTO = new ResUserDTO(userService.save(user));
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping
    @ApiMessage(value = "Update a user")
    public ResponseEntity<ResUserDTO> update(
            @Valid @RequestBody User user
    ) throws InValidException {
        User userDB = userService.findById(user.getId());
        if (userDB == null) {
            throw new InValidException(
                    "User with Id " + user.getId() + " does not exists"
            );
        }
        userDB.setPassword(user.getPassword());
        userDB.setAvatar(user.getAvatar());

        ResUserDTO resUserDTO = new ResUserDTO(userService.save(userDB));

        return ResponseEntity.ok(resUserDTO);

    }

    @DeleteMapping("/{id}")
    @ApiMessage(value = "Soft delete a user")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id
    ) throws InValidException {
        User userDB = userService.findById(id);
        if (userDB == null) {
            throw new InValidException(
                    "User with id " + id + " does not exist"
            );
        }
        userService.delete(userDB);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/{id}/change-avatar")
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
    @PostMapping("/{id}/change-password")
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
    @PostMapping("/{id}/activate")
    @ApiMessage(value = "Activate user account")
    public ResponseEntity<Void> activateUser(
            @PathVariable("id") Long id
    ) throws InValidException {
        User user = userService.findById(id);
        if (user == null) {
            throw new InValidException("User with id " + id + " does not exist");
        }
        user.setEnabled(true);
        userService.save(user);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/{id}/deactivate")
    @ApiMessage(value = "Deactivate user account")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable("id") Long id
    ) throws InValidException {
        User user = userService.findById(id);
        if (user == null) {
            throw new InValidException("User with id " + id + " does not exist");
        }
        user.setEnabled(false);
        userService.save(user);
        return ResponseEntity.ok(null);
    }


    @PostMapping("/bulk-create")
    @ApiMessage(value = "Bulk create users")
    public ResponseEntity<List<ResUserDTO>> bulkCreateUsers(
            @RequestParam("file") MultipartFile file
    ) throws IOException, InValidException {
        List<User> users = userService.parseUsersFromFile(file);
        List<User> userDB=userService.saveAll(users);
        List<ResUserDTO> res=userDB.stream().map(ResUserDTO::new).toList();
        return ResponseEntity.ok(res);
    }

}
