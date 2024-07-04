package com.springboot.drive.domain.modal;

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
    private User user;

    @Enumerated(EnumType.STRING)
    private AccessEnum accessType;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private Instant createdAt;
    private Instant updatedAt;
    private String updatedBy;
    private String createdBy;


}
