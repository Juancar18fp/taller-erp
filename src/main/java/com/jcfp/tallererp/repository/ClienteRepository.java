package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    @Query("SELECT c FROM clientes c WHERE " +
            "LOWER(c.razonSocial) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
            "LOWER(c.razonSocial) LIKE LOWER(CONCAT('%', :filtro, '%'))")
    List<Cliente> buscarPorFiltro(@Param("filtro") String filtro);}
