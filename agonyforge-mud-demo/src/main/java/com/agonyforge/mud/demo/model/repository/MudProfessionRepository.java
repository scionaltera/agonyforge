package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.MudProfession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MudProfessionRepository extends JpaRepository<MudProfession, Long> {
}
