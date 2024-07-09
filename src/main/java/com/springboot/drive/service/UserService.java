package com.springboot.drive.service;

import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.dto.response.ResUserDTO;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.repository.UserRepository;
import com.springboot.drive.ulti.error.InValidException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResultPaginationDTO getAllUserPaging(Specification<User> specification, Pageable pageable){
        Page<User> users=userRepository.findAll(specification,pageable);
        ResultPaginationDTO resultPaginationDTO=new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta=new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(users.getTotalPages());
        meta.setTotal(users.getTotalElements());

        List<ResUserDTO> list = users.getContent().stream().map(
                ResUserDTO::new
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
        return userRepository.findById(id).orElse(null);
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

    public List<User> parseUsersFromFile(MultipartFile file) throws IOException, InValidException {
        List<User> users = new ArrayList<>();
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream());
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            for (CSVRecord csvRecord : csvParser) {
                User userDB=userRepository.findByEmail(csvRecord.get("email"));
                if (userDB != null) {
                    continue;
                }
                User user = new User();
                user.setName(csvRecord.get("name"));
                user.setEmail(csvRecord.get("email"));
                String hashPassword = csvRecord.get("password");
                user.setPassword(passwordEncoder.encode(hashPassword));
                user.setEnabled(true);


                users.add(user);
            }
        } catch (Exception e) {
            throw new InValidException("Error parsing file: " + e.getMessage());
        }
        return users;
    }

    public List<User> saveAll(List<User> users) {
        return userRepository.saveAll(users);
    }
}
