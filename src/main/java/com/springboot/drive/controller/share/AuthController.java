package com.springboot.drive.controller.share;

import com.springboot.drive.domain.dto.request.ReqLoginDTO;
import com.springboot.drive.domain.dto.request.ReqRegisterDTO;
import com.springboot.drive.domain.dto.response.ResLoginDTO;
import com.springboot.drive.domain.dto.response.ResUserDTO;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.domain.modal.Role;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.service.FolderService;
import com.springboot.drive.service.RoleService;
import com.springboot.drive.service.UserService;
import com.springboot.drive.ulti.SecurityUtil;
import com.springboot.drive.ulti.anotation.ApiMessage;
import com.springboot.drive.ulti.constant.ItemTypeEnum;
import com.springboot.drive.ulti.error.InValidException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private final FolderService folderService;

    private final SecurityUtil securityUtil;
    private final RoleService roleService;

    public AuthController(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil,
            UserService userService,
            PasswordEncoder passwordEncoder,
            FolderService folderService,
            RoleService roleService
    ) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.folderService = folderService;
        this.roleService = roleService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User currentUserDB = userService.findByEmailAndEnabled(loginDTO.getEmail(), true);
        ResLoginDTO res = new ResLoginDTO(currentUserDB);

        String token = securityUtil.accessToken(authentication.getName(), res);

        res.setAccessToken(token);
        //create refresh token
        String refreshToken = securityUtil.refreshToken(loginDTO.getEmail(), res);
        //update token
        userService.updateUserToken(refreshToken, loginDTO.getEmail());


        //set cookie
        ResponseCookie resCookie = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();


        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                .body(res);
    }

    @GetMapping("/account")
    @ApiMessage(value = "Get account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUserDB = userService.findByEmailAndEnabled(email, true);
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount(currentUserDB);
        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/refresh")
    @ApiMessage("Get refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token
    ) throws InValidException {
        if (refresh_token.equals("abc")) {
            throw new InValidException("Refresh token is not exist in cookie");
        }
        //check valid token
        Jwt jwt = securityUtil.checkValidToken(refresh_token);
        String email = jwt.getSubject();

        User user = userService.findByEmailAndRefreshTokenAndEnabled(email, refresh_token, true);
        if (user == null) {
            throw new InValidException("Refresh token is invalid");
        }

        User currentUserDB = userService.findByEmailAndEnabled(user.getEmail(), true);
        ResLoginDTO res = new ResLoginDTO(currentUserDB);

        String token = securityUtil.accessToken(user.getEmail(), res);

        res.setAccessToken(token);

        //create refresh token
        String newRefreshToken = securityUtil.refreshToken(user.getEmail(), res);
        //update token
        userService.updateUserToken(newRefreshToken, user.getEmail());


        //set cookie
        ResponseCookie resCookie = ResponseCookie
                .from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();


        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                .body(res);
    }

    @PostMapping("/logout")
    @ApiMessage(value = "Logout user")
    public ResponseEntity<Void> logout() throws InValidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email.isEmpty()) {
            throw new InValidException("Access token is invalid");
        }
        userService.updateUserToken(null, email);
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString()).body(null);
    }

    @PostMapping("/register")
    @ApiMessage(value = "Register an account")
    public ResponseEntity<ResUserDTO> register(
            @Valid @RequestBody ReqRegisterDTO userDTO
    ) throws InValidException {
        User userDB = userService.findByEmail(userDTO.getEmail());
        if (userDB != null) {
            throw new InValidException(
                    "Email " + userDTO.getEmail() + " already registered"
            );
        }

        Role roleUser = roleService.findByName("ROLE_USER");

        String hashPassword = passwordEncoder.encode(userDTO.getPassword());
        User user = new User();
        user.setPassword(hashPassword);
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setRole(roleUser);
        user.setEnabled(true);

        User us = userService.save(user);
        ResUserDTO dto = new ResUserDTO(us);

        createFolderRoot(us);
        return ResponseEntity.ok(dto);

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
