package com.springboot.drive.repository;

import com.springboot.drive.domain.modal.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FavouriteRepository extends JpaRepository<Favourite,Long>, JpaSpecificationExecutor<Favourite> {
}
