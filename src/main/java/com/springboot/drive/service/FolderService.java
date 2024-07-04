package com.springboot.drive.service;

import com.springboot.drive.domain.dto.response.ResFolderDTO;
import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.File;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.repository.FileRepository;
import com.springboot.drive.repository.FolderRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FolderService {

    private FolderRepository folderRepository;
    private FileRepository fileRepository;

    public FolderService(FolderRepository folderRepository,FileRepository fileRepository){
        this.folderRepository = folderRepository;
        this.fileRepository = fileRepository;
    }

    public ResultPaginationDTO getAllFolderAndFile(Specification<Folder> specification, Pageable pageable){
        List<Folder> folders=folderRepository.findByParentIsNullAndIsEnabled(true);
        List<ResFolderDTO> result=new ArrayList<>();
        folders.forEach(folder->result.add(new ResFolderDTO(folder)));

        ResultPaginationDTO resultPaginationDTO=new ResultPaginationDTO();
        resultPaginationDTO.setResult(result);
        return resultPaginationDTO;

    }

    public Folder save(Folder folder){
        return folderRepository.save(folder);
    }

    public void delete (Folder folder){
        folder.setIsEnabled(false);
        enableSubfolders(folder.getSubFolders(),false);
        enableFiles(folder.getFiles(),false);
        folderRepository.save(folder);
    }
    public void restore(Folder folder){
        folder.setIsEnabled(true);
        enableSubfolders(folder.getSubFolders(),true);
        enableFiles(folder.getFiles(),true);
        folderRepository.save(folder);
    }
    private void enableSubfolders(List<Folder> subfolders,boolean enabled) {
        if (subfolders != null) {
            for (Folder subfolder : subfolders) {
                subfolder.setIsEnabled(enabled);
                folderRepository.save(subfolder);
                enableSubfolders(subfolder.getSubFolders(),enabled);
                enableFiles(subfolder.getFiles(),enabled);
            }
        }
    }

    private void enableFiles(List<File> files,boolean enabled) {
        if (files != null) {
            for (File file : files) {
                file.setIsEnabled(enabled);
                fileRepository.save(file);
            }
        }
    }


    public Folder findById(long id){
        Optional<Folder> folder=folderRepository.findById(id);
        if(folder.isPresent()){
            return folder.get();
        }
        return null;
    }

    public Folder findByName(String name){
        return folderRepository.findByFolderName(name);
    }
    public Folder findByNameAndRootFolder(String name, Folder root){
        return folderRepository.findByFolderNameAndParent(name, root);
    }

}
