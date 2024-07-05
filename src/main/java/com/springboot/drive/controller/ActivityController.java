package com.springboot.drive.controller;

import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.Activity;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.service.ActivityService;
import com.springboot.drive.service.FolderService;
import com.springboot.drive.ulti.error.InValidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/folders/{folderId}/activities")
public class ActivityController {

    private FolderService folderService;
    private ActivityService activityService;
    public ActivityController(
            FolderService folderService,
            ActivityService activityService
    ){
        this.folderService = folderService;
        this.activityService = activityService;
    }
    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getAllByFolderId(
            @PathVariable("folderId") Long folderId,
            @Filter Specification<Activity> specification,
            Pageable pageable

    ) throws InValidException {
        Folder folder=folderService.findById(folderId);
        if(folder==null){
            throw new InValidException(
                    "Folder with id " + folderId+"does not exist"
            );
        }
        return ResponseEntity.ok(activityService.getAll(folderId,specification,pageable));
    }

}
