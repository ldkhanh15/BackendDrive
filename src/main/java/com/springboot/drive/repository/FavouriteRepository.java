package com.springboot.drive.repository;

import com.springboot.drive.domain.modal.Favourite;
import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.domain.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FavouriteRepository extends JpaRepository<Favourite,Long>, JpaSpecificationExecutor<Favourite> {
    Favourite findByUserAndItem(User user, Item item);

}
