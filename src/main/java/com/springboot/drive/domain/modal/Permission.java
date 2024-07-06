package com.springboot.drive.domain.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.springboot.drive.ulti.SecurityUtil;
import com.springboot.drive.ulti.constant.AccessEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Permission {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    @NotBlank
    private String apiPath;
    @NotBlank
    private String method;
    @NotBlank
    private String module;
    @NotBlank
    private String name;

    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "permissions")
    @JsonIgnoreProperties(value = {"permissions"})
    private List<Role> roles;

    private Instant createdAt;
    private Instant updatedAt;
    private String updatedBy;
    private String createdBy;
    @PrePersist
    public void handleBeforeCreate(){
        this.createdBy= SecurityUtil.getCurrentUserLogin().isPresent()? SecurityUtil.getCurrentUserLogin().get():"";
        this.createdAt=Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate(){
        this.updatedBy= SecurityUtil.getCurrentUserLogin().isPresent()? SecurityUtil.getCurrentUserLogin().get()
                :"";
        this.updatedAt=Instant.now();
    }
}
