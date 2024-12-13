package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.MudZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MudZoneRepository extends JpaRepository<MudZone, Long> {
}
