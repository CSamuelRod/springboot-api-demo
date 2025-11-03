package com.api.app.demo_api.user.mapper;

import com.api.app.demo_api.common.dto.UserDto;
import com.api.app.demo_api.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto dto);
}