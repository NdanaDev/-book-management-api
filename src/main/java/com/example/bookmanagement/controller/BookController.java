package com.example.bookmanagement.controller;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController  // ← Tells Spring: "This handles REST API requests"
@RequestMapping("/api/books")  // ← Base URL for all endpoints in this controller
@CrossOrigin(origins = "*")  // ← Allow requests from any origin (for development)
public class BookController {

    // ========== DEPENDENCY INJECTION ==========

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }


    // ========== CREATE ENDPOINTS ==========

    /**
     * CREATE - Add a new book
     *
     * Endpoint: POST http://localhost:8082/api/books
     * Request Body (JSON):
     * {
     *   "title": "Spring Boot Guide",
     *   "author": "John Doe",
     *   "isbn": "978-1234567890",
     *   "publicationYear": 2024
     * }
     *
     * Response: 201 CREATED + Book object with generated ID
     */
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        try {
            Book savedBook = bookService.addBook(book);
            return new ResponseEntity<>(savedBook, HttpStatus.CREATED);  // 201
        } catch (IllegalArgumentException e) {
            // Business logic error (e.g., ISBN already exists)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // 400
        } catch (Exception e) {
            // Unexpected error
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // 500
        }
    }

    /**
     * CREATE - Add multiple books at once
     *
     * Endpoint: POST http://localhost:8082/api/books/bulk
     * Request Body: Array of book objects
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<Book>> createBooks(@RequestBody List<Book> books) {
        try {
            List<Book> savedBooks = bookService.addBooks(books);
            return new ResponseEntity<>(savedBooks, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    // ========== READ ENDPOINTS ==========

    /**
     * READ - Get all books
     *
     * Endpoint: GET http://localhost:8082/api/books
     * Response: 200 OK + Array of all books
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        try {
            List<Book> books = bookService.getAllBooks();

            if (books.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // 204 - No books found
            }

            return new ResponseEntity<>(books, HttpStatus.OK);  // 200
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * READ - Get a single book by ID
     *
     * Endpoint: GET http://localhost:8082/api/books/1
     * Response: 200 OK + Book object OR 404 NOT FOUND
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable("id") Long id) {
        try {
            Optional<Book> book = bookService.getBookById(id);

            if (book.isPresent()) {
                return new ResponseEntity<>(book.get(), HttpStatus.OK);  // 200
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 404
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * READ - Get book by ISBN
     *
     * Endpoint: GET http://localhost:8082/api/books/isbn/978-1234567890
     * Response: 200 OK + Book object OR 404 NOT FOUND
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable("isbn") String isbn) {
        try {
            Optional<Book> book = bookService.getBookByIsbn(isbn);

            return book.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * READ - Search books by title (partial match, case-insensitive)
     *
     * Endpoint: GET http://localhost:8082/api/books/search?title=spring
     * Query Parameter: title (required)
     * Response: 200 OK + Array of matching books
     */
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooksByTitle(
            @RequestParam("title") String title) {
        try {
            List<Book> books = bookService.searchBooksByTitle(title);

            if (books.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * READ - Advanced search (title OR author)
     *
     * Endpoint: GET http://localhost:8082/api/books/search/advanced?term=john
     * Query Parameter: term (searches in both title and author)
     */
    @GetMapping("/search/advanced")
    public ResponseEntity<List<Book>> advancedSearch(
            @RequestParam("term") String searchTerm) {
        try {
            List<Book> books = bookService.searchBooks(searchTerm);

            if (books.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>((HttpHeaders) null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * READ - Get books by author
     *
     * Endpoint: GET http://localhost:8082/api/books/author/John Doe
     */
    @GetMapping("/author/{author}")
    public ResponseEntity<List<Book>> getBooksByAuthor(
            @PathVariable("author") String author) {
        try {
            List<Book> books = bookService.getBooksByAuthor(author);

            if (books.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>((HttpHeaders) null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * READ - Get books by publication year
     *
     * Endpoint: GET http://localhost:8082/api/books/year/2024
     */
    @GetMapping("/year/{year}")
    public ResponseEntity<List<Book>> getBooksByYear(
            @PathVariable("year") Integer year) {
        try {
            List<Book> books = bookService.getBooksByYear(year);

            if (books.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * READ - Get books by year range
     *
     * Endpoint: GET http://localhost:8082/api/books/year-range?start=2020&end=2024
     */
    @GetMapping("/year-range")
    public ResponseEntity<List<Book>> getBooksByYearRange(
            @RequestParam("start") Integer startYear,
            @RequestParam("end") Integer endYear) {
        try {
            List<Book> books = bookService.getBooksByYearRange(startYear, endYear);

            if (books.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * READ - Get all unique authors
     *
     * Endpoint: GET http://localhost:8082/api/books/authors
     */
    @GetMapping("/authors")
    public ResponseEntity<List<String>> getAllAuthors() {
        try {
            List<String> authors = bookService.getAllAuthors();

            if (authors.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(authors, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * READ - Get book count
     *
     * Endpoint: GET http://localhost:8082/api/books/count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalBookCount() {
        try {
            long count = bookService.getTotalBookCount();
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * READ - Get statistics
     *
     * Endpoint: GET http://localhost:8082/api/books/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<String> getStatistics() {
        try {
            String stats = bookService.getBookStatistics();
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // ========== UPDATE ENDPOINTS ==========

    /**
     * UPDATE - Update an existing book (full update)
     *
     * Endpoint: PUT http://localhost:8082/api/books/1
     * Request Body: Complete book object with updated fields
     * Response: 200 OK + Updated book OR 404 NOT FOUND
     */
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @PathVariable("id") Long id,
            @RequestBody Book book) {
        try {
            Book updatedBook = bookService.updateBook(id, book);
            return new ResponseEntity<>(updatedBook, HttpStatus.OK);  // 200
        } catch (IllegalArgumentException e) {
            // Validation error (e.g., ISBN already exists)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // 400
        } catch (RuntimeException e) {
            // Book not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 404
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * UPDATE - Partial update (update only provided fields)
     *
     * Endpoint: PATCH http://localhost:8082/api/books/1?title=New Title
     * Query Parameters: title, author, isbn, year (all optional)
     * Response: 200 OK + Updated book
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Book> partialUpdateBook(
            @PathVariable("id") Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) Integer year) {
        try {
            Book updatedBook = bookService.partialUpdateBook(id, title, author, isbn, year);
            return new ResponseEntity<>(updatedBook, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // ========== DELETE ENDPOINTS ==========

    /**
     * DELETE - Delete a book by ID
     *
     * Endpoint: DELETE http://localhost:8082/api/books/1
     * Response: 204 NO CONTENT (success) OR 404 NOT FOUND
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteBook(@PathVariable("id") Long id) {
        try {
            bookService.deleteBook(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // 204 - Successfully deleted
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 404 - Book not found
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * DELETE - Delete book by ISBN
     *
     * Endpoint: DELETE http://localhost:8082/api/books/isbn/978-1234567890
     */
    @DeleteMapping("/isbn/{isbn}")
    public ResponseEntity<HttpStatus> deleteBookByIsbn(@PathVariable("isbn") String isbn) {
        try {
            bookService.deleteBookByIsbn(isbn);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * DELETE - Delete all books by an author
     *
     * Endpoint: DELETE http://localhost:8082/api/books/author/John Doe
     * Response: 200 OK with count of deleted books
     */
    @DeleteMapping("/author/{author}")
    public ResponseEntity<String> deleteBooksByAuthor(@PathVariable("author") String author) {
        try {
            int deletedCount = bookService.deleteBooksByAuthor(author);
            return new ResponseEntity<>(
                    "Deleted " + deletedCount + " books by " + author,
                    HttpStatus.OK
            );
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * DELETE - Delete all books (USE WITH CAUTION!)
     *
     * Endpoint: DELETE http://localhost:8082/api/books/all
     */
    @DeleteMapping("/all")
    public ResponseEntity<HttpStatus> deleteAllBooks() {
        try {
            bookService.deleteAllBooks();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // ========== UTILITY ENDPOINTS ==========

    /**
     * Health check endpoint
     *
     * Endpoint: GET http://localhost:8082/api/books/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("Book API is running!", HttpStatus.OK);
    }
}
