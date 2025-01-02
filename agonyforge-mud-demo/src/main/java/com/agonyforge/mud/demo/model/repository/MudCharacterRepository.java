package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MudCharacterRepository extends JpaRepository<MudCharacter, Long> {
    Optional<MudCharacter> findByCharacterName(String name);
    List<MudCharacter> findByLocationRoom(MudRoom room);
    List<MudCharacter> findByLocationRoomId(long id);
    List<MudCharacter> findByLocationRoomIdBetween(Long firstRoomId, Long lastRoomId);
}
