package com.springboot.drive.domain.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.springboot.drive.ulti.constant.AccessEnum;
import jakarta.persistence.*;
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

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private String apiPath;

    private String method;

    private String module;
    private String name;

    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "permissions")
    @JsonIgnoreProperties(value = {"permissions"})
    private List<Role> roles;

    private Instant createdAt;
    private Instant updatedAt;
    private String updatedBy;
    private String createdBy;

}
