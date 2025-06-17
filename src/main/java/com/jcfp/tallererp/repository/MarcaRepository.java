package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.entity.Marca;
import com.jcfp.tallererp.util.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarcaRepository extends BaseRepository<Marca, Long> {
    @Query("""
    select e from marcas e
""")
    Page<Marca> findByFilter(@Param("filter") String filter, Pageable pageable);
    List<Marca> findByNombreContainingIgnoreCase(@Param("filter") String filter);
    Optional<Marca> findByNombre(String nombre);
    Optional<Marca> findByNombreIgnoreCase(String nombre);
}
