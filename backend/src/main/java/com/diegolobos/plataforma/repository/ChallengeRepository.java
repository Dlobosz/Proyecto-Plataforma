package com.diegolobos.plataforma.repository;

import com.diegolobos.plataforma.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findByUnitId(Long unitId);
}
