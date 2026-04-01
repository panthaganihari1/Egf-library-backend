package com.egf.library.controller;

import com.egf.library.model.Book;
import com.egf.library.repository.BookRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam String query) {
        return bookRepository.searchBooks(query);
    }

    @GetMapping("/category/{category}")
    public List<Book> getByCategory(@PathVariable String category) {
        return bookRepository.findByCategory(category);
    }

    @PostMapping
    public ResponseEntity<?> createBook(
            @RequestPart("book") String bookJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            Book book = objectMapper.readValue(bookJson, Book.class);

            if (file != null && !file.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                book.setCoverImageUrl(uploadResult.get("url").toString());
            }

            return ResponseEntity.ok(bookRepository.save(book));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating book: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(
            @PathVariable Long id,
            @RequestPart("book") String bookJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            Book updated = objectMapper.readValue(bookJson, Book.class);

            return bookRepository.findById(id).map(book -> {
                book.setTitle(updated.getTitle());
                book.setAuthor(updated.getAuthor());
                book.setCategory(updated.getCategory());
                book.setPublisher(updated.getPublisher());
                book.setIsbn(updated.getIsbn());
                book.setDescription(updated.getDescription());
                book.setLanguage(updated.getLanguage());
                book.setPublishedYear(updated.getPublishedYear());
                book.setTotalCopies(updated.getTotalCopies());
                book.setOwner(updated.getOwner());   // ✅ NEW — save owner field

                if (file != null && !file.isEmpty()) {
                    try {
                        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                        book.setCoverImageUrl(uploadResult.get("url").toString());
                    } catch (Exception e) {
                        throw new RuntimeException("Image upload failed: " + e.getMessage());
                    }
                }

                return ResponseEntity.ok(bookRepository.save(book));

            }).orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating book: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Book deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }
}