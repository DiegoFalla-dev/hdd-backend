package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.Role;
import com.cineplus.cineplus.domain.entity.Role.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}