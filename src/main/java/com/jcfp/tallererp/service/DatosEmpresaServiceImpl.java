package com.jcfp.tallererp.service;

import com.jcfp.tallererp.dto.DatosEmpresaDto;
import com.jcfp.tallererp.entity.DatosEmpresa;
import com.jcfp.tallererp.repository.DatosEmpresaRepository;
import com.jcfp.tallererp.util.DatosEmpresaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DatosEmpresaServiceImpl implements DatosEmpresaService {

    private final DatosEmpresaRepository configuracionRepository;

    @Transactional(readOnly = true)
    public Optional<DatosEmpresaDto> obtenerConfiguracionActiva() {
        log.info("Obteniendo configuración activa de la empresa");

        return configuracionRepository.findConfiguracionActiva()
                .map(this::convertirADTO);
    }

    public DatosEmpresaDto guardarConfiguracion(DatosEmpresaDto dto) {
        log.info("Guardando configuración de empresa: {}", dto.getNombre());

        try {
            DatosEmpresa configuracion = configuracionRepository.findConfiguracionActiva()
                    .orElse(new DatosEmpresa());

            if (configuracion.getId() != null) {
                validarCifUnico(dto.getCif(), configuracion.getId());
            } else {
                validarCifUnico(dto.getCif(), null);
            }

            if (configuracion.getId() == null) {
                desactivarConfiguracionesAnteriores();
            }
            mapearDTOAEntidad(dto, configuracion);
            configuracion.setActivo(true);
            DatosEmpresa configuracionGuardada = configuracionRepository.save(configuracion);
            log.info("Configuración guardada exitosamente con ID: {}", configuracionGuardada.getId());
            return convertirADTO(configuracionGuardada);
        } catch (Exception e) {
            log.error("Error al guardar configuración de empresa", e);
            throw new DatosEmpresaException("Error al guardar la configuración de la empresa: " + e.getMessage(), e);
        }
    }

    public void eliminarConfiguracion() {
        log.info("Eliminando configuración activa de empresa");

        configuracionRepository.findConfiguracionActiva()
                .ifPresentOrElse(
                        config -> {
                            config.setActivo(false);
                            configuracionRepository.save(config);
                            log.info("Configuración desactivada con ID: {}", config.getId());
                        },
                        () -> {
                            throw new DatosEmpresaException("No existe configuración activa para eliminar");
                        }
                );
    }


    @Transactional(readOnly = true)
    public boolean existeConfiguracionActiva() {
        return configuracionRepository.countConfiguracionesActivas() > 0;
    }

    private void validarCifUnico(String cif, Long idActual) {
        Optional<DatosEmpresa> existente;

        if (idActual != null) {
            existente = configuracionRepository.findByCifAndIdNotAndActivoTrue(cif, idActual);
        } else {
            existente = configuracionRepository.findByCifAndActivoTrue(cif);
        }

        if (existente.isPresent()) {
            throw new DatosEmpresaException("Ya existe una empresa con el CIF/NIF: " + cif);
        }
    }

    private void desactivarConfiguracionesAnteriores() {
        configuracionRepository.findConfiguracionActiva()
                .ifPresent(config -> {
                    config.setActivo(false);
                    configuracionRepository.save(config);
                    log.info("Configuración anterior desactivada con ID: {}", config.getId());
                });
    }

    private void mapearDTOAEntidad(DatosEmpresaDto dto, DatosEmpresa entidad) {
        entidad.setNombre(dto.getNombre());
        entidad.setRazonSocial(dto.getRazonSocial());
        entidad.setCif(dto.getCif());
        entidad.setNumeroRegistro(dto.getNumeroRegistro());
        entidad.setDireccion(dto.getDireccion());
        entidad.setCodigoPostal(dto.getCodigoPostal());
        entidad.setPoblacion(dto.getPoblacion());
        entidad.setProvincia(dto.getProvincia());
        entidad.setPais(dto.getPais());
        entidad.setTelefono(dto.getTelefono());
        entidad.setTelefonoSecundario(dto.getTelefonoSecundario());
        entidad.setEmail(dto.getEmail());
        entidad.setEmailSecundario(dto.getEmailSecundario());
        entidad.setSitioWeb(dto.getSitioWeb());
    }

    private DatosEmpresaDto convertirADTO(DatosEmpresa entidad) {
        DatosEmpresaDto dto = new DatosEmpresaDto();
        dto.setId(entidad.getId());
        dto.setNombre(entidad.getNombre());
        dto.setRazonSocial(entidad.getRazonSocial());
        dto.setCif(entidad.getCif());
        dto.setNumeroRegistro(entidad.getNumeroRegistro());
        dto.setDireccion(entidad.getDireccion());
        dto.setCodigoPostal(entidad.getCodigoPostal());
        dto.setPoblacion(entidad.getPoblacion());
        dto.setProvincia(entidad.getProvincia());
        dto.setPais(entidad.getPais());
        dto.setTelefono(entidad.getTelefono());
        dto.setTelefonoSecundario(entidad.getTelefonoSecundario());
        dto.setEmail(entidad.getEmail());
        dto.setEmailSecundario(entidad.getEmailSecundario());
        dto.setSitioWeb(entidad.getSitioWeb());
        return dto;
    }
}
