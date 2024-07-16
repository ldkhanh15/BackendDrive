package com.springboot.drive.domain.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.springboot.drive.ulti.SecurityUtil;
import com.springboot.drive.ulti.constant.AccessEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "accessitems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccessItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties(value ={"role","accessItems","favourites","items"} )
    private User user;

    @Enumerated(EnumType.STRING)
    private AccessEnum accessType;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @JsonIgnoreProperties(value = {"user","activity","favourites","parent","subFolders","files"})
    private Item item;

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
