package com.springboot.drive.repository;

import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.ulti.constant.AccessEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder,Long>, JpaSpecificationExecutor<Folder> {
    List<Folder> findByFolderNameLikeAndParent(String name, Folder parent);
    List<Folder> findByUserAndIsEnabledAndParent(User user, boolean isEnabled,Folder parent);
    List<Folder> findByParentIsNullAndIsEnabled(Specification<Folder> specification, Pageable pageable,
                                                 Boolean enabled);

    Folder findByItemIdAndIsEnabled(Long id,Boolean enabled);
    @Query("SELECT f FROM Folder f " +
            "JOIN Item i ON f.itemId = i.itemId " +
            "LEFT JOIN AccessItem ai ON i.itemId = ai.item.itemId " +
            "WHERE (i.isPublic = true OR ai.user.id = :userId) AND ( i.isEnabled= true )  AND i.isDeleted=false " +
            "AND f.parent.itemId = :folderId")
    List<Folder> findAccessibleSubFolders(@Param("userId") Long userId, @Param("folderId") Long folderId);



    @Query("SELECT f FROM Folder f " +
            "JOIN Item i ON f.itemId = i.itemId " +
            "LEFT JOIN AccessItem ai ON i.itemId = ai.item.itemId " +
            "WHERE (i.isPublic = true OR (ai.user.id = :userId AND ai.accessType = :accessType) OR i.user.id=:userId)" +
            "AND i.isEnabled =true AND i.isDeleted=false AND f.itemId = :folderId")
    Folder findFolder(@Param("userId") Long userId, @Param("folderId") Long folderId, @Param("accessType") AccessEnum accessType);

    @Query("SELECT f FROM Folder f " +
            "JOIN Item i ON f.itemId = i.itemId " +
            "LEFT JOIN AccessItem ai ON i.itemId = ai.item.itemId " +
            "WHERE (i.isPublic = true OR (ai.user.id = :userId AND ai.accessType = :accessType )) AND i.isEnabled = " +
            "true  AND i.isDeleted=false AND f.parent.itemId = :folderId")
    List<Folder> findAccessibleSubFolders(@Param("userId") Long userId, @Param("folderId") Long folderId, @Param("accessType") AccessEnum accessType);


    Folder findByItemIdAndIsEnabledAndIsDeleted(long itemId,boolean isEnabled,boolean isDeleted);

    @Query("SELECT folder FROM Folder folder " +
            "JOIN Item i ON folder.itemId = i.itemId "+
            "WHERE folder.isDeleted=:isDeleted AND folder.isEnabled=:isEnabled AND folder.parent.itemId=:itemId")
    List<Folder>findByParentAndIsEnabledAndIsDeleted(@Param("itemId") Long itemId,
                                                     @Param("isEnabled") boolean isEnabled,@Param("isDeleted")boolean isDeleted);

}
