package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.model.OrdenTrabajo;
import com.jcfp.tallererp.util.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenTrabajoRepository extends BaseRepository<OrdenTrabajo, Long> {
    @Query("""
              SELECT o FROM ordenes o
            """)
    Page<OrdenTrabajo> findByFilter(@Param("filter") String filter, Pageable pageable);
}
