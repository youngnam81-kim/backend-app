package com.project.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.app.model.User;
import com.project.app.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 모든 사용자 조회
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    // 이름으로 사용자 조회
    public List<User> findUserByUserName(String uesrName) {
		return userRepository.findByUserName(uesrName);
	}
    
    // ID로 사용자 조회
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    // 사용자 저장 (등록/수정)
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // 사용자 삭제
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

	public Optional<User> findByUserIdAndPassword(String userId, String password) {
		return userRepository.findByUserIdAndPassword(userId, password);
	}

	
	
}