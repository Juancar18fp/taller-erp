package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.entity.ArticuloUsado;
import com.jcfp.tallererp.util.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticuloUsadoRepository extends BaseRepository<ArticuloUsado, Long> {
    @Query("""
      SELECT a FROM articulos_usados a
    """)
    Page<ArticuloUsado> findByFilter(@Param("filter") String filter, Pageable pageable);
    List<ArticuloUsado> findByOrdenTrabajoId(Long ordenTrabajoId);
}
