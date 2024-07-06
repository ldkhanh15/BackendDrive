package com.springboot.drive.controller;

import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.Role;
import com.springboot.drive.service.RoleService;
import com.springboot.drive.ulti.anotation.ApiMessage;
import com.springboot.drive.ulti.error.InValidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @ApiMessage(value = "Get all roles")
    public ResponseEntity<ResultPaginationDTO> getAllRoles(
            @Filter Specification<Role> specification,
            Pageable pageable
    ) {
        return ResponseEntity.ok().body(roleService.getAll(specification, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage(value = "Get a role")
    public ResponseEntity<Role> getRole(
            @PathVariable("id") Long id
    ) throws InValidException {
        Role role = roleService.findById(id);
        if (role == null) {
            throw new InValidException(
                    "Role with id " + id + " not found"
            );
        }
        return ResponseEntity.ok(role);
    }

    @PostMapping
    @ApiMessage(value = "Create new role")
    public ResponseEntity<Role> create(
            @Valid @RequestBody Role role
    ) throws InValidException {
        if(roleService.existsByName(role.getName())){
            throw new InValidException(
                    "Role " + role.getName() + " already exists"
            );
        }
        return ResponseEntity.ok(roleService.save(role));
    }

    @PutMapping
    @ApiMessage(value = "Update a role")
    public ResponseEntity<Role> update(
            @Valid @RequestBody Role role
    ) throws InValidException {
        Role roleDB = roleService.findById(role.getId());
        if (roleDB == null) {
            throw new InValidException(
                    "Role with id " + role.getId() + " not found"
            );
        }
        roleDB.setName(role.getName());
        roleDB.setActive(role.isActive());
        roleDB.setDescription(role.getDescription());

        return ResponseEntity.ok(roleService.update(roleDB));
    }

    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete a role")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id
    ) throws InValidException {
        Role roleDB = roleService.findById(id);
        if (roleDB == null) {
            throw new InValidException(
                    "Role with id " + id + " not found"
            );
        }
        roleService.delete(roleDB);
        return ResponseEntity.ok(null);
    }
}
