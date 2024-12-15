package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.MudSpecies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MudSpeciesRepository extends JpaRepository<MudSpecies, Long> {
}
