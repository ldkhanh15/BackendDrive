package com.springboot.drive.repository;

import com.springboot.drive.domain.modal.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item,Long> {
}
