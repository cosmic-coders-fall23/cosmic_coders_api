package org.cosmiccoders.api.repository;

import org.cosmiccoders.api.dao.UserWithRoles;
import org.cosmiccoders.api.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    public UserEntity findByUsername(String username);
    public UserEntity findByEmail(String email);
    UserWithRoles findDtoWithRolesByEmail(String email);
}
