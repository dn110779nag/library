package org.dnu.novomlynov.library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dnu.novomlynov.library.model.LendingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookLendingDto {
    private Long id;

    @NotNull(message = "Book ID is required")
    private Long bookId;

    @NotNull(message = "Subscriber ID is required")
    private Long subscriberId;

    private BookDto book;
    private SubscriberDto subscriber;

    @NotNull(message = "Issue date is required")
    private LocalDate issueDate;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    private LocalDate returnDate;

    private LendingStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}