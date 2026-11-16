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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "registration/register";
    }

    @PostMapping("/register")
    public String registerSubmit(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            @RequestParam(value = "plainPassword", required = false) String plainPassword,
            Model model) {

        if (bindingResult.hasErrors()) {
            System.out.println("Errores de validación en el registro:");
            bindingResult.getAllErrors().forEach(error -> System.out.println("   - " + error.getDefaultMessage()));
            return "registration/register";
        }

        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            model.addAttribute("error", "La contraseña es obligatoria.");
            return "registration/register";
        }

        user.setPassword(passwordEncoder.encode(plainPassword));

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles("ROLE_USER");
        }

        try {
            userRepository.save(user);
            System.out.println("¡Usuario guardado exitosamente en la BD!: " + user.getEmail());
        } catch (Exception e) {
            System.out.println("Error al guardar en MySQL: " + e.getMessage());
            model.addAttribute("error", "No se pudo completar el registro. Es posible que el email ya esté en uso.");
            return "registration/register";
        }

        return "redirect:/login?registered";
    }
}