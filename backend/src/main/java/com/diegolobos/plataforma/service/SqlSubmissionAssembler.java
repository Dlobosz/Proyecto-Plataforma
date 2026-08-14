package com.diegolobos.plataforma.service;

import com.diegolobos.plataforma.model.TipoValidacion;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Desafios RESULTSET (SQL): source_code = schemaSql + seedDataSql + query del usuario.
 * No hay stdin. El expectedResult debe coincidir con el output por defecto de sqlite3
 * (una fila por linea, columnas separadas por "|", sin headers).
 */
@Component
public class SqlSubmissionAssembler implements SubmissionAssembler {

    @Override
    public boolean supports(TipoValidacion tipo) {
        return tipo == TipoValidacion.RESULTSET;
    }

    @Override
    public String buildSourceCode(String codigoUsuario, Map<String, String> payload) {
        String schemaSql = payload.getOrDefault("schemaSql", "");
        String seedDataSql = payload.getOrDefault("seedDataSql", "");
        return schemaSql + "\n" + seedDataSql + "\n" + codigoUsuario;
    }

    @Override
    public String buildStdin(Map<String, String> payload) {
        return null;
    }

    @Override
    public String getExpectedOutput(Map<String, String> payload) {
        return payload.get("expectedResult");
    }
}
