package com.diegolobos.plataforma.dto.judge0;

/** id == 3 significa "Accepted" (ejecuto sin errores) en Judge0 CE. */
public class Judge0Status {

    private Integer id;
    private String description;

    public Judge0Status() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
