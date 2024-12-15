package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.MudRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MudRoomRepository extends JpaRepository<MudRoom, Long> {
}
