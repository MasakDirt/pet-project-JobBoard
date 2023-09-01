package com.board.job.model.mapper;

import com.board.job.model.dto.user.UserCreateRequest;
import com.board.job.model.dto.user.UserResponse;
import com.board.job.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User getUserFromUserCreate(UserCreateRequest userCreateRequest);
    UserResponse getUserResponseFromUser(User user);
}
