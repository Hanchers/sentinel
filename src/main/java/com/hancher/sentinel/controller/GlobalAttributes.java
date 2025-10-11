package com.hancher.sentinel.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.hancher.sentinel.exception.SentinelRunException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@ControllerAdvice
@Slf4j
public class GlobalAttributes {

    private JsonMapper jsonMapper;

    @ModelAttribute("jsonMapper")
    public JsonMapper jsonMapper() {
        return jsonMapper;
    }


    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(SentinelRunException.class)
    public String handleSentinelRunException(SentinelRunException ex, Model model) {
        List<String> errors = new ArrayList<>();

        model.addAttribute("errorMessage", "操作执行发生异常");
        model.addAttribute("errorDetail", ex.getMessage());

        log.error("表单验证失败: {}", errors);
        return "error/error";
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        model.addAttribute("errorMessage", "系统发生未知错误");
        model.addAttribute("errorDetail", ex.getMessage());

        log.error("系统异常: ", ex);
        return "error/error";
    }

}
