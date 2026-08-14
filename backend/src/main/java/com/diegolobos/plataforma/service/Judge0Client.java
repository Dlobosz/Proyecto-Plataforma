package com.diegolobos.plataforma.service;

import com.diegolobos.plataforma.dto.judge0.Judge0SubmissionRequest;
import com.diegolobos.plataforma.dto.judge0.Judge0SubmissionResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Cliente de Judge0 CE. Ejecucion sincrona con wait=true para evitar
 * implementar polling (ver CLAUDE.md seccion 5).
 */
@Component
public class Judge0Client {

    private final WebClient judge0WebClient;

    public Judge0Client(WebClient judge0WebClient) {
        this.judge0WebClient = judge0WebClient;
    }

    public Judge0SubmissionResponse ejecutar(String sourceCode, Integer languageId, String stdin) {
        Judge0SubmissionRequest request = new Judge0SubmissionRequest(sourceCode, languageId, stdin);

        return judge0WebClient.post()
                .uri(uriBuilder -> uriBuilder.path("/submissions")
                        .queryParam("base64_encoded", "false")
                        .queryParam("wait", "true")
                        .build())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Judge0SubmissionResponse.class)
                .block();
    }
}
