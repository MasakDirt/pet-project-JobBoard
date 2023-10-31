package com.board.job.model.mapper;

import com.board.job.model.dto.user.UserCreateRequest;
import com.board.job.model.dto.user.UserResponse;
import com.board.job.model.dto.user.UserUpdateRequest;
import com.board.job.model.dto.user.UserUpdateRequestWithPassword;
import com.board.job.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User getUserFromUserCreate(UserCreateRequest userCreateRequest);

    UserResponse getUserResponseFromUser(User user);

    @Mapping(target = "password", expression = "java(userUpdateRequest.getNewPassword())")
    User getUserFromUserUpdateRequestPass(UserUpdateRequestWithPassword userUpdateRequest);

    User getUserFromUserUpdateRequest(UserUpdateRequest userUpdateRequest);
}
