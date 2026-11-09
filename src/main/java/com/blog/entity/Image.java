package com.blog.entity;

import jakarta.persistence.*;
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

    @Column(name = "num_likes", nullable = false)
    private Integer numLikes = 0;

    @Column(name = "num_views", nullable = false)
    private Integer numViews = 0;

    @Column(name = "num_downloads", nullable = false)
    private Integer numDownloads = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public Image() {
        this.numLikes = 0;
        this.numViews = 0;
        this.numDownloads = 0;
    }
}