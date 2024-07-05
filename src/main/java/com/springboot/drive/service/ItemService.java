package com.springboot.drive.service;

import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ItemService {
    private ItemRepository itemRepository;
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository=itemRepository;
    }


    public Item findById(Long id){
        return itemRepository.findById(id).orElse(null);
    }
}
