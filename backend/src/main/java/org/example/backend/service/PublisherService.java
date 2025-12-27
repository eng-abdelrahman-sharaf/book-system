package org.example.backend.service;

import org.example.backend.Repository.PublisherRepository;
import org.example.backend.model.entity.Publisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublisherService {
    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    public List<Publisher> getAllPublishers() {
        return publisherRepository.findAll();
    }

    public Publisher getPublisherById(Integer publisherId) {
        return publisherRepository.findById(publisherId)
                .orElseThrow(() -> new IllegalArgumentException("Publisher not found with ID: " + publisherId));
    }

    public Publisher createPublisher(Publisher publisher) {
        validatePublisher(publisher);
        return publisherRepository.create(publisher);
    }

    public Publisher updatePublisher(Integer publisherId, Publisher publisher) {
        if (!publisherRepository.existsById(publisherId)) {
            throw new IllegalArgumentException("Publisher not found with ID: " + publisherId);
        }
        validatePublisher(publisher);
        publisher.setPublisherId(publisherId);
        return publisherRepository.update(publisher);
    }

    public void deletePublisher(Integer publisherId) {
        publisherRepository.delete(publisherId);
    }

    private void validatePublisher(Publisher publisher) {
        if (publisher.getName() == null || publisher.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Publisher name is required");
        }
        if (publisher.getName().length() > 100) {
            throw new IllegalArgumentException("Publisher name must be 100 characters or less");
        }
        if (publisher.getAddress() != null && publisher.getAddress().length() > 255) {
            throw new IllegalArgumentException("Publisher address must be 255 characters or less");
        }
        if (publisher.getPhone() != null && publisher.getPhone().length() > 20) {
            throw new IllegalArgumentException("Publisher phone must be 20 characters or less");
        }
    }
}

