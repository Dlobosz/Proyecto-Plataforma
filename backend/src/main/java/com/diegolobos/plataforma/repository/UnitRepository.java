package com.diegolobos.plataforma.repository;

import com.diegolobos.plataforma.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Long> {

    List<Unit> findByLanguageIdOrderByOrdenAsc(Long languageId);
}
