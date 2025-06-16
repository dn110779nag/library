package org.dnu.novomlynov.library.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dnu.novomlynov.library.dto.SubscriberDto;
import org.dnu.novomlynov.library.service.SubscriberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscribers")
@RequiredArgsConstructor
public class SubscriberController {

    private final SubscriberService subscriberService;

    @PostMapping
    public ResponseEntity<SubscriberDto> createSubscriber(@Valid @RequestBody SubscriberDto subscriberDto) {
        return new ResponseEntity<>(subscriberService.createSubscriber(subscriberDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriberDto> getSubscriberById(@PathVariable Long id) {
        return ResponseEntity.ok(subscriberService.getSubscriberById(id));
    }

    @GetMapping("/card/{cardNumber}")
    public ResponseEntity<SubscriberDto> getSubscriberByLibraryCardNumber(@PathVariable String cardNumber) {
        return ResponseEntity.ok(subscriberService.getSubscriberByLibraryCardNumber(cardNumber));
    }

    @GetMapping
    public ResponseEntity<Page<SubscriberDto>> getAllSubscribers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(subscriberService.getAllSubscribers(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<SubscriberDto>> searchSubscribersByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(subscriberService.searchSubscribersByName(name, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriberDto> updateSubscriber(
            @PathVariable Long id,
            @Valid @RequestBody SubscriberDto subscriberDto) {
        return ResponseEntity.ok(subscriberService.updateSubscriber(id, subscriberDto));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> changeSubscriberActivity(
            @PathVariable Long id,
            @RequestParam boolean active) {
        subscriberService.changeSubscriberActivity(id, active);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscriber(@PathVariable Long id) {
        subscriberService.deleteSubscriber(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/has-loans")
    public ResponseEntity<Boolean> hasOutstandingLoans(@PathVariable Long id) {
        return ResponseEntity.ok(subscriberService.hasOutstandingLoans(id));
    }

    @GetMapping("/active")
    public ResponseEntity<List<SubscriberDto>> getActiveSubscribers() {
        return ResponseEntity.ok(subscriberService.getActiveSubscribers());
    }
}