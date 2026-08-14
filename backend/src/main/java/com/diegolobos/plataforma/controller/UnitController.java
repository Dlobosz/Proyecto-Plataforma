package com.diegolobos.plataforma.controller;

import com.diegolobos.plataforma.model.Unit;
import com.diegolobos.plataforma.repository.UnitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UnitController {

    private final UnitRepository unitRepository;

    public UnitController(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @GetMapping("/languages/{languageId}/units")
    public List<Unit> listarPorLanguage(@PathVariable Long languageId) {
        return unitRepository.findByLanguageIdOrderByOrdenAsc(languageId);
    }

    @GetMapping("/units/{id}")
    public Unit obtener(@PathVariable Long id) {
        return unitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unit no encontrada: " + id));
    }
}
