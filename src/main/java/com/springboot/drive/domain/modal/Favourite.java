package com.springboot.drive.domain.modal;

import com.springboot.drive.ulti.SecurityUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "favourites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Favourite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

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
