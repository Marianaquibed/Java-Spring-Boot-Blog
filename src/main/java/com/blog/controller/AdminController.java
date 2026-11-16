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
@PreAuthorize("hasRole('ADMIN')") // Protege todas las rutas del controlador para que solo accedan administradores
public class AdminController {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Directorios donde se guardarán las imágenes
    private final String UPLOAD_DIR = "public/uploads/images/";
    private final String PORTFOLIO_DIR = "public/uploads/portfolio/";

    @GetMapping("/images")
    public String images(Model model) {
        List<Image> images = imageRepository.findAll();
        model.addAttribute("images", images);
        model.addAttribute("image", new Image()); // Objeto vacío para el formulario de subida
        return "admin/images"; // Renderiza templates/admin/images.html
    }

    @PostMapping("/images")
    public String uploadImage(
            @ModelAttribute("image") Image image,
            @RequestParam("fileParam") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        if (!file.isEmpty()) {
            try {
                // Generar un nombre seguro y único para el archivo
                String originalFilename = file.getOriginalFilename();
                String safeFilename = originalFilename != null ? originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_")
                        : "file";
                String newFilename = UUID.randomUUID().toString() + "_" + safeFilename;

                // Crear los directorios si no existen
                Path uploadPath = Paths.get(UPLOAD_DIR);
                Path portfolioPath = Paths.get(PORTFOLIO_DIR);
                if (!Files.exists(uploadPath))
                    Files.createDirectories(uploadPath);
                if (!Files.exists(portfolioPath))
                    Files.createDirectories(portfolioPath);

                // Guardar archivo en el directorio de imágenes principal
                Path filePath = uploadPath.resolve(newFilename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Copiar el archivo al directorio de portfolio
                Path portfolioFilePath = portfolioPath.resolve(newFilename);
                Files.copy(filePath, portfolioFilePath, StandardCopyOption.REPLACE_EXISTING);

                // Asignar el nombre del archivo a la entidad
                image.setFile(newFilename);

            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Error al subir el archivo: " + e.getMessage());
                return "redirect:/admin/images";
            }
        }

        // Guardar la entidad Image en la base de datos
        imageRepository.save(image);
        return "redirect:/admin/images";
    }

    @GetMapping("/categories")
    public String categories(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("category", new Category()); // Objeto vacío para el formulario
        return "admin/categories"; // Renderiza templates/admin/categories.html
    }

    @PostMapping("/categories")
    public String createCategory(@Valid @ModelAttribute("category") Category category, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/categories"; // Recarga la vista con errores si falla la validación
        }

        categoryRepository.save(category);
        return "redirect:/admin/categories"; // Redirección para evitar reenvío de formulario
    }
}