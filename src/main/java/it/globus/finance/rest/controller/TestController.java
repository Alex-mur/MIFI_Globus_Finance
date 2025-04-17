package it.globus.finance.rest.controller;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return "Hello " + username + "!!!!";
    }
}
