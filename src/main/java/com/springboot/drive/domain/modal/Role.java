package com.springboot.drive.domain.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.springboot.drive.ulti.SecurityUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private boolean active;


    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"roles"})
    @JoinTable(name = "permision_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")

    )
    List<Permission> permissions;

    @OneToMany(mappedBy = "role")
    private List<User> users;


    private Instant createdAt;
    private Instant updatedAt;
    private String updatedBy;
    private String createdBy;
    @PrePersist
    public void handleBeforeCreate(){
        this.createdBy= SecurityUtil.getCurrentUserLogin().isPresent()==true? SecurityUtil.getCurrentUserLogin().get():"";
        this.createdAt=Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate(){
        this.updatedBy= SecurityUtil.getCurrentUserLogin().isPresent()==true? SecurityUtil.getCurrentUserLogin().get()
                :"";
        this.updatedAt=Instant.now();
    }
}
