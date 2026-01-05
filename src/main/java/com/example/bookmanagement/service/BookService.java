package com.example.bookmanagement.service;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service  // ← Tells Spring: "This is a service component"
@Transactional  // ← Database transactions (explained below)
public class BookService {

    // ========== DEPENDENCY INJECTION ==========

    private final BookRepository bookRepository;

    /**
     * Constructor injection (RECOMMENDED approach)
     * Spring automatically provides BookRepository
     */
    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }


    // ========== CREATE OPERATIONS ==========

    /**
     * Add a new book to the database
     * Business logic: Check if ISBN already exists
     *
     * @param book - The book to save
     * @return Saved book with generated ID
     * @throws IllegalArgumentException if ISBN already exists
     */
    public Book addBook(Book book) {
        // Business Rule: ISBN must be unique
        if (book.getIsbn() != null && bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException(
                    "Book with ISBN " + book.getIsbn() + " already exists!"
            );
        }

        // Business Rule: Title and Author are required
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title is required!");
        }

        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Book author is required!");
        }

        // All validations passed - save the book
        return bookRepository.save(book);
    }

    /**
     * Add multiple books at once
     * Useful for bulk import
     */
    public List<Book> addBooks(List<Book> books) {
        // Validate each book before saving
        for (Book book : books) {
            if (bookRepository.existsByIsbn(book.getIsbn())) {
                throw new IllegalArgumentException(
                        "Book with ISBN " + book.getIsbn() + " already exists!"
                );
            }
        }

        return bookRepository.saveAll(books);
    }


    // ========== READ OPERATIONS ==========

    /**
     * Get all books from database
     *
     * @return List of all books (empty list if none found)
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Get a single book by ID
     *
     * @param id - Book ID to search for
     * @return Optional containing book if found, empty if not
     */
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    /**
     * Get book by ID or throw exception if not found
     * Useful when you expect the book to exist
     *
     * @param id - Book ID
     * @return Book object
     * @throws RuntimeException if book not found
     */
    public Book getBookByIdOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    /**
     * Find book by ISBN
     *
     * @param isbn - ISBN to search for
     * @return Optional containing book if found
     */
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    /**
     * Get all books by a specific author
     *
     * @param author - Author name
     * @return List of books (empty if none found)
     */
    public List<Book> getBooksByAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be empty!");
        }
        return bookRepository.findByAuthor(author);
    }

    /**
     * Search books by title (partial match, case-insensitive)
     *
     * @param title - Search term
     * @return List of matching books
     */
    public List<Book> searchBooksByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be empty!");
        }
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Get books published in a specific year
     *
     * @param year - Publication year
     * @return List of books from that year
     */
    public List<Book> getBooksByYear(Integer year) {
        if (year == null || year < 1000 || year > 2100) {
            throw new IllegalArgumentException("Invalid publication year!");
        }
        return bookRepository.findByPublicationYear(year);
    }

    /**
     * Get books published between two years
     *
     * @param startYear - Start of range
     * @param endYear - End of range
     * @return List of books in range
     */
    public List<Book> getBooksByYearRange(Integer startYear, Integer endYear) {
        if (startYear == null || endYear == null) {
            throw new IllegalArgumentException("Start year and end year are required!");
        }

        if (startYear > endYear) {
            throw new IllegalArgumentException("Start year must be before end year!");
        }

        return bookRepository.findByPublicationYearBetween(startYear, endYear);
    }

    /**
     * Search books by title OR author
     *
     * @param searchTerm - Term to search for
     * @return List of matching books
     */
    public List<Book> searchBooks(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be empty!");
        }
        return bookRepository.searchBooks(searchTerm);
    }

    /**
     * Get all unique authors in the database
     *
     * @return List of author names (no duplicates)
     */
    public List<String> getAllAuthors() {
        return bookRepository.findAllUniqueAuthors();
    }

    /**
     * Count total books in database
     *
     * @return Total number of books
     */
    public long getTotalBookCount() {
        return bookRepository.count();
    }

    /**
     * Count books by a specific author
     *
     * @param author - Author name
     * @return Number of books by this author
     */
    public long getBookCountByAuthor(String author) {
        return bookRepository.countByAuthor(author);
    }

    /**
     * Check if a book exists by ID
     *
     * @param id - Book ID
     * @return true if exists, false otherwise
     */
    public boolean bookExists(Long id) {
        return bookRepository.existsById(id);
    }


    // ========== UPDATE OPERATIONS ==========

    /**
     * Update an existing book
     * Business logic: Check if book exists, validate data
     *
     * @param id - ID of book to update
     * @param updatedBook - New book data
     * @return Updated book
     * @throws RuntimeException if book not found
     */
    public Book updateBook(Long id, Book updatedBook) {
        // Check if book exists
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        // Business Rule: If ISBN is being changed, check if new ISBN already exists
        if (updatedBook.getIsbn() != null &&
                !updatedBook.getIsbn().equals(existingBook.getIsbn())) {

            if (bookRepository.existsByIsbn(updatedBook.getIsbn())) {
                throw new IllegalArgumentException(
                        "Another book with ISBN " + updatedBook.getIsbn() + " already exists!"
                );
            }
        }

        // Update fields (only if provided)
        if (updatedBook.getTitle() != null && !updatedBook.getTitle().trim().isEmpty()) {
            existingBook.setTitle(updatedBook.getTitle());
        }

        if (updatedBook.getAuthor() != null && !updatedBook.getAuthor().trim().isEmpty()) {
            existingBook.setAuthor(updatedBook.getAuthor());
        }

        if (updatedBook.getIsbn() != null) {
            existingBook.setIsbn(updatedBook.getIsbn());
        }

        if (updatedBook.getPublicationYear() != null) {
            existingBook.setPublicationYear(updatedBook.getPublicationYear());
        }

        // Save and return updated book
        return bookRepository.save(existingBook);
    }

    /**
     * Partial update - update only specific fields
     * More flexible than full update
     *
     * @param id - Book ID
     * @param title - New title (null to keep existing)
     * @param author - New author (null to keep existing)
     * @param isbn - New ISBN (null to keep existing)
     * @param year - New year (null to keep existing)
     * @return Updated book
     */
    public Book partialUpdateBook(Long id, String title, String author,
                                  String isbn, Integer year) {
        Book book = getBookByIdOrThrow(id);

        if (title != null && !title.trim().isEmpty()) {
            book.setTitle(title);
        }

        if (author != null && !author.trim().isEmpty()) {
            book.setAuthor(author);
        }

        if (isbn != null) {
            // Check ISBN uniqueness
            if (!isbn.equals(book.getIsbn()) && bookRepository.existsByIsbn(isbn)) {
                throw new IllegalArgumentException("ISBN already exists!");
            }
            book.setIsbn(isbn);
        }

        if (year != null) {
            book.setPublicationYear(year);
        }

        return bookRepository.save(book);
    }


    // ========== DELETE OPERATIONS ==========

    /**
     * Delete a book by ID
     * Business logic: Check if book exists before deleting
     *
     * @param id - Book ID to delete
     * @throws RuntimeException if book not found
     */
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete: Book not found with id: " + id);
        }

        // Could add more business logic here:
        // - Check if book is borrowed
        // - Check if book has reservations
        // - Send notification about deletion

        bookRepository.deleteById(id);
    }

    /**
     * Delete book by ISBN
     *
     * @param isbn - ISBN of book to delete
     * @throws RuntimeException if book not found
     */
    public void deleteBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new RuntimeException("Book not found with ISBN: " + isbn));

        bookRepository.delete(book);
    }

    /**
     * Delete all books by an author
     * Use with caution!
     *
     * @param author - Author name
     * @return Number of books deleted
     */
    public int deleteBooksByAuthor(String author) {
        List<Book> books = bookRepository.findByAuthor(author);

        if (books.isEmpty()) {
            throw new RuntimeException("No books found by author: " + author);
        }

        bookRepository.deleteAll(books);
        return books.size();
    }

    /**
     * Delete all books (DANGEROUS - use only for testing!)
     */
    public void deleteAllBooks() {
        bookRepository.deleteAll();
    }


    // ========== BUSINESS LOGIC / UTILITY METHODS ==========

    /**
     * Get statistics about books in the database
     * Example of a method that uses multiple repository calls
     *
     * @return String with statistics
     */
    public String getBookStatistics() {
        long totalBooks = bookRepository.count();
        List<String> authors = bookRepository.findAllUniqueAuthors();

        return String.format(
                "Total Books: %d | Unique Authors: %d",
                totalBooks,
                authors.size()
        );
    }

    /**
     * Check if database is empty
     *
     * @return true if no books exist
     */
    public boolean isDatabaseEmpty() {
        return bookRepository.count() == 0;
    }

    /**
     * Validate book data
     * Can be called before saving/updating
     *
     * @param book - Book to validate
     * @return true if valid, throws exception otherwise
     */
    private boolean validateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null!");
        }

        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title is required!");
        }

        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Book author is required!");
        }

        if (book.getPublicationYear() != null) {
            if (book.getPublicationYear() < 1000 || book.getPublicationYear() > 2100) {
                throw new IllegalArgumentException("Invalid publication year!");
            }
        }

        return true;
    }
}
