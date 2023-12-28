package com.prototype.products.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class HealthController {

    public static final String STATUS_TEXT = "ok";

    @RequestMapping(value = "/healthz", method = RequestMethod.GET)
    public String healthCheck(HttpServletResponse response) {
        return STATUS_TEXT;
    }
}
