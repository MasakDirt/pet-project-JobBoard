package com.board.job.model.mapper;

import com.board.job.model.dto.role.RoleResponse;
import com.board.job.model.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
   RoleResponse getRoleResponseFromRole(Role role);
}
