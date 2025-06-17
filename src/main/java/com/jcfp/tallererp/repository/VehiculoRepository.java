package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.entity.Vehiculo;
import com.jcfp.tallererp.util.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends BaseRepository<Vehiculo, Long> {
    @Query("""
      SELECT v FROM vehiculos v
      WHERE LOWER(v.matricula) LIKE LOWER(CONCAT('%', :filter, '%'))
         OR LOWER(v.cliente.nombre) LIKE LOWER(CONCAT('%', :filter, '%'))
    """)
    Page<Vehiculo> findByFilter(@Param("filter") String filter, Pageable pageable);
    List<Vehiculo> findByMatriculaContainingIgnoreCase(@Param("filter") String filter);
    Optional<Vehiculo> findByMatricula(String matricula);
}
