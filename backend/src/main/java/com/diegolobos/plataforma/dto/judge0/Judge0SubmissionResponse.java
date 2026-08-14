package com.diegolobos.plataforma.dto.judge0;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Respuesta de POST /submissions?wait=true de Judge0 CE. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Judge0SubmissionResponse {

    private String stdout;
    private String stderr;

    @JsonProperty("compile_output")
    private String compileOutput;

    private String message;
    private Judge0Status status;

    public Judge0SubmissionResponse() {
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public String getCompileOutput() {
        return compileOutput;
    }

    public void setCompileOutput(String compileOutput) {
        this.compileOutput = compileOutput;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Judge0Status getStatus() {
        return status;
    }

    public void setStatus(Judge0Status status) {
        this.status = status;
    }
}
