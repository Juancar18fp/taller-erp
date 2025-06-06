package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.entity.Puesto;
import com.jcfp.tallererp.util.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PuestoRepository extends BaseRepository<Puesto, Long> {
    @Query("""
    select e from puestos e
""")
    Page<Puesto> findByFilter(@Param("filter") String filter, Pageable pageable);
    Optional<Puesto> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
