package com.springboot.drive.config;

import com.springboot.drive.domain.modal.Permission;
import com.springboot.drive.domain.modal.Role;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.service.UserService;
import com.springboot.drive.ulti.SecurityUtil;
import com.springboot.drive.ulti.error.PermissionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        System.out.println(">>>>RUN HANDLER");
        System.out.println(">>>>PATH: " + path);
        System.out.println(">>>>METHOD: " + requestMethod);
        System.out.println(">>>>URI: " + requestURI);

        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() :
                "";

        User user = userService.findByEmail(email);

        if (user != null) {
            Role role = user.getRole();
            if (role != null) {
                List<Permission> permissions = role.getPermissions();

                boolean check =
                        permissions.stream().anyMatch(x -> x.getApiPath().equals(path) && x.getMethod().equals(requestMethod));

                if (!check) {
                    throw new PermissionException(
                            "You cannot access this endpoint"
                    );
                }
            } else {
                throw new PermissionException(
                        "You cannot access this endpoint"
                );
            }

        }

        return true;


    }
}
