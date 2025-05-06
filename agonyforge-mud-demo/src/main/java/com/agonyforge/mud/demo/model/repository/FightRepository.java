package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.Fight;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FightRepository extends JpaRepository<Fight, Long> {
    Optional<Fight> findByAttackerAndDefender(MudCharacter attacker, MudCharacter defender);
}
