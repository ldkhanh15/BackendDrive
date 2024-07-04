package com.springboot.drive.service;

import com.springboot.drive.domain.dto.response.ResFileDTO;
import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.File;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.repository.FileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileService {

    private FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public ResultPaginationDTO getAll(Folder folder) {
        List<File> files=fileRepository.findByParent(folder);
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
        Optional<File> file=fileRepository.findById(id);
        if(file.isPresent()){
            return file.get();
        }
        return null;
    }

    public void delete(File file){
        file.setIsEnabled(false);
        fileRepository.save(file);
    }
    
    public File findByName(String name){
        return fileRepository.findByFileName(name);
    }

    public File findByIdAndParent(Long id, Folder folder) {
        return fileRepository.findByItemIdAndParent(id, folder);
    }
}
