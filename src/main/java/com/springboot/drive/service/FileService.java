package com.springboot.drive.service;

import com.springboot.drive.domain.dto.response.ResFileDTO;
import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.File;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.repository.FileRepository;
import com.springboot.drive.service.spec.FileSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public ResultPaginationDTO getAll(Specification<File> specification,
                                      Pageable pageable, Long folderId, boolean enabled, boolean deleted) {
        Specification<File> specFile = FileSpecification.findByParentIdEnabledAndDelete(folderId, enabled, deleted)
                .and(specification);
        Page<File> files = fileRepository.findAll(specFile, pageable);
        List<ResFileDTO> res = files.stream().map(
                ResFileDTO::new
        ).toList();
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(files.getTotalPages());
        meta.setTotal(files.getTotalElements());
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(res);
        return resultPaginationDTO;

    }

    public File save(File file) {
        return fileRepository.save(file);
    }

    public File findById(Long id) {
        return fileRepository.findById(id).orElse(null);
    }

    public File findByIdAndEnabled(long id, boolean enabled) {
        return fileRepository.findByItemIdAndIsEnabled(id, enabled);
    }
    public File findByIdAndEnabledAndDeleted(Long id, Boolean enabled,Boolean deleted) {
        return fileRepository.findByItemIdAndIsEnabledAndIsDeleted(id, enabled,deleted);
    }

    public File softDelete(File file) {
        file.setIsEnabled(false);
        return fileRepository.save(file);
    }

    public void delete(File file) {
        file.setIsDeleted(true);
        fileRepository.save(file);
    }

    public File restore(File file) {
        file.setIsEnabled(true);
        return fileRepository.save(file);
    }

    public File findByIdAndParent(Long id, Folder folder) {
        return fileRepository.findByItemIdAndParent(id, folder);
    }


    public List<File> findByNameInFolder(Folder folder, String name) {
        return fileRepository.findByFileNameLikeAndParent("%" + name + "%", folder);
    }
}
