package com.diegolobos.plataforma.service;

import com.diegolobos.plataforma.model.TipoValidacion;
import org.springframework.stereotype.Component;
import java.util.Map;

/** Desafios OUTPUT (Python): source_code = codigo tal cual, stdin = payload.input. */
@Component
public class PythonSubmissionAssembler implements SubmissionAssembler {

    @Override
    public boolean supports(TipoValidacion tipo) {
        return tipo == TipoValidacion.OUTPUT;
    }

    @Override
    public String buildSourceCode(String codigoUsuario, Map<String, String> payload) {
        return codigoUsuario;
    }

    @Override
    public String buildStdin(Map<String, String> payload) {
        return payload.get("input");
    }

    @Override
    public String getExpectedOutput(Map<String, String> payload) {
        return payload.get("expectedOutput");
    }
}
