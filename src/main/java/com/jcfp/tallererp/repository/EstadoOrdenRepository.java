package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.model.EstadoOrden;
import com.jcfp.tallererp.util.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoOrdenRepository extends BaseRepository<EstadoOrden, Long> {
    @Query("""
    select e from estados_ordenes e
""")
    Page<EstadoOrden> findByFilter(@Param("filter") String filter, Pageable pageable);
}
