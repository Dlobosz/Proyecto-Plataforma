package com.diegolobos.plataforma.controller;

import com.diegolobos.plataforma.dto.SubmissionResultDTO;
import com.diegolobos.plataforma.dto.SubmitCodigoRequest;
import com.diegolobos.plataforma.service.ExecutionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/challenges")
public class SubmissionController {

    private final ExecutionService executionService;

    public SubmissionController(ExecutionService executionService) {
        this.executionService = executionService;
    }

    @PostMapping("/{id}/submit")
    public SubmissionResultDTO submit(@PathVariable Long id, @Valid @RequestBody SubmitCodigoRequest request) {
        return executionService.ejecutar(id, request.getCodigo());
    }
}
