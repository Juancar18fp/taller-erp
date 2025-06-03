package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.model.Contrato;
import com.jcfp.tallererp.util.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface ContratoRepository extends BaseRepository<Contrato, Long> {
    @Query("""
      SELECT c FROM contratos c
    """)
    Page<Contrato> findByFilter(@Param("filter") String filter, Pageable pageable);

    List<Contrato> findByActivoTrue();}
