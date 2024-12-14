package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.MudItemPrototype;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MudItemPrototypeRepository extends JpaRepository<MudItemPrototype, Long> {
}
