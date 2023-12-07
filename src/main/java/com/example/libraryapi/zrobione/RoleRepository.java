package com.example.libraryapi.zrobione;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRole(UserRole role);
    void deleteByRole(UserRole role);

}
