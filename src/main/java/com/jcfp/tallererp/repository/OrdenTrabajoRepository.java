package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.entity.OrdenTrabajo;
import com.jcfp.tallererp.util.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenTrabajoRepository extends BaseRepository<OrdenTrabajo, Long> {
    @Query("""
    SELECT DISTINCT o FROM ordenes o
    LEFT JOIN o.vehiculo v
    LEFT JOIN v.cliente c
    LEFT JOIN o.empleadoAsignado e
    LEFT JOIN o.estadoOrden est
    WHERE (
        LOWER(o.codigoOrden) LIKE LOWER(CONCAT('%', :filter, '%')) OR
        LOWER(v.matricula) LIKE LOWER(CONCAT('%', :filter, '%')) OR
        LOWER(v.marca.nombre) LIKE LOWER(CONCAT('%', :filter, '%')) OR
        LOWER(v.modelo.nombre) LIKE LOWER(CONCAT('%', :filter, '%')) OR
        LOWER(c.nombre) LIKE LOWER(CONCAT('%', :filter, '%')) OR
        LOWER(e.nombre) LIKE LOWER(CONCAT('%', :filter, '%')) OR
        LOWER(est.nombre) LIKE LOWER(CONCAT('%', :filter, '%'))
    )
""")
    Page<OrdenTrabajo> findByFilter(@Param("filter") String filter, Pageable pageable);
    @Query("SELECT o FROM ordenes o WHERE o.codigoOrden LIKE :patron ORDER BY o.codigoOrden DESC")
    List<OrdenTrabajo> findByCodigoOrdenLikeOrderByCodigoOrdenDesc(@Param("patron") String patron);
    Optional<OrdenTrabajo> findByCodigoOrden(String codigoOrden);
}
