package org.example.backend.controller;

import org.example.backend.model.entity.Publisher;
import org.example.backend.service.PublisherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/publishers")
public class PublisherController {
    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPublishers() {
        try {
            List<Publisher> publishers = publisherService.getAllPublishers();
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching publishers: " + e.getMessage());
        }
    }

    @GetMapping("/{publisherId}")
    public ResponseEntity<?> getPublisherById(@PathVariable Integer publisherId) {
        try {
            Publisher publisher = publisherService.getPublisherById(publisherId);
            return ResponseEntity.ok(publisher);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching publisher: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('Admin')")
    @PostMapping
    public ResponseEntity<?> createPublisher(@RequestBody Publisher publisher) {
        try {
            Publisher createdPublisher = publisherService.createPublisher(publisher);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Publisher created successfully with ID: " + createdPublisher.getPublisherId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating publisher: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('Admin')")
    @PutMapping("/{publisherId}")
    public ResponseEntity<?> updatePublisher(@PathVariable Integer publisherId, @RequestBody Publisher publisher) {
        try {
            publisherService.updatePublisher(publisherId, publisher);
            return ResponseEntity.ok("Publisher updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating publisher: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('Admin')")
    @DeleteMapping("/{publisherId}")
    public ResponseEntity<?> deletePublisher(@PathVariable Integer publisherId) {
        try {
            publisherService.deletePublisher(publisherId);
            return ResponseEntity.ok("Publisher deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting publisher: " + e.getMessage());
        }
    }
}

