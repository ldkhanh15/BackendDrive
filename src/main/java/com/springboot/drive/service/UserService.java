package com.springboot.drive.service;

import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.dto.response.UserResDTO;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public ResultPaginationDTO getAllUserPaging(Specification<User> specification, Pageable pageable){
        Page<User> users=userRepository.findAll(specification,pageable);
        ResultPaginationDTO resultPaginationDTO=new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta=new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(users.getTotalPages());
        meta.setTotal(users.getTotalElements());

        List<UserResDTO> list = users.getContent().stream().map(
                UserResDTO::new
        ).toList();

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(list);
        return resultPaginationDTO;

    }

    public User save(User user){
        return userRepository.save(user);
    }

    public void delete (User user){
        user.setEnabled(false);
        userRepository.save(user);
    }
    public User findById(long id){
        Optional<User> userOptional=userRepository.findById(id);
        if(userOptional.isPresent()){
            return userOptional.get();
        }
        return null;
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User findByEmailAndRefreshToken(String email, String refreshToken) {
        return userRepository.findByEmailAndRefreshToken(email, refreshToken);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = userRepository.findByEmail(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            userRepository.save(currentUser);
        }
    }
}
