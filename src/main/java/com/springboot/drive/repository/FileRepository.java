package com.springboot.drive.repository;

import com.springboot.drive.domain.modal.File;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.ulti.constant.AccessEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<File,Long>, JpaSpecificationExecutor<File> {
    File findByItemIdAndParent(Long id, Folder folder);
    @Query("SELECT file FROM File file " +
            "JOIN Item i ON file.itemId = i.itemId " +
            "LEFT JOIN AccessItem ai ON i.itemId = ai.item.itemId " +
            "WHERE (i.isPublic = true OR ai.user.id = :userId)  AND ( i.isEnabled= true )" +
            "AND file.parent.itemId = :folderId")
    List<File> findAccessibleFiles(@Param("userId") Long userId, @Param("folderId") Long folderId);

    @Query("SELECT file FROM File file " +
            "JOIN Item i ON file.itemId = i.itemId " +
            "LEFT JOIN AccessItem ai ON i.itemId = ai.item.itemId " +
            "WHERE (i.isPublic = true OR (ai.user.id = :userId AND ai.accessType = :accessType )) " +
            "AND file.parent.itemId = :folderId")
    List<File> findAccessibleFiles(@Param("userId") Long userId, @Param("folderId") Long folderId, @Param("accessType") AccessEnum accessType);

    File findByItemIdAndIsEnabled(long id, boolean enabled);

    List<File> findByFileNameLikeAndParent(String fileName, Folder folder);


    @Query("SELECT file FROM File file " +
            "JOIN Item i ON file.itemId = i.itemId "+
            "WHERE file.isDeleted=:isDeleted AND file.isEnabled=:isEnabled AND file.parent.itemId=:itemId")
    List<File>findByParentAndIsEnabledAndIsDeleted(@Param("itemId") Long itemId,@Param("isEnabled")
            boolean isEnabled,
            @Param("isDeleted") boolean isDeleted);
}
