package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.model.JornadaLaboral;
import com.jcfp.tallererp.util.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JornadaLaboralRepository extends BaseRepository<JornadaLaboral, Long> {
    @Query("""
    select e from jornadas_laborales e
""")
    Page<JornadaLaboral> findByFilter(@Param("filter") String filter, Pageable pageable);

}
