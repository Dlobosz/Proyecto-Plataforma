package com.diegolobos.plataforma.service;

import com.diegolobos.plataforma.model.TipoValidacion;
import java.util.Map;

/**
 * Arma el source_code/stdin/expected que se envia a Judge0 a partir del
 * codigo del usuario y el payload de un ValidationCase. Judge0 no separa
 * "schema" de "query" para SQL, solo ejecuta un script completo (ver
 * CLAUDE.md seccion 5) por lo que ambos tipos terminan comparando texto
 * contra texto; lo unico que cambia es como se arma el source_code.
 */
public interface SubmissionAssembler {

    boolean supports(TipoValidacion tipo);

    String buildSourceCode(String codigoUsuario, Map<String, String> payload);

    String buildStdin(Map<String, String> payload);

    String getExpectedOutput(Map<String, String> payload);
}
