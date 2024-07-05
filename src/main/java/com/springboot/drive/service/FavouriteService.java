package com.springboot.drive.service;

import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.modal.Favourite;
import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.domain.modal.Permission;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.repository.FavouriteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FavouriteService {

    private FavouriteRepository favouriteRepository;


    public FavouriteService(
            FavouriteRepository favouriteRepository
    ) {
        this.favouriteRepository=favouriteRepository;
 ;
    }

    public ResultPaginationDTO getAll(Specification<Favourite> specification, Pageable pageable){
        Page<Favourite> favourites=favouriteRepository.findAll(specification,pageable);
        ResultPaginationDTO resultPaginationDTO=new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta=new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(favourites.getTotalPages());
        meta.setTotal(favourites.getTotalElements());


        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(favourites);
        return resultPaginationDTO;
    }
    public ResultPaginationDTO getAllOfUser(Long userId,Specification<Favourite> specification, Pageable pageable){
        Specification<Favourite> userSpec = Specification.where(specification)
                .and((root, query, builder) -> builder.equal(root.get("userId"), userId));
        Page<Favourite> favourites=favouriteRepository.findAll(userSpec,pageable);
        ResultPaginationDTO resultPaginationDTO=new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta=new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(favourites.getTotalPages());
        meta.setTotal(favourites.getTotalElements());


        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(favourites);
        return resultPaginationDTO;
    }
    public Favourite findById(Long id){
        Optional<Favourite> optionalFavourite=favouriteRepository.findById(id);
        if(optionalFavourite.isPresent()){
            return optionalFavourite.get();
        }
        return null;
    }

    public Favourite save(Favourite favourite){
        return favouriteRepository.save(favourite);
    }

    public void delete(Favourite favourite){
        favouriteRepository.delete(favourite);
    }

    public Favourite findByUserAndItem(User user, Item item) {
        return favouriteRepository.findByUserAndItem(user, item);
    }
}
