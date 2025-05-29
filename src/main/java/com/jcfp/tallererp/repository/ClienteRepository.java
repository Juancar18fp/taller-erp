package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends BaseRepository<Cliente, Long> {
    @Query("""
      SELECT c FROM clientes c
      WHERE LOWER(c.nombre)     LIKE LOWER(CONCAT('%', :filter, '%'))
         OR LOWER(c.titular)    LIKE LOWER(CONCAT('%', :filter, '%'))
         OR LOWER(c.documento)  LIKE LOWER(CONCAT('%', :filter, '%'))
         OR LOWER(c.poblacion)  LIKE LOWER(CONCAT('%', :filter, '%'))
         OR LOWER(c.email)      LIKE LOWER(CONCAT('%', :filter, '%'))
         OR LOWER(c.telefono)   LIKE LOWER(CONCAT('%', :filter, '%'))
    """)
    Page<Cliente> findByFilter(@Param("filter") String filter, Pageable pageable);
    List<Cliente> findByNombreContainingIgnoreCase(@Param("filter") String filter);
    }
