package com.api.app.demo_api.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.api.app.demo_api.user.entity.User;
import com.api.app.demo_api.user.repository.UserRepository;

@Service
public class UserService {

        private final UserRepository UserRepository;
        
        public UserService(UserRepository UserRepository) {
            this.UserRepository = UserRepository;
        }

        public List<User> getAllUsers() {
            return UserRepository.findAll();
        }

        public Optional<User> getUserById(Long id) {
            return UserRepository.findById(id);
        }

        public User saveUser(User u) {
            return UserRepository.save(u);
        }

        public void deleteUser(Long id) {
            UserRepository.deleteById(id);
        }

}
