package org.dnu.novomlynov.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private Long id;

    @NotBlank(message = "Book title is required")
    private String title;

    private String isbn;

    private Integer publicationYear;

    @Min(value = 0, message = "Total copies cannot be negative")
    private Integer totalCopies;

    private Integer availableCopies;

    @NotEmpty(message = "At least one author is required")
    @Builder.Default
    private Set<Long> authorIds = new HashSet<>();

    @NotEmpty(message = "At least one category is required")
    @Builder.Default
    private Set<Long> categoryIds = new HashSet<>();

    @Builder.Default
    private Set<AuthorDto> authors = new HashSet<>();
    @Builder.Default
    private Set<CategoryDto> categories = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}