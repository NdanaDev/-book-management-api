package com.example.bookmanagement.repository;

import com.example.bookmanagement.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository  // ← Tells Spring: "This is a repository component"
public interface BookRepository extends JpaRepository<Book, Long> {

    // ========== AUTOMATIC METHODS (Inherited from JpaRepository) ==========
    // You get these for FREE without writing any code:
    // - save(book)              → INSERT or UPDATE
    // - findById(id)            → SELECT by ID
    // - findAll()               → SELECT all books
    // - deleteById(id)          → DELETE by ID
    // - count()                 → COUNT total books
    // - existsById(id)          → Check if book exists


    // ========== CUSTOM QUERY METHODS (Spring generates SQL automatically) ==========

    /**
     * Find book by ISBN
     * Spring generates: SELECT * FROM books WHERE isbn = ?
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Find all books by a specific author
     * Spring generates: SELECT * FROM books WHERE author = ?
     */
    List<Book> findByAuthor(String author);

    /**
     * Find books where title contains the search term (case-insensitive)
     * Spring generates: SELECT * FROM books WHERE LOWER(title) LIKE LOWER(?)
     */
    List<Book> findByTitleContainingIgnoreCase(String title);

    /**
     * Find books published in a specific year
     * Spring generates: SELECT * FROM books WHERE publication_year = ?
     */
    List<Book> findByPublicationYear(Integer year);

    /**
     * Find books published between two years
     * Spring generates: SELECT * FROM books WHERE publication_year BETWEEN ? AND ?
     */
    List<Book> findByPublicationYearBetween(Integer startYear, Integer endYear);

    /**
     * Find books by author, ordered by publication year descending
     * Spring generates: SELECT * FROM books WHERE author = ? ORDER BY publication_year DESC
     */
    List<Book> findByAuthorOrderByPublicationYearDesc(String author);

    /**
     * Check if a book with given ISBN exists
     * Spring generates: SELECT COUNT(*) > 0 FROM books WHERE isbn = ?
     */
    boolean existsByIsbn(String isbn);

    /**
     * Count books by author
     * Spring generates: SELECT COUNT(*) FROM books WHERE author = ?
     */
    long countByAuthor(String author);


    // ========== CUSTOM QUERIES using @Query annotation ==========

    /**
     * Search books by title OR author (more flexible)
     * Custom JPQL query
     */
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Book> searchBooks(@Param("searchTerm") String searchTerm);

    /**
     * Find books published after a certain year
     * Custom JPQL query
     */
    @Query("SELECT b FROM Book b WHERE b.publicationYear > :year ORDER BY b.publicationYear ASC")
    List<Book> findBooksPublishedAfter(@Param("year") Integer year);

    /**
     * Get all unique authors (no duplicates)
     * Custom JPQL query
     */
    @Query("SELECT DISTINCT b.author FROM Book b ORDER BY b.author")
    List<String> findAllUniqueAuthors();

    /**
     * Count total books published in a year range
     * Custom JPQL query
     */
    @Query("SELECT COUNT(b) FROM Book b WHERE b.publicationYear BETWEEN :startYear AND :endYear")
    long countBooksInYearRange(@Param("startYear") Integer startYear, @Param("endYear") Integer endYear);

    /**
     * Using native SQL query (when you need database-specific features)
     * nativeQuery = true means it's actual SQL, not JPQL
     */
    @Query(value = "SELECT * FROM books WHERE publication_year = :year", nativeQuery = true)
    List<Book> findBooksByYearNative(@Param("year") Integer year);
}
