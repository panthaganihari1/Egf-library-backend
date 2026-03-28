package com.egf.library.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String category;

    private String publisher;

    private String isbn;

    private Integer totalCopies = 1;

    private Integer availableCopies = 1;

    @Column(length = 500)
    private String description;

    private String language;

    private Integer publishedYear;

    @Enumerated(EnumType.STRING)
    private BookStatus status = BookStatus.AVAILABLE;

    // ✅ Fixed
    private LocalDateTime createdAt;

    // ✅ Fixed
    private LocalDateTime joinedAt;

    public enum BookStatus {
        AVAILABLE, ISSUED, OUT_OF_STOCK
    }
}