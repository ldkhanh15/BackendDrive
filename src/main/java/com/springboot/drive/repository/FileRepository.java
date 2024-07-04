package com.springboot.drive.repository;

import com.springboot.drive.domain.modal.File;
import com.springboot.drive.domain.modal.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FileRepository extends JpaRepository<File,Long>, JpaSpecificationExecutor<File> {
    File findByFileName(String name);

    List<File> findByParentIsNull();
    List<File> findByParent(Folder parent);

    File findByItemIdAndParent(Long id, Folder folder);

}
