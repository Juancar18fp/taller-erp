package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.entity.TipoContrato;
import com.jcfp.tallererp.util.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoContratoRepository extends BaseRepository<TipoContrato, Long> {
    @Query("""
    select e from tipos_contrato e
""")
    Page<TipoContrato> findByFilter(@Param("filter") String filter, Pageable pageable);
    Optional<TipoContrato> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
