package com.diegolobos.plataforma.controller;

import com.diegolobos.plataforma.model.Challenge;
import com.diegolobos.plataforma.repository.ChallengeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ChallengeController {

    private final ChallengeRepository challengeRepository;

    public ChallengeController(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @GetMapping("/units/{unitId}/challenges")
    public List<Challenge> listarPorUnit(@PathVariable Long unitId) {
        return challengeRepository.findByUnitId(unitId);
    }

    @GetMapping("/challenges/{id}")
    public Challenge obtener(@PathVariable Long id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge no encontrado: " + id));
    }
}
