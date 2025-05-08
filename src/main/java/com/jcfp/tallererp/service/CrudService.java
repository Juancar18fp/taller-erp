package com.jcfp.tallererp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CrudService<T,ID> {
    Page<T> findAllFiltered(String filter, Pageable pageable);
    List<T> findAll();
    Optional<T> findById(ID id);
    T create(T entity);
    T update(ID id, T entity);
    void delete(ID id);
}
