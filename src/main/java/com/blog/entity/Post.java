package com.blog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    @Column(nullable = false, length = 255)
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "published_at", nullable = false)
    private LocalDateTime publishedAt;

    @NotNull(message = "Post author is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_user_id", nullable = false)
    private User postUser;

    @Column(length = 255, nullable = true)
    private String image;

    @Column(nullable = false, length = 255)
    private String slug;

    @Min(0)
    @Column(name = "num_likes", nullable = false)
    private Integer numLikes = 0;

    @Min(0)
    @Column(name = "num_comments", nullable = false)
    private Integer numComments = 0;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Post() {
        this.publishedAt = LocalDateTime.now();
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }
}