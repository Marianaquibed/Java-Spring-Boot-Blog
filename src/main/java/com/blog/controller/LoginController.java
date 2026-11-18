package com.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "registered", required = false) String registered,
            Model model) {

        model.addAttribute("showError", error != null);
        model.addAttribute("showLogout", logout != null);

        if (registered != null) {
            model.addAttribute("successMessage", "¡Registro completado con éxito! Por favor, inicia sesión.");
        }
        return "login/index";
    }
}