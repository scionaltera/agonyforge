package com.agonyforge.mud.demo.model.repository;

import com.agonyforge.mud.demo.model.impl.CommandReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandRepository extends JpaRepository<CommandReference, String> {

}
