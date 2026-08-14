package com.diegolobos.plataforma.repository;

import com.diegolobos.plataforma.model.ValidationCase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ValidationCaseRepository extends JpaRepository<ValidationCase, Long> {

    List<ValidationCase> findByChallengeId(Long challengeId);
}
