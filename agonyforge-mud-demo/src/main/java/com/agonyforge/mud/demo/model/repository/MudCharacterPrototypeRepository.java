package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.MudCharacterPrototype;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MudCharacterPrototypeRepository extends JpaRepository<MudCharacterPrototype, Long> {
    List<MudCharacterPrototype> findByPlayerUsername(String username);
    List<MudCharacterPrototype> findByName(String name);
}
