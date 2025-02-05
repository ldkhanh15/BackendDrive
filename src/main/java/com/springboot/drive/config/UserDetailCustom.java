package com.springboot.drive.config;

import com.springboot.drive.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component("userDetailsService")
public class UserDetailCustom implements UserDetailsService {
    private UserService userService;

    public UserDetailCustom(UserService userService) {
        this.userService=userService;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.springboot.drive.domain.modal.User user = userService.findByEmail(username);
        if (user == null){
            throw new UsernameNotFoundException("Email/password wrong");
        }
        return new User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
