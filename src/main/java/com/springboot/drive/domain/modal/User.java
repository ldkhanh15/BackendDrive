package com.springboot.drive.domain.modal;

import com.springboot.drive.ulti.SecurityUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private String name;
    private String refreshToken;
    private String avatar;

    private boolean enabled;

    private double storageQuota;


    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<AccessItem> accessItems;


    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<Favourite> favourites;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<Item> items;



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
