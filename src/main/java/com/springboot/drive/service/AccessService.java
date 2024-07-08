package com.springboot.drive.service;

import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.AccessItem;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.repository.AccessRepository;
import com.springboot.drive.ulti.constant.AccessEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
public class AccessService {

    private final AccessRepository accessRepository;
    public AccessService(AccessRepository accessRepository){
        this.accessRepository = accessRepository;
    }

    public ResultPaginationDTO getAll(Specification<AccessItem> specification, Pageable pageable){
        Page<AccessItem> accessFiles= accessRepository.findAll(specification, pageable);
        ResultPaginationDTO res=new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta=new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(accessFiles.getTotalPages());
        meta.setTotal(accessFiles.getTotalElements());

        res.setMeta(meta);
        res.setResult(accessFiles);
        return res;
    }
    public AccessItem save(AccessItem accessItem){
        return accessRepository.save(accessItem);
    }

    public AccessItem findById(Long id){
        return accessRepository.findById(id).orElse(null);
    }
    public AccessItem findByItemAndUser(Item item,User user){
        return accessRepository.findByItemAndUser(item,user);
    }
    public void delete(Item item, User user){
        accessRepository.deleteByItemAndUser(item,user);
    }
    public void delete(AccessItem item){
        accessRepository.delete(item);
    }

    public AccessItem findByItemAndUserAndAccessType(Item item, User user, AccessEnum action) {
        return accessRepository.findByItemAndUserAndAccessType(item,user,action);
    }
}
