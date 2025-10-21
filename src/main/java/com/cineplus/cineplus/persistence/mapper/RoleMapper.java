package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {
    // RoleDto toDto(Role role); // No lo necesitamos por ahora
    // Role toEntity(RoleDto roleDto);
}