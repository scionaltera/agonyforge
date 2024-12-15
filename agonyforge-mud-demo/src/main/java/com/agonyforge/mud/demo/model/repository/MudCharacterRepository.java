package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.MudCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MudCharacterRepository extends JpaRepository<MudCharacter, Long> {
    List<MudCharacter> findByUsername(String username);
    List<MudCharacter> findByRoomId(Long roomId);
    List<MudCharacter> findByRoomIdBetween(Long firstRoomId, Long lastRoomId);
}
