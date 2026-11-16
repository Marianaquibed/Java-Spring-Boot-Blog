package com.blog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "registered", required = false) String registered,
            HttpServletRequest request,
            Model model) {

        HttpSession session = request.getSession(false);
        String error = null;

        if (session != null) {
            Exception ex = (Exception) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                error = "Usuario o contraseña inválidos.";
            }
        }

        if (registered != null) {
            model.addAttribute("successMessage", "¡Registro completado con éxito! Por favor, inicia sesión.");
        }

        model.addAttribute("error", error);
        return "login/index";
    }
}