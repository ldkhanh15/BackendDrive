package com.springboot.drive.domain.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@DiscriminatorValue("FILE")

public class File extends Item {
    private String fileType;
    private String fileName;
    private Long fileSize;
    private String filePath;
    private Long downloadCount;
    private Long viewCount;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties(value = {"subfolders","files"})
    private Folder parent;




}
