package com.blog.controller;

import com.blog.entity.User;
import com.blog.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletException;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Mostrar el formulario de registro (/register)
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "registration/register"; // Renderiza templates/registration/register.html
    }

    // Procesar el formulario de registro (POST /register)
    @PostMapping("/register")
    public String registerSubmit(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            String plainPassword,
            HttpServletRequest request,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "registration/register";
        }

        // Cifrar la contraseña en texto plano (equivalente a hashPassword de Symfony)
        if (plainPassword != null && !plainPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(plainPassword));
        }

        // Asignar rol por defecto si es necesario (ej: ROLE_USER)
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles("ROLE_USER");
        }

        // Guardar el usuario en la base de datos
        userRepository.save(user);

        // Autenticación automática del usuario tras el registro (equivalente a $security->login)
        try {
            request.login(user.getEmail(), plainPassword);
        } catch (ServletException e) {
            // Si falla el auto-login, redirigimos al login manual
            return "redirect:/login";
        }

        return "redirect:/"; // Redirige a la página principal tras registrarse e iniciar sesión
    }
}