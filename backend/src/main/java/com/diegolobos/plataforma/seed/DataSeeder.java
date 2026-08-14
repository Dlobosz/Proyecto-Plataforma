package com.diegolobos.plataforma.seed;

import com.diegolobos.plataforma.model.*;
import com.diegolobos.plataforma.repository.*;
import tools.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Carga 1 unidad + 1 desafio de ejemplo por lenguaje al arrancar, si la
 * tabla languages esta vacia. Los judge0LanguageId (71 Python, 82 SQLite)
 * son los tipicos de Judge0 CE: confirmar contra GET /languages de la
 * suscripcion real antes de usarlos en desafios de verdad.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final LanguageRepository languageRepository;
    private final UnitRepository unitRepository;
    private final ChallengeRepository challengeRepository;
    private final ValidationCaseRepository validationCaseRepository;
    private final ObjectMapper objectMapper;

    public DataSeeder(LanguageRepository languageRepository,
                       UnitRepository unitRepository,
                       ChallengeRepository challengeRepository,
                       ValidationCaseRepository validationCaseRepository,
                       ObjectMapper objectMapper) {
        this.languageRepository = languageRepository;
        this.unitRepository = unitRepository;
        this.challengeRepository = challengeRepository;
        this.validationCaseRepository = validationCaseRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (languageRepository.count() > 0) {
            return;
        }
        seedPython();
        seedSql();
    }

    private void seedPython() throws Exception {
        Language python = new Language();
        python.setNombre("Python");
        python.setJudge0LanguageId(71);
        python = languageRepository.save(python);

        Unit unit = new Unit();
        unit.setLanguage(python);
        unit.setTitulo("Introduccion a Python");
        unit.setOrden(1);
        unit.setContenidoTeorico("""
                # Introduccion a Python

                Python es un lenguaje interpretado, de tipado dinamico, ideal para empezar
                a programar. Se lee la entrada estandar con `input()` y se imprime con
                `print()`.

                ```python
                nombre = input()
                print("Hola, " + nombre)
                ```
                """);
        unit.setEstado(EstadoUnidad.DESBLOQUEADA);
        unit = unitRepository.save(unit);

        Challenge challenge = new Challenge();
        challenge.setUnit(unit);
        challenge.setTitulo("Suma de dos numeros");
        challenge.setEnunciado("Lee dos numeros enteros desde la entrada estandar (uno por linea) "
                + "e imprime su suma.");
        challenge.setDificultad(Dificultad.FACIL);
        challenge.setTipoValidacion(TipoValidacion.OUTPUT);
        challenge.setCodigoInicial("a = int(input())\nb = int(input())\n# tu codigo aqui\n");
        challenge.setPistas("Usa int(input()) para leer cada numero y print() para mostrar el resultado.");
        challenge = challengeRepository.save(challenge);

        ValidationCase validationCase = new ValidationCase();
        validationCase.setChallenge(challenge);
        validationCase.setTipo(TipoValidacion.OUTPUT);
        Map<String, String> payload = Map.of(
                "input", "2\n3",
                "expectedOutput", "5"
        );
        validationCase.setPayload(objectMapper.writeValueAsString(payload));
        validationCaseRepository.save(validationCase);
    }

    private void seedSql() throws Exception {
        Language sql = new Language();
        sql.setNombre("SQL");
        sql.setJudge0LanguageId(82);
        sql = languageRepository.save(sql);

        Unit unit = new Unit();
        unit.setLanguage(sql);
        unit.setTitulo("Introduccion a SQL");
        unit.setOrden(1);
        unit.setContenidoTeorico("""
                # Introduccion a SQL

                SQL es el lenguaje estandar para consultar bases de datos relacionales.
                La instruccion basica para leer datos es `SELECT`.

                ```sql
                SELECT nombre FROM usuarios ORDER BY id;
                ```
                """);
        unit.setEstado(EstadoUnidad.DESBLOQUEADA);
        unit = unitRepository.save(unit);

        Challenge challenge = new Challenge();
        challenge.setUnit(unit);
        challenge.setTitulo("Seleccionar todos los usuarios");
        challenge.setEnunciado("Escribe una consulta que devuelva el nombre de todos los usuarios "
                + "de la tabla usuarios, ordenados por id.");
        challenge.setDificultad(Dificultad.FACIL);
        challenge.setTipoValidacion(TipoValidacion.RESULTSET);
        challenge.setCodigoInicial("-- escribe tu SELECT aqui\n");
        challenge.setPistas("Usa SELECT nombre FROM usuarios ORDER BY id;");
        challenge = challengeRepository.save(challenge);

        ValidationCase validationCase = new ValidationCase();
        validationCase.setChallenge(challenge);
        validationCase.setTipo(TipoValidacion.RESULTSET);
        Map<String, String> payload = Map.of(
                "schemaSql", "CREATE TABLE usuarios (id INTEGER PRIMARY KEY, nombre TEXT);",
                "seedDataSql", "INSERT INTO usuarios (id, nombre) VALUES (1, 'Ana'), (2, 'Beto');",
                "expectedResult", "Ana\nBeto"
        );
        validationCase.setPayload(objectMapper.writeValueAsString(payload));
        validationCaseRepository.save(validationCase);
    }
}
