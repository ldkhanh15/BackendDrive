package com.springboot.drive.domain.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@DiscriminatorValue("FOLDER")
public class Folder extends Item {

    private String folderName;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties(value = {"subfolders","files"})
    private Folder parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = {"parent","subfolders","files"})
    private List<Folder> subFolders;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value ={"parent"})
    private List<File> files;


}

