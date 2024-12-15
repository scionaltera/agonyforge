package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.MudItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MudItemRepository extends JpaRepository<MudItem, Long> {
    List<MudItem> getByChId(Long character);
    List<MudItem> getByRoomId(Long roomId);
}
