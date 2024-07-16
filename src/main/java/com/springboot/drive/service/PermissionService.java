package com.springboot.drive.service;

import com.springboot.drive.domain.dto.response.ResPermissionDTO;
import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.Permission;
import com.springboot.drive.repository.PermissionRepository;
import com.springboot.drive.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    public PermissionService(PermissionRepository permissionRepository,RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    public ResultPaginationDTO getAll(Specification<Permission> specification, Pageable pageable){
        Page<Permission> permissions=permissionRepository.findAll(specification,pageable);
        ResultPaginationDTO resultPaginationDTO=new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta=new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(permissions.getTotalPages());
        meta.setTotal(permissions.getTotalElements());

        resultPaginationDTO.setMeta(meta);

        List<ResPermissionDTO> res = permissions.getContent().stream().map(ResPermissionDTO::new).toList();

        resultPaginationDTO.setResult(res);
        return resultPaginationDTO;
    }

    public Permission getById(Long id){
        return permissionRepository.findById(id).orElse(null);
    }
    public Permission save(Permission permission){
        return permissionRepository.save(permission);
    }
    public void delete(Permission permission){
        permission.getRoles().forEach(role->role.getPermissions().remove(permission));

        permissionRepository.delete(permission);
    }
    public boolean isPermissionExist(String module,String method,String apiPath){
        return permissionRepository.existsByModuleAndMethodAndApiPath(module,method,apiPath);
    }
    public Permission findByModuleAndMethodAndApiPath(String module,String method,String apiPath){
        return permissionRepository.findByModuleAndMethodAndApiPath(module,method,apiPath);
    }


}
