package com.lucas.login_seguro.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class TemaConfig {

    private final String tema;

    public TemaConfig(@Value("${app.tema:padrao}") String tema) {
        this.tema = tema;
    }

    @ModelAttribute("tema")
    public String tema() {
        return tema;
    }
}