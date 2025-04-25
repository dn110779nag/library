package org.dnu.novomlynov.library.service.impl;

import lombok.RequiredArgsConstructor;
import org.dnu.novomlynov.library.dto.SubscriberDto;
import org.dnu.novomlynov.library.exception.ResourceNotFoundException;
import org.dnu.novomlynov.library.model.LendingStatus;
import org.dnu.novomlynov.library.model.Subscriber;
import org.dnu.novomlynov.library.repository.BookLendingRepository;
import org.dnu.novomlynov.library.repository.SubscriberRepository;
import org.dnu.novomlynov.library.service.SubscriberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriberServiceImpl implements SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final BookLendingRepository bookLendingRepository;

    @Override
    @Transactional
    public SubscriberDto createSubscriber(SubscriberDto subscriberDto) {
        if (subscriberRepository.existsByLibraryCardNumber(subscriberDto.getLibraryCardNumber())) {
            throw new IllegalArgumentException("Subscriber with this library card number already exists");
        }

        if (subscriberRepository.existsByEmail(subscriberDto.getEmail())) {
            throw new IllegalArgumentException("Subscriber with this email already exists");
        }

        Subscriber subscriber = mapToEntity(subscriberDto);
        subscriber.setActive(true);
        Subscriber savedSubscriber = subscriberRepository.save(subscriber);
        return mapToDto(savedSubscriber);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriberDto getSubscriberById(Long id) {
        return subscriberRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriberDto getSubscriberByLibraryCardNumber(String libraryCardNumber) {
        return subscriberRepository.findByLibraryCardNumber(libraryCardNumber)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscriber not found with library card number: " + libraryCardNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubscriberDto> getAllSubscribers(Pageable pageable) {
        return subscriberRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubscriberDto> searchSubscribersByName(String name, Pageable pageable) {
        return subscriberRepository.findByNameContainingIgnoreCase(name, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional
    public SubscriberDto updateSubscriber(Long id, SubscriberDto subscriberDto) {
        Subscriber subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber not found with id: " + id));

        // Check if another subscriber already has the requested library card number
        if (!subscriber.getLibraryCardNumber().equals(subscriberDto.getLibraryCardNumber()) &&
                subscriberRepository.existsByLibraryCardNumber(subscriberDto.getLibraryCardNumber())) {
            throw new IllegalArgumentException("Another subscriber with this library card number already exists");
        }

        // Check if another subscriber already has the requested email
        if (!subscriber.getEmail().equals(subscriberDto.getEmail()) &&
                subscriberRepository.existsByEmail(subscriberDto.getEmail())) {
            throw new IllegalArgumentException("Another subscriber with this email already exists");
        }

        subscriber.setName(subscriberDto.getName());
        subscriber.setEmail(subscriberDto.getEmail());
        subscriber.setPhoneNumber(subscriberDto.getPhoneNumber());
        subscriber.setLibraryCardNumber(subscriberDto.getLibraryCardNumber());

        Subscriber updatedSubscriber = subscriberRepository.save(subscriber);
        return mapToDto(updatedSubscriber);
    }

    @Override
    @Transactional
    public void changeSubscriberActivity(Long id, boolean active) {
        Subscriber subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber not found with id: " + id));

        // If trying to deactivate a subscriber with outstanding loans
        if (!active && hasOutstandingLoans(id)) {
            throw new IllegalStateException("Cannot deactivate subscriber with outstanding book loans");
        }

        subscriber.setActive(active);
        subscriberRepository.save(subscriber);
    }

    @Override
    @Transactional
    public void deleteSubscriber(Long id) {
        if (!subscriberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subscriber not found with id: " + id);
        }

        if (hasOutstandingLoans(id)) {
            throw new IllegalStateException("Cannot delete subscriber with outstanding book loans");
        }

        subscriberRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasOutstandingLoans(Long id) {
        return bookLendingRepository.countCurrentBorrowingsForSubscriber(id) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriberDto> getActiveSubscribers() {
        return subscriberRepository.findByActive(true).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private SubscriberDto mapToDto(Subscriber subscriber) {
        return SubscriberDto.builder()
                .id(subscriber.getId())
                .name(subscriber.getName())
                .email(subscriber.getEmail())
                .phoneNumber(subscriber.getPhoneNumber())
                .libraryCardNumber(subscriber.getLibraryCardNumber())
                .active(subscriber.isActive())
                .createdAt(subscriber.getCreatedAt())
                .updatedAt(subscriber.getUpdatedAt())
                .build();
    }

    private Subscriber mapToEntity(SubscriberDto subscriberDto) {
        return Subscriber.builder()
                .id(subscriberDto.getId())
                .name(subscriberDto.getName())
                .email(subscriberDto.getEmail())
                .phoneNumber(subscriberDto.getPhoneNumber())
                .libraryCardNumber(subscriberDto.getLibraryCardNumber())
                .active(subscriberDto.isActive())
                .build();
    }
}