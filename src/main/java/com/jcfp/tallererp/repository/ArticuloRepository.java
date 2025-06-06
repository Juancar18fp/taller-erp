package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.entity.Articulo;
import com.jcfp.tallererp.util.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticuloRepository extends BaseRepository<Articulo, Long> {
    @Query("""
              SELECT a FROM articulos a
              WHERE CAST(a.id AS string)  = :filter
                 OR LOWER(a.descripcion)    LIKE LOWER(CONCAT('%', :filter, '%'))
                 OR LOWER(a.proveedor   )      LIKE LOWER(CONCAT('%', :filter, '%'))
            """)
    Page<Articulo> findByFilter(@Param("filter") String filter, Pageable pageable);
    List<Articulo> findByDescripcionContainingIgnoreCase(@Param("descripcion") String descripcion);
    Optional<Articulo> findByDescripcion(String descripcion);}
