package com.example.libraryapi.db;

import com.example.libraryapi.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByName(String user);
}