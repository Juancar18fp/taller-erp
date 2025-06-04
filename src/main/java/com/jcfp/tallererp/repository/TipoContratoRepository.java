package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.model.TipoContrato;
import com.jcfp.tallererp.util.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoContratoRepository extends BaseRepository<TipoContrato, Long> {
    @Query("""
    select e from tipos_contrato e
""")
    Page<TipoContrato> findByFilter(@Param("filter") String filter, Pageable pageable);

}
