package com.esg.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {

    @GetMapping("/")
    public String redirectToSwagger() {
        return "redirect:/swagger-ui.html";
    }

    @GetMapping("/docs")
    public String redirectToSwaggerDocs() {
        return "redirect:/swagger-ui.html";
    }

    @GetMapping("/api-docs")
    public String redirectToApiDocs() {
        return "redirect:/v3/api-docs";
    }
}