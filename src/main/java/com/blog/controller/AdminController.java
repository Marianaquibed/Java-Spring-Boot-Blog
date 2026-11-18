package com.blog.controller;

import com.blog.entity.Category;
import com.blog.entity.Image;
import com.blog.repository.CategoryRepository;
import com.blog.repository.ImageRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private final String UPLOAD_DIR = "public/uploads/images/";
    private final String PORTFOLIO_DIR = "public/uploads/portfolio/";

    @GetMapping("/images")
    public String images(Model model) {
        model.addAttribute("images", imageRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll()); // <-- faltaba
        return "admin/images";
    }

    @PostMapping("/images")
    public String uploadImage(
            @RequestParam("imageFile") MultipartFile file,
            @RequestParam("categoryId") Long categoryId,
            RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Selecciona un archivo.");
            return "redirect:/admin/images";
        }

        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            redirectAttributes.addFlashAttribute("error", "La categoría seleccionada no existe.");
            return "redirect:/admin/images";
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String safeFilename = originalFilename != null
                    ? originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_")
                    : "file";
            String newFilename = UUID.randomUUID() + "_" + safeFilename;

            Path uploadPath = Paths.get(UPLOAD_DIR);
            Path portfolioPath = Paths.get(PORTFOLIO_DIR);
            Files.createDirectories(uploadPath);
            Files.createDirectories(portfolioPath);

            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(filePath, portfolioPath.resolve(newFilename), StandardCopyOption.REPLACE_EXISTING);

            Image image = new Image();
            image.setFile(newFilename);
            image.setCategory(category);
            imageRepository.save(image);

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al subir el archivo: " + e.getMessage());
        }

        return "redirect:/admin/images";
    }

    @GetMapping("/categories")
    public String categories(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("category", new Category());
        return "admin/categories";
    }

    @PostMapping("/categories")
    public String createCategory(@Valid @ModelAttribute("category") Category category,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/categories";
        }
        categoryRepository.save(category);
        return "redirect:/admin/categories";
    }
}