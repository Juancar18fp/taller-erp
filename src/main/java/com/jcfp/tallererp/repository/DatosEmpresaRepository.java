package com.jcfp.tallererp.repository;


import com.jcfp.tallererp.entity.DatosEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DatosEmpresaRepository extends JpaRepository<DatosEmpresa, Long> {

    @Query("SELECT c FROM datos_empresa c WHERE c.activo = true")
    Optional<DatosEmpresa> findConfiguracionActiva();

    @Query("SELECT COUNT(c) FROM datos_empresa c WHERE c.activo = true")
    long countConfiguracionesActivas();

    Optional<DatosEmpresa> findByCifAndActivoTrue(String cif);

    @Query("SELECT c FROM datos_empresa c WHERE c.cif = :cif AND c.id != :id AND c.activo = true")
    Optional<DatosEmpresa> findByCifAndIdNotAndActivoTrue(String cif, Long id);
}
