package com.diegolobos.plataforma.dto;

/** Resultado de la ejecucion de un ValidationCase individual dentro de un submit. */
public class ValidationCaseResultDTO {

    private Long validationCaseId;
    private boolean passed;
    private String expectedOutput;
    private String actualOutput;
    private String errorMessage;

    public ValidationCaseResultDTO() {
    }

    public ValidationCaseResultDTO(Long validationCaseId, boolean passed, String expectedOutput,
                                    String actualOutput, String errorMessage) {
        this.validationCaseId = validationCaseId;
        this.passed = passed;
        this.expectedOutput = expectedOutput;
        this.actualOutput = actualOutput;
        this.errorMessage = errorMessage;
    }

    public Long getValidationCaseId() {
        return validationCaseId;
    }

    public void setValidationCaseId(Long validationCaseId) {
        this.validationCaseId = validationCaseId;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    public String getActualOutput() {
        return actualOutput;
    }

    public void setActualOutput(String actualOutput) {
        this.actualOutput = actualOutput;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
