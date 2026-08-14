package com.diegolobos.plataforma.repository;

import com.diegolobos.plataforma.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByChallengeIdOrderByFechaDesc(Long challengeId);
}
