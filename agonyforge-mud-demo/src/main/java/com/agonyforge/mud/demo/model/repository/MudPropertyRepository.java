package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.MudProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MudPropertyRepository extends JpaRepository<MudProperty, String> {
}
