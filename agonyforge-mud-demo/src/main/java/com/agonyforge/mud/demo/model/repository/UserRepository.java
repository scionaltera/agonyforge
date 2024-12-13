package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findUserByPrincipalName(String principal);
}
