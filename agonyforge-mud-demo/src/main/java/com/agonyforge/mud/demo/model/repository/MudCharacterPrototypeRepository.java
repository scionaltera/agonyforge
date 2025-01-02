package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.MudCharacterTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MudCharacterPrototypeRepository extends JpaRepository<MudCharacterTemplate, Long> {
    List<MudCharacterTemplate> findByPlayerUsername(String username);
    List<MudCharacterTemplate> findByCharacterName(String name);
}
