package com.springboot.drive.ulti.error;

import com.springboot.drive.domain.dto.response.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            InValidException.class,
            InValidAccessEnumTypeException.class
    })
    public ResponseEntity<RestResponse<Object>> handleException(Exception exception) {
        RestResponse<Object> res = new RestResponse<>();

        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Exception occurred.....");
        res.setMessage(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> validatorError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();

        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getBody().getDetail());

        List<String> errors = fieldErrors.stream().map(f -> "Field '" + f.getField() + "' " + f.getDefaultMessage()).collect(Collectors.toList());

        res.setMessage(errors.size() > 1 ? errors : errors.get(0));

        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleNotFound(NoResourceFoundException ex) {

        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError("404 Not found. URL may be not exist.");
        res.setMessage(ex.getMessage());

        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(value = CustomAuthenticationException.class)
    public ResponseEntity<RestResponse<Object>> handleTokenInValid(Exception ex) {

        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        res.setError("Token invalid or expired !!!");
        res.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
    }

    @ExceptionHandler(value = {
            StorageException.class
    })
    public ResponseEntity<RestResponse<Object>> handleFileException(Exception exception) {
        RestResponse<Object> res = new RestResponse<>();

        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Exception occurred while uploading file");
        res.setMessage(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
    @ExceptionHandler(value = {
            PermissionException.class
    })
    public ResponseEntity<RestResponse<Object>> handlePermissionException(Exception exception) {
        RestResponse<Object> res = new RestResponse<>();

        res.setStatusCode(HttpStatus.FORBIDDEN.value());
        res.setError("Forbidden");
        res.setMessage(exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
    }
    @ExceptionHandler(value = {
            NotOwnerException.class
    })
    public ResponseEntity<RestResponse<Object>> handleNotOwner(NotOwnerException exception) {
        RestResponse<Object> res = new RestResponse<>();

        res.setStatusCode(HttpStatus.FORBIDDEN.value());
        res.setError("Forbidden");
        res.setMessage(exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
    }
    @ExceptionHandler(value = {
            Exception.class
    })
    public ResponseEntity<RestResponse<Object>> handleAllException(Exception exception) {
        RestResponse<Object> res = new RestResponse<>();

        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.setError("Internal Server Error");
        res.setMessage(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }
}
