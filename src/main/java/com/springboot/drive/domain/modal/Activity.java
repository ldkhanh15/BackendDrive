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
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Activity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private AccessEnum activityType;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private Instant createdAt;
    private Instant updatedAt;
    private String updatedBy;
    private String createdBy;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties(value = {"subActivity"})
    private Activity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"parent","subActivity"})
    private List<Activity> subActivity;

}
