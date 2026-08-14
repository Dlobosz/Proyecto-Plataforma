package com.diegolobos.plataforma.repository;

import com.diegolobos.plataforma.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Long> {
}
