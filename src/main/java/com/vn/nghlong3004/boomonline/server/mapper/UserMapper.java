package com.vn.nghlong3004.boomonline.server.mapper;

import com.vn.nghlong3004.boomonline.server.model.User;
import com.vn.nghlong3004.boomonline.server.model.request.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
  @Mapping(target = "passwordHash", source = "password")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "updated", ignore = true)
  User toEntity(RegisterRequest request);
}
