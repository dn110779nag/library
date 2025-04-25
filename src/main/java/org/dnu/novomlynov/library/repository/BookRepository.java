package org.dnu.novomlynov.library.repository;

import org.dnu.novomlynov.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Optional<Book> findByIsbn(String isbn);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    Page<Book> findByAuthorNameContainingIgnoreCase(@Param("authorName") String authorName, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN b.categories c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))")
    Page<Book> findByCategoryNameContainingIgnoreCase(@Param("categoryName") String categoryName, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "b.isbn LIKE CONCAT('%', :searchTerm, '%') OR " +
            "EXISTS (SELECT 1 FROM b.authors a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "EXISTS (SELECT 1 FROM b.categories c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Book> search(@Param("searchTerm") String searchTerm, Pageable pageable);

    boolean existsByIsbn(String isbn);
}