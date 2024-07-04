package com.springboot.drive.service;

import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.domain.modal.Permission;
import com.springboot.drive.domain.modal.Role;
import com.springboot.drive.repository.PermissionRepository;
import com.springboot.drive.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class RoleService {

    private RoleRepository roleRepository;
    private PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository
    ) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public ResultPaginationDTO getAll(Specification<Role> specification, Pageable pageable) {
        Page<Role> jobs = roleRepository.findAll(specification, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(jobs.getTotalPages());
        meta.setTotal(jobs.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(jobs.getContent());
        return resultPaginationDTO;
    }

    public void delete(Role role) {
        roleRepository.delete(role);
    }

    public Role findById(Long id) {
        Optional<Role> role = roleRepository.findById(id);
        if (role.isPresent()) {
            return role.get();
        }
        return null;
    }

    public Role save(Role role) {
        if (role.getPermissions() != null) {
            List<Long> reqPermissions = role.getPermissions().stream().map(x -> x.getId()).collect(Collectors.toList());

            List<Permission> permissions = permissionRepository.findByIdIn(reqPermissions);
            role.setPermissions(permissions);
        }
        System.out.println(role.getPermissions().size());
        return roleRepository.save(role);
    }

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    public Role update(Role role) {
        Role roleDB=findById(role.getId());
        if (role.getPermissions() != null) {
            List<Long> reqPermissions = role.getPermissions().stream().map(x -> x.getId()).collect(Collectors.toList());

            List<Permission> permissions = permissionRepository.findByIdIn(reqPermissions);
            role.setPermissions(permissions);
        }
        roleDB.setName(role.getName());
        roleDB.setActive(role.isActive());
        roleDB.setDescription(role.getDescription());
        roleDB.setPermissions(role.getPermissions());

        return roleRepository.save(roleDB);
    }
}
