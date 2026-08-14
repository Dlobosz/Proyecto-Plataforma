package com.diegolobos.plataforma.dto.judge0;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Body enviado a POST /submissions?wait=true de Judge0 CE. */
public class Judge0SubmissionRequest {

    @JsonProperty("source_code")
    private String sourceCode;

    @JsonProperty("language_id")
    private Integer languageId;

    @JsonProperty("stdin")
    private String stdin;

    public Judge0SubmissionRequest() {
    }

    public Judge0SubmissionRequest(String sourceCode, Integer languageId, String stdin) {
        this.sourceCode = sourceCode;
        this.languageId = languageId;
        this.stdin = stdin;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public String getStdin() {
        return stdin;
    }

    public void setStdin(String stdin) {
        this.stdin = stdin;
    }
}
