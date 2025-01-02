package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.AbstractMudObject;
import com.agonyforge.mud.demo.model.impl.MudItem;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MudItemRepository extends JpaRepository<MudItem, Long> {
    List<MudItem> findByLocationHeld(AbstractMudObject holder);
    List<MudItem> findByLocationRoom(MudRoom room);
}
