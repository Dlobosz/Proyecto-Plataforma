package com.diegolobos.plataforma.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "languages")
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    /**
     * Id de lenguaje de Judge0 CE (ej. 71 = Python, 82 = SQLite).
     * Confirmar contra GET /languages de la suscripcion real antes de usar en desafios reales.
     */
    @Column(nullable = false)
    private Integer judge0LanguageId;

    @JsonIgnore
    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Unit> units = new ArrayList<>();

    public Language() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getJudge0LanguageId() {
        return judge0LanguageId;
    }

    public void setJudge0LanguageId(Integer judge0LanguageId) {
        this.judge0LanguageId = judge0LanguageId;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }
}
