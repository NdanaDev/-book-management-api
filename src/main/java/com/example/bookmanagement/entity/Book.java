package com.example.bookmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity  // ← Tells Spring: "This is a database table"
@Table(name = "books")  // ← Optional: specify table name (default would be "book")
public class Book {

    // ========== FIELDS (Columns in database) ==========

    @Id  // ← Marks this as the PRIMARY KEY (unique identifier)
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ← Auto-increment (1, 2, 3...)
    private Long id;

    @Column(nullable = false, length = 200)  // ← This field is REQUIRED, max 200 chars
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(unique = true, length = 20)  // ← ISBN must be UNIQUE (no duplicates)
    private String isbn;

    @Column(name = "publication_year")  // ← Column name in DB (snake_case)
    private Integer publicationYear;

    @Column(name = "created_at", updatable = false)  // ← Set once, never updated
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    // ========== CONSTRUCTORS ==========

    // Default constructor (REQUIRED by JPA)
    public Book() {
    }

    // Constructor with all fields (except id - that's auto-generated)
    public Book(String title, String author, String isbn, Integer publicationYear) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
    }


    // ========== LIFECYCLE CALLBACKS ==========

    @PrePersist  // ← Runs automatically BEFORE saving to database
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate  // ← Runs automatically BEFORE updating in database
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    // ========== GETTERS and SETTERS ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


    // ========== UTILITY METHODS ==========

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", publicationYear=" + publicationYear +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
