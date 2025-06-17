package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.entity.Empleado;
import com.jcfp.tallererp.util.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends BaseRepository<Empleado, Long> {
    @Query("""
      SELECT e FROM empleados e
      WHERE LOWER(e.nombre)     LIKE LOWER(CONCAT('%', :filter, '%'))
         OR LOWER(e.documento)  LIKE LOWER(CONCAT('%', :filter, '%'))
         OR LOWER(e.direccion)  LIKE LOWER(CONCAT('%', :filter, '%'))
         OR LOWER(e.email)      LIKE LOWER(CONCAT('%', :filter, '%'))
         OR LOWER(e.telefono)   LIKE LOWER(CONCAT('%', :filter, '%'))
    """)
    Page<Empleado> findByFilter(@Param("filter") String filter, Pageable pageable);
    List<Empleado> findByNombreContainingIgnoreCase(String nombre);
    @Query("SELECT e FROM empleados e WHERE e.documento = :documento")
    Optional<Empleado> findByDocumentoForAuthentication(@Param("documento") String documento);
    boolean existsByDocumento(String documento);
    Optional<Empleado> findByNombre(String nombre);
    Optional<Empleado> findByDocumento(String documento);
    int countBy();

}
