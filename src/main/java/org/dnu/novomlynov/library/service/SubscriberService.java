package org.dnu.novomlynov.library.service;

import org.dnu.novomlynov.library.dto.SubscriberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubscriberService {
    SubscriberDto createSubscriber(SubscriberDto subscriberDto);

    SubscriberDto getSubscriberById(Long id);

    SubscriberDto getSubscriberByLibraryCardNumber(String libraryCardNumber);

    Page<SubscriberDto> getAllSubscribers(Pageable pageable);

    Page<SubscriberDto> searchSubscribersByName(String name, Pageable pageable);

    SubscriberDto updateSubscriber(Long id, SubscriberDto subscriberDto);

    void changeSubscriberActivity(Long id, boolean active);

    void deleteSubscriber(Long id);

    boolean hasOutstandingLoans(Long id);

    List<SubscriberDto> getActiveSubscribers();
}