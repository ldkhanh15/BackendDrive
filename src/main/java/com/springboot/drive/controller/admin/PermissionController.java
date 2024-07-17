package com.springboot.drive.controller.admin;

import com.springboot.drive.domain.dto.request.ReqPermissionDTO;
import com.springboot.drive.domain.dto.response.ResPermissionDTO;
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
@RequestMapping("/api/v1/admin/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    @ApiMessage(value = "Get all permissions")
    public ResponseEntity<ResultPaginationDTO> getAllPermissions(
            @Filter Specification<Permission> specification,
            Pageable pageable
    ) {
        return ResponseEntity.ok().body(permissionService.getAll(specification, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage(value = "Get a permission")
    public ResponseEntity<ResPermissionDTO> getPermission(
            @PathVariable("id") Long id
    ) throws InValidException {
        Permission permission = permissionService.getById(id);
        if (permission == null) {
            throw new InValidException(
                    "Permission with id " + id + " not found"
            );
        }
        return ResponseEntity.ok(new ResPermissionDTO(permission));
    }

    @PostMapping
    @ApiMessage(value = "Create new permission")
    public ResponseEntity<ResPermissionDTO> create(
            @Valid @RequestBody ReqPermissionDTO permissionDTO
    ) throws InValidException {
        if (permissionService.isPermissionExist(permissionDTO.getModule(),permissionDTO.getMethod(),permissionDTO.getApiPath())) {
            throw new InValidException(
                    "Permission already exists"
            );
        }
        Permission permission=new Permission();
        permission.setName(permissionDTO.getName());
        permission.setDescription(permissionDTO.getDescription());
        permission.setApiPath(permissionDTO.getApiPath());
        permission.setMethod(permission.getMethod());
        permission.setModule(permissionDTO.getModule());
        return ResponseEntity.ok(new ResPermissionDTO(permissionService.save(permission)));
    }

    @PutMapping
    @ApiMessage(value = "Update a permission")
    public ResponseEntity<ResPermissionDTO> update(
            @Valid @RequestBody ReqPermissionDTO permissionDTO
    ) throws InValidException {
        Permission permissionDB = permissionService.getById(permissionDTO.getId());
        if (permissionDB == null) {
            throw new InValidException(
                    "Permission with id " + permissionDTO.getId() + " not found"
            );
        }
        Permission exist=permissionService.findByModuleAndMethodAndApiPath(permissionDTO.getModule(),permissionDTO.getMethod(),
                permissionDTO.getApiPath());
        if (exist!=null) {
           if(exist.getId()!=permissionDB.getId()){
               throw new InValidException(
                       "Permission already exists"
               );
           }
        }
        permissionDB.setModule(permissionDTO.getModule());
        permissionDB.setMethod(permissionDTO.getMethod());
        permissionDB.setName(permissionDTO.getName());
        permissionDB.setApiPath(permissionDTO.getApiPath());
        permissionDB.setDescription(permissionDTO.getDescription());
        return ResponseEntity.ok(new ResPermissionDTO(permissionService.save(permissionDB)));
    }

    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete a permission")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id
    ) throws InValidException {
        Permission permissionDB = permissionService.getById(id);
        if (permissionDB == null) {
            throw new InValidException(
                    "Permission with id " + id + " not found"
            );
        }
        permissionService.delete(permissionDB);
        return ResponseEntity.ok(null);
    }

}
