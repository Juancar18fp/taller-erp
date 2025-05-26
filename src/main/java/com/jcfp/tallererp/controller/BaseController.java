package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.dto.PaginatedResponse;
import com.jcfp.tallererp.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

public abstract class BaseController<T, S extends CrudService<T, Long>> {

    protected final S service;

    public BaseController(S service) {
        this.service = service;
    }
    @GetMapping("/{id}")
    public ResponseEntity<T> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<T>> getAll() {
        List<T> clientes = service.findAll();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<T>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) String filter) {

        PageRequest pageable = buildPageRequest(page, size, sortBy, direction);
        Page<T> resultPage = service.findAllFiltered(filter, pageable);

        PaginatedResponse<T> response = new PaginatedResponse<>(
                resultPage.getContent(),
                resultPage.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<T> create(@RequestBody T dto) {
        T created = service.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<T> update(@PathVariable Long id, @RequestBody T dto) {
        Optional<T> existingEntity = service.findById(id);

        if (existingEntity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        T updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    protected PageRequest buildPageRequest( int page, int size, String sortBy, String direction) {
        PageRequest pageable = PageRequest.of(page - 1, size);

        if (sortBy != null && direction != null) {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            pageable = PageRequest.of(page - 1, size, Sort.by(sortDirection, sortBy));
        }

        return pageable;
    }

}