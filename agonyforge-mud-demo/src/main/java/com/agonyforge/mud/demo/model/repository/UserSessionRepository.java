package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {
    Optional<UserSession> getByPrincipalName(String principal);
}
