package com.api.app.demo_api.user.repository;

import com.api.app.demo_api.user.entity.UserAPI;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserAPI,Long> {

}
