package com.springboot.drive.domain.modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.springboot.drive.ulti.SecurityUtil;
import com.springboot.drive.ulti.constant.ItemTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "items")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "itemType")
public abstract class Item {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(insertable=false, updatable=false)
    @Enumerated(EnumType.STRING)
    private ItemTypeEnum itemType;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private Boolean isPublic;
    private Boolean isEnabled;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnoreProperties(value ={ "favourites","accessItems","items"})
    private User user;

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
