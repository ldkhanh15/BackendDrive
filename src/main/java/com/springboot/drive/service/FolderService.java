package com.springboot.drive.service;

import com.springboot.drive.domain.dto.response.ResFolderDTO;
import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.Activity;
import com.springboot.drive.domain.modal.File;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.repository.FileRepository;
import com.springboot.drive.repository.FolderRepository;
import com.springboot.drive.ulti.constant.AccessEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FolderService {

    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    private final UploadService uploadService;
    @Value("${upload-file.file-folder}")
    private String fileFolder;
    public FolderService(FolderRepository folderRepository, FileRepository fileRepository,UploadService uploadService) {
        this.folderRepository = folderRepository;
        this.fileRepository = fileRepository;
        this.uploadService=uploadService;
    }

    public List<Folder> getAccessibleSubFolders(Long userId, Long folderId) {
        return folderRepository.findAccessibleSubFolders(userId, folderId);
    }


    public ResultPaginationDTO getWithAccess(Long userId, Long folderId) {
        Folder folder = folderRepository.findFolder(userId, folderId, AccessEnum.VIEW);
        if (folder != null) {
            List<Folder> subFolders = folderRepository.findAccessibleSubFolders(userId, folderId, AccessEnum.VIEW);
            List<File> files = fileRepository.findAccessibleFiles(userId, folderId, AccessEnum.VIEW);
            folder.setSubFolders(subFolders);
            folder.setFiles(files);
        }

        ResFolderDTO result = new ResFolderDTO(folder);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setResult(result);
        return resultPaginationDTO;
    }

    public ResultPaginationDTO getAllFolderAndFile(Specification<Folder> specification, Pageable pageable,
                                                   boolean enabled,boolean isDeleted) {
        Specification<Folder> folderSpec = Specification.where(specification)
                .and((root, query, builder) -> builder.isNull(root.get("parent")))
                .and((root, query, builder) ->builder.equal(root.get("isEnabled"),enabled))
                .and((root, query, builder) -> builder.equal(root.get("isDeleted"),isDeleted));
        Page<Folder> folders = folderRepository.findAll(folderSpec,pageable);
        List<ResFolderDTO> result = new ArrayList<>();
        folders.getContent().forEach(folder -> result.add(new ResFolderDTO(folder)));
        ResultPaginationDTO.Meta meta=new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(folders.getTotalPages());
        meta.setTotal(folders.getTotalElements());

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(result);
        return resultPaginationDTO;

    }

    public Folder save(Folder folder) {
        return folderRepository.save(folder);
    }

    public void deleteSoft(Folder folder) {
        folder.setIsEnabled(false);
        enableSubfolders(folder.getSubFolders(), false);
        enableFiles(folder.getFiles(), false);
        folderRepository.save(folder);
    }

    public Folder restore(Folder folder) {
        folder.setIsEnabled(true);
        enableSubfolders(folder.getSubFolders(), true);
        enableFiles(folder.getFiles(), true);
        return folderRepository.save(folder);

    }

    private void enableSubfolders(List<Folder> subfolders, boolean enabled) {
        if (subfolders != null) {
            for (Folder subfolder : subfolders) {
                subfolder.setIsEnabled(enabled);
                folderRepository.save(subfolder);
                enableSubfolders(subfolder.getSubFolders(), enabled);
                enableFiles(subfolder.getFiles(), enabled);
            }
        }
    }

    private void enableFiles(List<File> files, boolean enabled) {
        if (files != null) {
            for (File file : files) {
                file.setIsEnabled(enabled);
                fileRepository.save(file);
            }
        }
    }
    private void deleteFilesInSubFolder(List<Folder> folders) throws URISyntaxException {
        if (folders != null) {
           for(Folder folder : folders) {
               deleteFilesInFolder(folder);
               deleteFilesInSubFolder(folder.getSubFolders());
           }

        }
    }
    private void deleteFilesInFolder(Folder folder) throws URISyntaxException {
        if (folder != null) {
            for (File file : folder.getFiles()) {
                uploadService.deleteFile(file.getFilePath(), fileFolder);
            }
        }
    }

    public Folder findById(long id) {
        return folderRepository.findById(id).orElse(null);
    }
    public Folder findByIdAndEnabled(long id,boolean enabled) {
        return folderRepository.findByItemIdAndIsEnabled(id,enabled);
    }

    public Folder findByNameAndRootFolder(String name, Folder root) {
        return folderRepository.findByFolderNameAndParent(name, root);
    }

    public void delete(Folder folder) throws URISyntaxException {
        folder.setIsDeleted(true);
        setDeletedSubFolders(folder.getSubFolders(),true);
        setDeletedFiles(folder.getFiles(),true);
        folderRepository.save(folder);
    }
    private void setDeletedSubFolders(List<Folder> subfolders, boolean deleted) {
        if (subfolders != null) {
            for (Folder subfolder : subfolders) {
                subfolder.setIsDeleted(deleted);
                folderRepository.save(subfolder);
                setDeletedSubFolders(subfolder.getSubFolders(), deleted);
                setDeletedFiles(subfolder.getFiles(), deleted);
            }
        }
    }

    private void setDeletedFiles(List<File> files, boolean deleted) {
        if (files != null) {
            for (File file : files) {
                file.setIsDeleted(deleted);
                fileRepository.save(file);
            }
        }
    }

    public Folder findByUserAndParent(User user) {
        List<Folder> folders = folderRepository.findByUserAndIsEnabledAndParent(user,true,null);
        if((folders != null) && (!folders.isEmpty())){
            return folders.get(0);
        }
        return null;
    }
}
