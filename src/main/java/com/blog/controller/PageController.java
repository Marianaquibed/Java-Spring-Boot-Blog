package com.blog.controller;

import com.blog.entity.Category;
import com.blog.entity.Contact;
import com.blog.repository.CategoryRepository;
import com.blog.repository.ContactRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class PageController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ContactRepository contactRepository;

    // Ruta raíz ('/')
    @GetMapping("/")
    public String index(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "index"; // Renderiza templates/index.html
    }

    // Ruta '/about'
    @GetMapping("/about")
    public String about() {
        return "page/about"; // Renderiza templates/page/about.html
    }

    // Ruta GET para mostrar el formulario de contacto ('/contact')
    @GetMapping("/contact")
    public String contactForm(Model model) {
        model.addAttribute("contact", new Contact());
        return "page/contact"; // Renderiza templates/page/contact.html
    }

    // Ruta POST para procesar el envío del formulario de contacto
    @PostMapping("/contact")
    public String contactSubmit(@Valid @ModelAttribute("contact") Contact contact, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "page/contact";
        }
        contactRepository.save(contact);
        return "redirect:/thankyou";
    }

    // Ruta '/thankyou'
    @GetMapping("/thankyou")
    public String thankyou() {
        return "page/thankyou"; // Renderiza templates/page/thankyou.html
    }
}