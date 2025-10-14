package com.api.app.demo_api.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.app.demo_api.user.entity.User;


public interface UserRepository extends JpaRepository<User,Long> {

}
