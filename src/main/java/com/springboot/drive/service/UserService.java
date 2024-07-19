package com.springboot.drive.service;

import com.springboot.drive.domain.dto.response.ResultPaginationDTO;
import com.springboot.drive.domain.dto.response.ResUserDTO;
import com.springboot.drive.domain.modal.Role;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.repository.UserRepository;
import com.springboot.drive.service.spec.UserSpecification;
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
    private final RoleService roleService;
    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder,RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService=roleService;
    }

    public ResultPaginationDTO getAllUserPaging(Specification<User> specification, Pageable pageable,Boolean enabled,
                                                String query){
        Specification<User> spec= UserSpecification.findByEnabledAndNameOrEmail(enabled,query).and(specification);

        Page<User> users=userRepository.findAll(spec,pageable);
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
    public User findByIdAndEnabled(Long id,Boolean enabled){
        return userRepository.findByIdAndEnabled(id,enabled);
    }

    public User findByEmailAndEnabled(String email,boolean enabled){
        return userRepository.findByEmailAndEnabled(email,enabled);
    }

    public User findByEmailAndRefreshTokenAndEnabled(String email, String refreshToken,Boolean enabled) {
        return userRepository.findByEmailAndRefreshTokenAndEnabled(email, refreshToken,enabled);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = userRepository.findByEmailAndEnabled(email,true);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            userRepository.save(currentUser);
        }
    }

    public List<User> parseUsersFromFile(MultipartFile file) throws IOException, InValidException {
        List<User> users = new ArrayList<>();
        Role roleDefault=roleService.findByName("ROLE_USER");
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

                String roleName=csvRecord.get("role");
                if(roleName!=null && !roleName.isEmpty()) {
                    Role role=roleService.findByName(roleName);
                    if (role != null) {
                        user.setRole(role);
                    }else{
                        user.setRole(roleDefault);
                    }
                }else{
                    user.setRole(roleDefault);
                }
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

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
