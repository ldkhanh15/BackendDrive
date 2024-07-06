package com.springboot.drive.controller;

import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.Permission;
import com.springboot.drive.service.PermissionService;
import com.springboot.drive.ulti.anotation.ApiMessage;
import com.springboot.drive.ulti.error.InValidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    private final PermissionService permissionService;
    public PermissionController(PermissionService permissionService){
        this.permissionService = permissionService;
    }

    @GetMapping
    @ApiMessage(value = "Get all permissions")
    public ResponseEntity<ResultPaginationDTO> getAllPermissions(
            @Filter Specification<Permission> specification,
            Pageable pageable
    ){
        return ResponseEntity.ok().body(permissionService.getAll(specification,pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage(value = "Get a permission")
    public ResponseEntity<Permission> getPermission(
            @PathVariable("id") Long id
    ) throws InValidException {
        Permission permission=permissionService.getById(id);
        if(permission==null){
            throw new InValidException(
                    "Permission with id " + id +" not found"
            );
        }
        return ResponseEntity.ok(permission);
    }

    @PostMapping
    @ApiMessage(value = "Create new permission")
    public ResponseEntity<Permission> create(
            @Valid @RequestBody Permission permission
    ) throws InValidException {
        if(permissionService.isPermissionExist(permission)){
            throw new InValidException(
                    "Permission already exists"
            );
        }
        return ResponseEntity.ok(permissionService.save(permission));
    }

    @PutMapping
    @ApiMessage(value = "Update a permission")
    public ResponseEntity<Permission> update(
            @Valid @RequestBody Permission permission
    ) throws InValidException {
        Permission permissionDB =permissionService.getById(permission.getId());
        if (permissionDB == null){
            throw new InValidException(
                    "Permission with id " + permission.getId()+" not found"
            );
        }
        if(permissionService.isPermissionExist(permission)){
            if(permissionService.isSameName(permission)){
                throw new InValidException(
                        "Permission already exists"
                );
            }

        }
        permissionDB.setModule(permission.getModule());
        permissionDB.setMethod(permission.getMethod());
        permissionDB.setName(permission.getName());
        permissionDB.setApiPath(permission.getApiPath());
        return ResponseEntity.ok(permissionService.save(permissionDB));
    }
    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete a permission")
    public ResponseEntity<Void> delete(
            @PathVariable("id")Long id
    ) throws InValidException {
        Permission permissionDB =permissionService.getById(id);
        if (permissionDB == null){
            throw new InValidException(
                    "Permission with id " + id+" not found"
            );
        }
        permissionService.delete(permissionDB);
        return ResponseEntity.ok(null);
    }

}
