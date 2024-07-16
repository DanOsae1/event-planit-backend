package com.osaebros.eventplanner.repository.mapping;

import com.osaebros.eventplanner.entity.UserAccount;
import com.osaebros.eventplanner.repository.dto.UserAccountDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserAccountMapper {

    UserAccountMapper INSTANCE = Mappers.getMapper(UserAccountMapper.class);

    UserAccountDto userAccountToDto(UserAccount userAccount);
}
