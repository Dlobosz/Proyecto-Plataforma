package com.diegolobos.plataforma.service;

import com.diegolobos.plataforma.dto.SubmissionResultDTO;
import com.diegolobos.plataforma.dto.ValidationCaseResultDTO;
import com.diegolobos.plataforma.dto.judge0.Judge0SubmissionResponse;
import com.diegolobos.plataforma.model.Challenge;
import com.diegolobos.plataforma.model.ResultadoSubmission;
import com.diegolobos.plataforma.model.Submission;
import com.diegolobos.plataforma.model.TipoValidacion;
import com.diegolobos.plataforma.model.ValidationCase;
import com.diegolobos.plataforma.repository.ChallengeRepository;
import com.diegolobos.plataforma.repository.SubmissionRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Orquestador de la ejecucion de un desafio: por cada ValidationCase arma el
 * codigo con el SubmissionAssembler correspondiente, llama a Judge0Client,
 * compara resultados y guarda un Submission con el resultado global
 * (ver CLAUDE.md seccion 5).
 */
@Service
public class ExecutionService {

    private final ChallengeRepository challengeRepository;
    private final SubmissionRepository submissionRepository;
    private final Judge0Client judge0Client;
    private final List<SubmissionAssembler> assemblers;
    private final ObjectMapper objectMapper;

    public ExecutionService(ChallengeRepository challengeRepository,
                             SubmissionRepository submissionRepository,
                             Judge0Client judge0Client,
                             List<SubmissionAssembler> assemblers,
                             ObjectMapper objectMapper) {
        this.challengeRepository = challengeRepository;
        this.submissionRepository = submissionRepository;
        this.judge0Client = judge0Client;
        this.assemblers = assemblers;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public SubmissionResultDTO ejecutar(Long challengeId, String codigoUsuario) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge no encontrado: " + challengeId));

        SubmissionAssembler assembler = resolveAssembler(challenge.getTipoValidacion());
        Integer judge0LanguageId = challenge.getUnit().getLanguage().getJudge0LanguageId();

        List<ValidationCaseResultDTO> detalle = new ArrayList<>();
        boolean hadError = false;
        boolean hadFailure = false;

        for (ValidationCase validationCase : challenge.getValidationCases()) {
            Map<String, String> payload = parsePayload(validationCase.getPayload());
            String sourceCode = assembler.buildSourceCode(codigoUsuario, payload);
            String stdin = assembler.buildStdin(payload);
            String expectedOutput = trim(assembler.getExpectedOutput(payload));

            Judge0SubmissionResponse response;
            try {
                response = judge0Client.ejecutar(sourceCode, judge0LanguageId, stdin);
            } catch (Exception ex) {
                hadError = true;
                detalle.add(new ValidationCaseResultDTO(validationCase.getId(), false, expectedOutput, null,
                        "Error al comunicarse con Judge0: " + ex.getMessage()));
                continue;
            }

            if (response == null || response.getStatus() == null) {
                hadError = true;
                detalle.add(new ValidationCaseResultDTO(validationCase.getId(), false, expectedOutput, null,
                        "Judge0 no devolvio una respuesta valida"));
                continue;
            }

            // status.id == 3 es "Accepted" en Judge0 CE: la ejecucion corrio sin errores.
            if (response.getStatus().getId() != 3) {
                hadError = true;
                String detalleError = response.getCompileOutput() != null ? response.getCompileOutput() : response.getStderr();
                String mensaje = response.getStatus().getDescription() + (detalleError != null ? ": " + detalleError : "");
                detalle.add(new ValidationCaseResultDTO(validationCase.getId(), false, expectedOutput, trim(response.getStdout()), mensaje));
                continue;
            }

            String actualOutput = trim(response.getStdout());
            boolean passed = actualOutput.equals(expectedOutput);
            if (!passed) {
                hadFailure = true;
            }
            detalle.add(new ValidationCaseResultDTO(validationCase.getId(), passed, expectedOutput, actualOutput, null));
        }

        ResultadoSubmission resultado = hadError ? ResultadoSubmission.ERROR
                : hadFailure ? ResultadoSubmission.FAILED
                : ResultadoSubmission.PASSED;

        Submission submission = new Submission();
        submission.setChallenge(challenge);
        submission.setCodigoEnviado(codigoUsuario);
        submission.setResultado(resultado);
        submission = submissionRepository.save(submission);

        return new SubmissionResultDTO(submission.getId(), resultado, detalle);
    }

    private SubmissionAssembler resolveAssembler(TipoValidacion tipo) {
        return assemblers.stream()
                .filter(a -> a.supports(tipo))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay SubmissionAssembler para el tipo " + tipo));
    }

    private Map<String, String> parsePayload(String payloadJson) {
        try {
            return objectMapper.readValue(payloadJson, new TypeReference<Map<String, String>>() {
            });
        } catch (JacksonException ex) {
            throw new IllegalStateException("Payload de ValidationCase invalido: " + ex.getMessage(), ex);
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
