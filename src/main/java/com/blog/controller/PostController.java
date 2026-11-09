package com.blog.controller;

import com.blog.entity.Comment;
import com.blog.entity.Post;
import com.blog.entity.User;
import com.blog.repository.CommentRepository;
import com.blog.repository.PostRepository;
import com.blog.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@PreAuthorize("hasRole('USER')")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    private final int POSTS_PER_PAGE = 1;
    private final String UPLOAD_DIR = "public/uploads/images/";
    private final String PORTFOLIO_DIR = "public/uploads/portfolio/";

    // ==========================================
    // NUEVO POST (/blog/new)
    // ==========================================
    @GetMapping("/blog/new")
    public String newPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "blog/new_post";
    }

    @PostMapping("/blog/new")
    public String newPostSubmit(
            @ModelAttribute("post") Post post,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String originalFilename = imageFile.getOriginalFilename();
                String safeFilename = originalFilename != null ? originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_") : "file";
                String newFilename = UUID.randomUUID().toString() + "_" + safeFilename;

                Path uploadPath = Paths.get(UPLOAD_DIR);
                Path portfolioPath = Paths.get(PORTFOLIO_DIR);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                if (!Files.exists(portfolioPath)) Files.createDirectories(portfolioPath);

                Path filePath = uploadPath.resolve(newFilename);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                Path portfolioFilePath = portfolioPath.resolve(newFilename);
                Files.copy(filePath, portfolioFilePath, StandardCopyOption.REPLACE_EXISTING);

                post.setImage(newFilename);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Error al subir la imagen: " + e.getMessage());
                return "redirect:/blog/new";
            }
        }

        // Generar Slug básico a partir del título
        String slug = post.getTitle().toLowerCase().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", "-");
        post.setSlug(slug);

        // Asignar usuario autenticado y valores iniciales
        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        post.setPostUser(user);
        post.setPublishedAt(LocalDateTime.now());
        post.setNumLikes(0);
        post.setNumComments(0);

        postRepository.save(post);
        return "redirect:/blog/new";
    }

    // ==========================================
    // VISTA DE UN SOLO POST (/single_post/{slug})
    // ==========================================
    @GetMapping("/single_post/{slug}")
    public String singlePost(@PathVariable("slug") String slug, Model model) {
        Post post = postRepository.findAll().stream()
                .filter(p -> p.getSlug().equals(slug))
                .findFirst()
                .orElse(null);

        if (post == null) {
            return "redirect:/blog";
        }

        List<Post> recents = postRepository.findTop5ByOrderByPublishedAtDesc();

        model.addAttribute("post", post);
        model.addAttribute("recents", recents);
        model.addAttribute("comment", new Comment());
        return "blog/single_post";
    }

    @PostMapping("/single_post/{slug}")
    public String addComment(
            @PathVariable("slug") String slug,
            @Valid @ModelAttribute("comment") Comment comment,
            BindingResult bindingResult,
            Model model) {

        Post post = postRepository.findAll().stream()
                .filter(p -> p.getSlug().equals(slug))
                .findFirst()
                .orElse(null);

        if (post == null) {
            return "redirect:/blog";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("post", post);
            model.addAttribute("recents", postRepository.findTop5ByOrderByPublishedAtDesc());
            return "blog/single_post";
        }

        comment.setPost(post);
        comment.setPublishedAt(LocalDateTime.now());
        post.setNumComments(post.getNumComments() + 1);

        commentRepository.save(comment);
        postRepository.save(post);

        return "redirect:/single_post/" + slug;
    }

    // ==========================================
    // BÚSQUEDA DE POSTS (/blog/buscar/{page})
    // ==========================================
    @GetMapping({"/blog/buscar", "/blog/buscar/{page}"})
    public String buscar(
            @PathVariable(value = "page", required = false) Integer pageNum,
            @RequestParam(value = "searchTerm", defaultValue = "") String searchTerm,
            Model model) {

        int page = (pageNum != null && pageNum > 0) ? pageNum - 1 : 0; // Spring Data usa páginas base 0
        Pageable pageable = PageRequest.of(page, POSTS_PER_PAGE);

        Page<Post> postPage = postRepository.findByContentContainingIgnoreCaseOrderByPublishedAtDesc(searchTerm, pageable);
        List<Post> recents = postRepository.findTop5ByOrderByPublishedAtDesc();

        int totalPages = postPage.getTotalPages();
        int currentPage = page + 1;

        if (currentPage > totalPages && totalPages > 0) {
            return "redirect:/blog/buscar/" + totalPages + "?searchTerm=" + searchTerm;
        }

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("recents", recents);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("page", currentPage);
        model.addAttribute("totalPages", Math.max(1, totalPages));
        model.addAttribute("total", postPage.getTotalElements());

        return "blog/blog";
    }

    // ==========================================
    // LISTADO Y PAGINACIÓN DE BLOG (/blog/{page})
    // ==========================================
    @GetMapping({"/blog", "/blog/{page}"})
    public String blogIndex(@PathVariable(value = "page", required = false) Integer pageNum, Model model) {
        int page = (pageNum != null && pageNum > 0) ? pageNum - 1 : 0;
        Pageable pageable = PageRequest.of(page, POSTS_PER_PAGE);

        Page<Post> postPage = postRepository.findAllByOrderByPublishedAtDesc(pageable);
        List<Post> recents = postRepository.findTop5ByOrderByPublishedAtDesc();

        int totalPages = postPage.getTotalPages();
        int currentPage = page + 1;

        if (currentPage > totalPages && totalPages > 0) {
            return "redirect:/blog/" + totalPages;
        }

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("recents", recents);
        model.addAttribute("page", currentPage);
        model.addAttribute("totalPages", Math.max(1, totalPages));
        model.addAttribute("total", postPage.getTotalElements());

        return "blog/blog";
    }

    // ==========================================
    // DAR LIKE AJAX (/single_post/{slug}/like)
    // ==========================================
    @PostMapping("/single_post/{slug}/like")
    @ResponseBody
    public Map<String, Object> likePost(@PathVariable("slug") String slug) {
        Post post = postRepository.findAll().stream()
                .filter(p -> p.getSlug().equals(slug))
                .findFirst()
                .orElse(null);

        Map<String, Object> response = new HashMap<>();
        if (post != null) {
            post.setNumLikes(post.getNumLikes() + 1);
            postRepository.save(post);
            response.put("success", true);
            response.put("numLikes", post.getNumLikes());
        } else {
            response.put("success", false);
        }
        return response;
    }
}