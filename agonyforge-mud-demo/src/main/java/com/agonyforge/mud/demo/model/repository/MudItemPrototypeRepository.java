package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.MudItemTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MudItemPrototypeRepository extends JpaRepository<MudItemTemplate, Long> {
}
