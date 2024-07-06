package com.springboot.drive.controller;

import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.dto.response.UserResDTO;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.service.UserService;
import com.springboot.drive.ulti.anotation.ApiMessage;
import com.springboot.drive.ulti.error.InValidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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
    public ResponseEntity<UserResDTO> getDetailUser(
            @PathVariable("id") Long id
    ) throws InValidException {
        User userDB = userService.findById(id);
        if (userDB == null || !userDB.isEnabled()) {
            throw new InValidException(
                    "User with id " + id + " does not exist"
            );
        }
        UserResDTO userResDTO = new UserResDTO(userDB);
        return ResponseEntity.ok(userResDTO);
    }

    @PostMapping
    @ApiMessage(value = "Create a new user")
    public ResponseEntity<UserResDTO> createUser(
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
        UserResDTO userDTO = new UserResDTO(userService.save(user));
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping
    @ApiMessage(value = "Update a user")
    public ResponseEntity<UserResDTO> update(
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

        UserResDTO userResDTO = new UserResDTO(userService.save(userDB));

        return ResponseEntity.ok(userResDTO);

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


}
