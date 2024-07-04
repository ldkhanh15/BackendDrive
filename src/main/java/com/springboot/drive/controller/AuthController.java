package com.springboot.drive.controller;

import com.springboot.drive.domain.dto.request.ReqLoginDTO;
import com.springboot.drive.domain.dto.response.ResLoginDTO;
import com.springboot.drive.domain.dto.response.UserResDTO;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.service.UserService;
import com.springboot.drive.ulti.SecurityUtil;
import com.springboot.drive.ulti.anotation.ApiMessage;
import com.springboot.drive.ulti.error.InValidException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    private PasswordEncoder passwordEncoder;
    private UserService userService;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private SecurityUtil securityUtil;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
                          UserService userService, PasswordEncoder passwordEncoder
    ) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);


        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = userService.findByEmail(loginDTO.getUsername());
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName(),
                    currentUserDB.getRole()
            );

            res.setUser(userLogin);
        }

        String token = securityUtil.accessToken(authentication.getName(), res);

        res.setAccessToken(token);
        //create refresh token
        String refreshToken = securityUtil.refreshToken(loginDTO.getUsername(), res);
        //update token
        userService.updateUserToken(refreshToken, loginDTO.getUsername());


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

        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = userService.findByEmail(email);
        ResLoginDTO.UserGetAccount userGetAccount=new ResLoginDTO.UserGetAccount();
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName(),
                    currentUserDB.getRole()
            );

            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/refresh")
    @ApiMessage("Get refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue (name = "refresh_token",defaultValue = "abc") String refresh_token
    ) throws InValidException {
        if(refresh_token.equals("abc")){
            throw new InValidException("Refresh token is not exist in cookie");
        }
        //check valid token
        Jwt jwt= securityUtil.checkValidToken(refresh_token);
        String email=jwt.getSubject();

        User user=userService.findByEmailAndRefreshToken(email,refresh_token);
        if(user==null){
            throw new InValidException("Refresh token is invalid");
        }


        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = userService.findByEmail(user.getEmail());
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName(),
                    currentUserDB.getRole()
            );

            res.setUser(userLogin);
        }
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
        String email = SecurityUtil.getCurrentUserLogin().isPresent()?SecurityUtil.getCurrentUserLogin().get() : "";
        if(email.equals("")){
            throw new InValidException("Access token is invalid");
        }
        userService.updateUserToken(null,email);
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token",null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,deleteSpringCookie.toString()).body(null);
    }

    @PostMapping("/register")
    @ApiMessage(value = "Register an account")
    public  ResponseEntity<UserResDTO> register(
            @Valid @RequestBody User user
    ) throws InValidException {
        User userDB=userService.findByEmail(user.getEmail());
        if(userDB!=null){
            throw new InValidException(
                    "Email "+user.getEmail()+" already registered"
            );
        }
        String hashPassword=passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User us=userService.save(user);
        UserResDTO dto=new UserResDTO(us);

        return ResponseEntity.ok(dto);

    }

}
