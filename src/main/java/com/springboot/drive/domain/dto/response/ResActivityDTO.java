package com.springboot.drive.domain.dto.response;

import com.springboot.drive.ulti.constant.AccessEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResActivityDTO {
    private long id;
    private AccessEnum activityType;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;



}
