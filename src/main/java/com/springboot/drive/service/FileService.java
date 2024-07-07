package com.springboot.drive.service;

import com.springboot.drive.domain.dto.response.ResFileDTO;
import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.File;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.repository.FileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public ResultPaginationDTO getAll(Folder folder,boolean enabled) {
        List<File> files=fileRepository.findByParentAndIsEnabled(folder,enabled);
        List<ResFileDTO> res=files.stream().map(
                ResFileDTO::new
        ).toList();
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setResult(res);
        return resultPaginationDTO;

    }

    public File save(File file){
        return fileRepository.save(file);
    }
    public File findById(Long id){
        return fileRepository.findById(id).orElse(null);
    }
    public File findByIdAndEnabled(long id, boolean enabled){
        return fileRepository.findByItemIdAndIsEnabled(id,enabled);
    }
    public File softDelete(File file){
        file.setIsEnabled(false);
        return fileRepository.save(file);
    }
    public void delete(File file){
        file.setIsDeleted(true);
        fileRepository.save(file);
    }
    public File restore(File file){
        file.setIsEnabled(true);
        return fileRepository.save(file);
    }

    public File findByIdAndParent(Long id, Folder folder) {
        return fileRepository.findByItemIdAndParent(id, folder);
    }

}
