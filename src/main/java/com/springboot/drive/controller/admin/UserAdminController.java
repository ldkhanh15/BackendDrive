package com.springboot.drive.controller.admin;

import com.springboot.drive.domain.dto.request.ReqPassword;
import com.springboot.drive.domain.dto.request.ReqUserDTO;
import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.dto.response.ResUserDTO;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.domain.modal.Role;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.service.FolderService;
import com.springboot.drive.service.RoleService;
import com.springboot.drive.service.UploadService;
import com.springboot.drive.service.UserService;
import com.springboot.drive.ulti.anotation.ApiMessage;
import com.springboot.drive.ulti.constant.ItemTypeEnum;
import com.springboot.drive.ulti.error.InValidException;
import com.springboot.drive.ulti.error.StorageException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
public class UserAdminController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UploadService uploadService;
    @Value("${upload-file.base-uri}")
    private String basePath;

    @Value("${upload-file.avatar-folder}")
    private String avatarFolder;
    private final RoleService roleService;
    private final FolderService folderService;

    public UserAdminController(
            UserService userService,
            PasswordEncoder passwordEncoder,
            UploadService uploadService,
            RoleService roleService,
            FolderService folderService
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.uploadService = uploadService;
        this.roleService = roleService;
        this.folderService=folderService;
    }

    @GetMapping("/enabled")
    @ApiMessage(value = "Get all user enabled")
    public ResponseEntity<ResultPaginationDTO> getAllUserEnabled(
            @Filter Specification<User> specification,
            Pageable pageable,
            @RequestParam("query") String query
    ) {

        return ResponseEntity.ok(userService.getAllUserPaging(specification, pageable, true,query));
    }

    @GetMapping("/disabled")
    @ApiMessage(value = "Get all user disabled")
    public ResponseEntity<ResultPaginationDTO> getAllUserDisabled(
            @Filter Specification<User> specification,
            Pageable pageable,
            @RequestParam("query") String query
    ) {

        return ResponseEntity.ok(userService.getAllUserPaging(specification, pageable, false,query));
    }


    @GetMapping("/{id}/enabled")
    @ApiMessage(value = "Get a new user")
    public ResponseEntity<ResUserDTO> getDetailUserEnabled(
            @PathVariable("id") Long id
    ) throws InValidException {
        User userDB = userService.findByIdAndEnabled(id, true);
        if (userDB == null) {
            throw new InValidException(
                    "User with id " + id + " does not exist"
            );
        }
        ResUserDTO resUserDTO = new ResUserDTO(userDB);
        return ResponseEntity.ok(resUserDTO);
    }

    @GetMapping("/{id}/disabled")
    @ApiMessage(value = "Get a new user")
    public ResponseEntity<ResUserDTO> getDetailUserDisabled(
            @PathVariable("id") Long id
    ) throws InValidException {
        User userDB = userService.findByIdAndEnabled(id, false);
        if (userDB == null) {
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
            @Valid @RequestBody ReqUserDTO userDTO
    ) throws InValidException {
        User userDB = userService.findByEmail(userDTO.getEmail());
        if (userDB != null) {
            throw new InValidException(
                    "Email " + userDTO.getEmail() + " already exists"
            );
        }
        Role role = roleService.findById(userDTO.getRole().getId());
        User user = new User();
        user.setRole(role);
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return ResponseEntity.ok(new ResUserDTO(userService.save(user)));
    }

    @PutMapping
    @ApiMessage(value = "Update a user")
    public ResponseEntity<ResUserDTO> update(
            @Valid @RequestBody ReqUserDTO user
    ) throws InValidException {
        User userDB = userService.findById(user.getId());
        if (userDB == null) {
            throw new InValidException(
                    "User with Id " + user.getId() + " does not exists"
            );
        }
        if(user.getRole()!=null){
            if(user.getRole().getId()!=userDB.getRole().getId()){
                Role role = roleService.findById(user.getRole().getId());
                userDB.setRole(role);
            }
        }
        userDB.setName(user.getName());
        return ResponseEntity.ok(new ResUserDTO(userService.save(userDB)));
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

        if (user.getAvatar() != null) {
            uploadService.deleteFile(user.getAvatar(), avatarFolder);
        }
        user.setAvatar(fileStorage);

        return ResponseEntity.ok(new ResUserDTO(userService.save(user)));

    }

    @PostMapping("/{id}/change-password")
    @ApiMessage(value = "Reset user password")
    public ResponseEntity<Void> resetPassword(
            @PathVariable("id") Long id,
            @RequestBody ReqPassword newPassword
    ) throws InValidException {
        User user = userService.findById(id);
        if (user == null) {
            throw new InValidException("User with id " + id + " does not exist");
        }
        if (!passwordEncoder.matches(newPassword.getOldPassword(), user.getPassword())) {
            throw new InValidException("Password is incorrect");
        }
        String hashPassword = passwordEncoder.encode(newPassword.getNewPassword());
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
        folderIsEnabled(user,true);
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
        folderIsEnabled(user,false);
        return ResponseEntity.ok(null);
    }


    @PostMapping("/bulk-create")
    @ApiMessage(value = "Bulk create users")
    public ResponseEntity<List<ResUserDTO>> bulkCreateUsers(
            @RequestParam("file") MultipartFile file
    ) throws IOException, InValidException {
        List<User> users = userService.parseUsersFromFile(file);
        List<User> userDB = userService.saveAll(users);

        List<ResUserDTO> res = userDB.stream().map(x->{
            createFolderRoot(x);
            return new ResUserDTO(x);
        }).toList();
        return ResponseEntity.ok(res);
    }
    private void folderIsEnabled(User user,Boolean enabled){
        Folder folder=folderService.activeFolderOfUserDeActive(user);
        if(folder!=null){
            folder.setIsEnabled(enabled);
            folderService.save(folder);
        }
    }
    @Async
    protected void createFolderRoot(User user) {
        Folder folder = new Folder();
        folder.setUser(user);
        folder.setFolderName(String.valueOf(user.getId()) + "DRIVE_" + user.getName());
        folder.setIsEnabled(true);
        folder.setIsPublic(true);
        folder.setParent(null);
        folder.setItemType(ItemTypeEnum.FOLDER);
        folder.setIsDeleted(false);
        folderService.save(folder);
    }

}
