package com.springboot.drive.service;

import com.springboot.drive.domain.dto.request.ReqRoleDTO;
import com.springboot.drive.domain.dto.response.ResRoleDTO;
import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.Permission;
import com.springboot.drive.domain.modal.Role;
import com.springboot.drive.repository.PermissionRepository;
import com.springboot.drive.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository
    ) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public ResultPaginationDTO getAll(Specification<Role> specification, Pageable pageable) {
        Page<Role> roles = roleRepository.findAll(specification, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(roles.getTotalPages());
        meta.setTotal(roles.getTotalElements());

        List<ResRoleDTO> res=roles.getContent().stream().map(ResRoleDTO::new).toList();
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(res);
        return resultPaginationDTO;
    }

    public void delete(Role role) {
        roleRepository.delete(role);
    }

    public Role findById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role save(ReqRoleDTO role) {
        Role roleDB=new Role();
        roleDB.setDescription(role.getDescription());
        roleDB.setName(role.getName());
        roleDB.setActive(role.getActive());
        if (role.getPermissions() != null) {
            List<Long> reqPermissions = role.getPermissions().stream().map(ReqRoleDTO.PermissionRole::getId).collect(Collectors.toList());

            List<Permission> permissions = permissionRepository.findByIdIn(reqPermissions);
            roleDB.setPermissions(permissions);
        }
        return roleRepository.save(roleDB);
    }

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    public Role update(ReqRoleDTO role) {
        Role roleDB=findById(role.getId());
        roleDB.setDescription(role.getDescription());
        roleDB.setName(role.getName());
        roleDB.setActive(role.getActive());
        if (role.getPermissions() != null) {
            List<Long> reqPermissions = role.getPermissions().stream().map(ReqRoleDTO.PermissionRole::getId).collect(Collectors.toList());

            List<Permission> permissions = permissionRepository.findByIdIn(reqPermissions);
            roleDB.setPermissions(permissions);
        }
        return roleRepository.save(roleDB);
    }

    public Role findByName(String roleUser) {
        return roleRepository.findByName(roleUser);
    }
}
