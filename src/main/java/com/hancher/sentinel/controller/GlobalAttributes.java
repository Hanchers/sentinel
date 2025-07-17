package com.hancher.sentinel.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@AllArgsConstructor
@ControllerAdvice
public class GlobalAttributes {

    private JsonMapper jsonMapper;

    @ModelAttribute("jsonMapper")
    public JsonMapper jsonMapper() {
        return jsonMapper;
    }
}
