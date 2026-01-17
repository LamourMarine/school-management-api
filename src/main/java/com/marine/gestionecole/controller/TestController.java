package com.marine.gestionecole.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    public TestController() {
        System.out.println("========================================");
        System.out.println("✅ TestController CRÉÉ !");
        System.out.println("========================================");
    }
    
    @GetMapping
    public String test() {
        return "Hello World!";
    }
}