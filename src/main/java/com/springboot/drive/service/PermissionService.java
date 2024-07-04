package com.springboot.drive.service;

import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.Permission;
import com.springboot.drive.domain.modal.Role;
import com.springboot.drive.repository.PermissionRepository;
import com.springboot.drive.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissionService {
    private PermissionRepository permissionRepository;
    private RoleRepository roleRepository;
    public PermissionService(PermissionRepository permissionRepository,RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    public ResultPaginationDTO getAll(Specification<Permission> specification, Pageable pageable){
        Page<Permission> jobs=permissionRepository.findAll(specification,pageable);
        ResultPaginationDTO resultPaginationDTO=new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta=new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(jobs.getTotalPages());
        meta.setTotal(jobs.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(jobs.getContent());
        return resultPaginationDTO;
    }

    public Permission getById(Long id){
        Optional<Permission> permissionOption=permissionRepository.findById(id);
        if(permissionOption.isPresent()){
            return permissionOption.get();
        }
        return null;
    }
    public Permission save(Permission permission){
        return permissionRepository.save(permission);
    }
    public void delete(Permission permission){
        permission.getRoles().forEach(role->role.getPermissions().remove(permission));

        permissionRepository.delete(permission);
    }
    public boolean isPermissionExist(Permission permission){
        return permissionRepository.existsByModuleAndMethodAndApiPath(permission.getModule(),permission.getMethod(),permission.getApiPath());
    }

    public boolean isSameName(Permission p){
        Permission pDB=getById(p.getId());
        if(pDB!=null){
            if(p.getName().toLowerCase().equals(pDB.getName().toLowerCase())){
                return true;
            }
        }
        return false;
    }


}
