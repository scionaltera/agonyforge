package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.CommandReference;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommandRepository extends JpaRepository<CommandReference, String> {
    Optional<CommandReference> findByName(String name);
    Optional<CommandReference> findFirstByNameStartingWith(String name, Sort sort);
}
