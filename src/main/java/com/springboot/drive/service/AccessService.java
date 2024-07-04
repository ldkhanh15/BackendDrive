package com.springboot.drive.service;

import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.AccessItem;
import com.springboot.drive.repository.AccessRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccessService {

    private AccessRepository accessRepository;
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
        Optional<AccessItem> accessFile = accessRepository.findById(id);
        if(accessFile.isPresent()){
            return accessFile.get();
        }
        return null;
    }

}
