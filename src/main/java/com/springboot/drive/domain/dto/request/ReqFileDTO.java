package com.springboot.drive.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqFileDTO {
    @NotBlank
    private long id;
    @NotBlank
    private String fileName;
    private boolean isPublic;
}
