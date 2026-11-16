package com.blog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "image")
@Getter
@Setter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String file;

    @Min(0)
    @Column(name = "num_likes", nullable = false)
    private Integer numLikes = 0;

    @Min(0)
    @Column(name = "num_views", nullable = false)
    private Integer numViews = 0;

    @Min(0)
    @Column(name = "num_downloads", nullable = false)
    private Integer numDownloads = 0;

    @NotNull(message = "Category is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public Image() {
        this.numLikes = 0;
        this.numViews = 0;
        this.numDownloads = 0;
    }
}