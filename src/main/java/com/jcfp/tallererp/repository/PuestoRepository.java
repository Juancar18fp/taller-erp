package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.model.Puesto;
import com.jcfp.tallererp.util.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PuestoRepository extends BaseRepository<Puesto, Long> {
    @Query("""
    select e from puestos e
""")
    Page<Puesto> findByFilter(@Param("filter") String filter, Pageable pageable);
}
