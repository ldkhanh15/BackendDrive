package com.springboot.drive.repository;

import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.domain.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder,Long>, JpaSpecificationExecutor<Folder> {
    Folder findByFolderName(String name);
    Folder findByFolderNameAndParent(String name, Folder parent);

    List<Folder> findByParentIsNullAndIsEnabled(Boolean enabled);

    Folder findByItemIdAndIsEnabled(Long id,Boolean enabled);
}
