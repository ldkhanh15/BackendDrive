package com.springboot.drive.controller;

import com.springboot.drive.domain.dto.request.ReqActivityDTO;
import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.Activity;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.service.ActivityService;
import com.springboot.drive.service.FolderService;
import com.springboot.drive.service.ItemService;
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

    private final FolderService folderService;
    private final ActivityService activityService;
    private final ItemService itemService;
    public ActivityController(
            FolderService folderService,
            ActivityService activityService,
            ItemService itemService
    ){
        this.folderService = folderService;
        this.activityService = activityService;
        this.itemService = itemService;
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
    @PostMapping
    public ResponseEntity<Activity> createActivity(
           @Valid @RequestBody ReqActivityDTO activity
    ) throws InValidException {
        Item item=itemService.findById(activity.getItem().getId());
        if(item==null){
            throw new InValidException(
                    "Item with id " + activity.getItem().getId()+" does not exist"
            );
        }
        Activity parent=activityService.findById(activity.getParent().getId());

        Activity ac=new Activity();
        ac.setItem(item);
        ac.setParent(parent);


        return ResponseEntity.ok(activityService.save(ac));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivityOfFolder(
            @PathVariable("folderId") Long folderId,
            @PathVariable("id") Long id
    ) throws InValidException {
        Folder folder=folderService.findById(folderId);
        if(folder==null){
            throw new InValidException(
                    "Folder with id " + folderId+"does not exist"
            );
        }
        Activity activity=activityService.findById(id);
        if(activity==null){
            throw new InValidException("" +
                    "Activity with id " + id+" does not exist"
            );
        }
        activityService.delete(activity);
        return ResponseEntity.ok(null)   ;
    }

}
