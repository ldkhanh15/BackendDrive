package com.springboot.drive.service;

import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.Activity;
import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.repository.ActivityRepository;
import com.springboot.drive.ulti.constant.AccessEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository=activityRepository;
    }

    public ResultPaginationDTO getAll(Long itemId,Specification<Activity> specification, Pageable pageable){
        Specification<Activity> itemSpec = Specification.where(specification)
                .and((root, query, builder) -> builder.equal(root.get("item").get("id"), itemId));
        Page<Activity> activities=activityRepository.findAll(itemSpec, pageable);
        ResultPaginationDTO res=new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta=new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(activities.getTotalPages());
        meta.setTotal(activities.getTotalElements());

        res.setMeta(meta);
        res.setResult(activities.getContent());
        return res;
    }
    public Activity save(Activity activity){
        return activityRepository.save(activity);
    }

    public Activity findById(Long id){
        return  activityRepository.findById(id).orElse(null);
    }
    public void delete(Activity activity){
        activityRepository.delete(activity);
    }


    public Activity findByItem(Item item) {
        return activityRepository.findByItem(item);
    }

    public Activity findByItemAndAccessType(Item folder, AccessEnum access) {
        return activityRepository.findByItemAndActivityType(folder,access);
    }
}
