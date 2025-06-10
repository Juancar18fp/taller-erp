package com.jcfp.tallererp.service;

import com.jcfp.tallererp.entity.OrdenTrabajo;
import com.jcfp.tallererp.repository.ArticuloUsadoRepository;
import com.jcfp.tallererp.repository.OrdenTrabajoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrdenTrabajoServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(OrdenTrabajoServiceImplTest.class);

    @Mock
    private OrdenTrabajoRepository ordenTrabajoRepository;

    @Mock
    private ArticuloUsadoRepository articuloUsadoRepository;

    @Mock
    private ArticuloService articuloService;

    private OrdenTrabajoServiceImpl ordenTrabajoService;

    @BeforeEach
    void setUp() {
        log.info("=== CONFIGURANDO TEST ===");
        log.info("Inicializando mocks y servicio OrdenTrabajoServiceImpl");

        ordenTrabajoService = new OrdenTrabajoServiceImpl(
                ordenTrabajoRepository,
                articuloUsadoRepository,
                articuloService
        );

        log.info("Setup completado - Servicio y mocks listos para testing");
        log.info("=====================================");
    }

    @Test
    void deberiaRealizarOperacionesCrudCompletas() {
        log.info("TEST 1: OPERACIONES CRUD COMPLETAS");
        log.info("Probando: CREATE → READ → UPDATE → DELETE");

        log.info("PASO 1: Crear nueva orden de trabajo");
        OrdenTrabajo nuevaOrden = crearOrdenTrabajoMock();
        OrdenTrabajo ordenCreada = crearOrdenTrabajoMock();
        ordenCreada.setId(1L);

        when(ordenTrabajoRepository.save(nuevaOrden)).thenReturn(ordenCreada);
        log.info("Mock configurado para CREATE operation");

        OrdenTrabajo resultadoCreate = ordenTrabajoService.create(nuevaOrden);

        assertNotNull(resultadoCreate);
        assertEquals(1L, resultadoCreate.getId());
        log.info("CREATE exitoso - Orden creada con ID: {}", resultadoCreate.getId());

        log.info("PASO 2: Buscar orden por ID");
        when(ordenTrabajoRepository.findById(1L)).thenReturn(Optional.of(ordenCreada));
        log.info("Mock configurado para READ operation");

        Optional<OrdenTrabajo> resultadoRead = ordenTrabajoService.findById(1L);

        assertTrue(resultadoRead.isPresent());
        assertEquals(1L, resultadoRead.get().getId());
        log.info("READ exitoso - Orden encontrada con ID: {}", resultadoRead.get().getId());

        log.info("PASO 3: Actualizar orden existente");
        OrdenTrabajo ordenActualizada = crearOrdenTrabajoMock();
        ordenActualizada.setId(1L);
        ordenActualizada.setCodigoOrden("OT-20240315-001-UPDATED");

        when(ordenTrabajoRepository.existsById(1L)).thenReturn(true);
        when(ordenTrabajoRepository.save(ordenActualizada)).thenReturn(ordenActualizada);
        log.info("Mock configurado para UPDATE operation");

        OrdenTrabajo resultadoUpdate = ordenTrabajoService.update(1L, ordenActualizada);

        assertNotNull(resultadoUpdate);
        assertEquals("OT-20240315-001-UPDATED", resultadoUpdate.getCodigoOrden());
        log.info("UPDATE exitoso - Código actualizado: {}", resultadoUpdate.getCodigoOrden());

        log.info("PASO 4: Eliminar orden");
        log.info("Ejecutando DELETE operation");

        ordenTrabajoService.delete(1L);

        verify(ordenTrabajoRepository).deleteById(1L);
        log.info("DELETE exitoso - Orden eliminada");

        verify(ordenTrabajoRepository, times(2)).save(any(OrdenTrabajo.class));
        verify(ordenTrabajoRepository).findById(1L);
        verify(ordenTrabajoRepository).existsById(1L);
        verify(ordenTrabajoRepository).deleteById(1L);

        log.info("Todas las operaciones CRUD verificadas correctamente");
        log.info("TEST 1 COMPLETADO - CRUD funciona perfectamente");
    }

    @Test
    void deberiaGenerarNumeracionOrdenesCorrectamente() {
        log.info("TEST 2: LÓGICA DE NUMERACIÓN DE ÓRDENES");
        log.info("Probando: Primer número → Siguiente número → Formato incorrecto");

        LocalDate fechaPrueba = LocalDate.of(2024, 3, 15);
        String patronEsperado = "OT-20240315-%";
        log.info("Fecha de prueba: {} → Patrón: {}", fechaPrueba, patronEsperado);

        log.info("ESCENARIO 1: Primera orden del día (lista vacía)");
        when(ordenTrabajoRepository.findByCodigoOrdenLikeOrderByCodigoOrdenDesc(patronEsperado))
                .thenReturn(List.of());
        log.info("Mock configurado - No hay órdenes previas");

        int primerNumero = ordenTrabajoService.obtenerSiguienteNumeroOrden(fechaPrueba);

        assertEquals(1, primerNumero);
        log.info("ESCENARIO 1 OK - Primer número: {}", primerNumero);

        log.info("ESCENARIO 2: Ya existen órdenes del día");
        OrdenTrabajo orden1 = new OrdenTrabajo();
        orden1.setCodigoOrden("OT-20240315-005");
        OrdenTrabajo orden2 = new OrdenTrabajo();
        orden2.setCodigoOrden("OT-20240315-003");

        List<OrdenTrabajo> ordenesExistentes = Arrays.asList(orden1, orden2);
        log.info("Órdenes existentes: {}",
                ordenesExistentes.stream().map(OrdenTrabajo::getCodigoOrden).toList());

        when(ordenTrabajoRepository.findByCodigoOrdenLikeOrderByCodigoOrdenDesc(patronEsperado))
                .thenReturn(ordenesExistentes);
        log.info("Mock configurado - {} órdenes existentes", ordenesExistentes.size());

        int siguienteNumero = ordenTrabajoService.obtenerSiguienteNumeroOrden(fechaPrueba);

        assertEquals(6, siguienteNumero); // 5 + 1
        log.info("ESCENARIO 2 OK - Siguiente número: {} (último era 5)", siguienteNumero);

        log.info("ESCENARIO 3: Manejo de formato incorrecto");
        OrdenTrabajo ordenMalFormato = new OrdenTrabajo();
        ordenMalFormato.setCodigoOrden("OT-20240315-ABC");
        log.info("Orden con formato incorrecto: {}", ordenMalFormato.getCodigoOrden());

        when(ordenTrabajoRepository.findByCodigoOrdenLikeOrderByCodigoOrdenDesc(patronEsperado))
                .thenReturn(List.of(ordenMalFormato));
        log.info("Mock configurado - Formato incorrecto");

        int numeroRecuperacion = ordenTrabajoService.obtenerSiguienteNumeroOrden(fechaPrueba);

        assertEquals(1, numeroRecuperacion);
        log.info("ESCENARIO 3 OK - Recuperación de error: {}", numeroRecuperacion);

        verify(ordenTrabajoRepository, times(3))
                .findByCodigoOrdenLikeOrderByCodigoOrdenDesc(patronEsperado);

        log.info("Lógica de numeración verificada correctamente");
        log.info("TEST 2 COMPLETADO - Numeración funciona en todos los escenarios");
    }

    @Test
    void deberiaManejarErroresYCasosEdgeCorrectamente() {
        log.info("EST 3: MANEJO DE ERRORES Y CASOS EDGE");
        log.info("Probando: Entidad inexistente → Paginación → Filtros");

        log.info("ERROR 1: Intentar actualizar orden inexistente");
        Long idInexistente = 999L;
        OrdenTrabajo ordenUpdate = crearOrdenTrabajoMock();

        when(ordenTrabajoRepository.existsById(idInexistente)).thenReturn(false);
        log.info("Mock configurado - ID {} no existe", idInexistente);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> ordenTrabajoService.update(idInexistente, ordenUpdate)
        );

        assertEquals("No existe entidad con id " + idInexistente, exception.getMessage());
        log.info("ERROR 1 OK - Excepción lanzada correctamente: {}", exception.getMessage());

        log.info("FUNCIONALIDAD 2: Búsqueda paginada con filtros");
        String filtro = "cambio aceite";
        Pageable pageable = PageRequest.of(0, 10);

        OrdenTrabajo orden1 = crearOrdenTrabajoMock();
        orden1.setCodigoOrden("OT-20240315-001");
        OrdenTrabajo orden2 = crearOrdenTrabajoMock();
        orden2.setCodigoOrden("OT-20240315-002");

        List<OrdenTrabajo> ordenesFilteradas = Arrays.asList(orden1, orden2);
        Page<OrdenTrabajo> paginaResultado = new PageImpl<>(ordenesFilteradas, pageable, 2);

        log.info("Aplicando filtro: '{}', Página: {}, Tamaño: {}",
                filtro, pageable.getPageNumber(), pageable.getPageSize());

        when(ordenTrabajoRepository.findByFilter(filtro, pageable)).thenReturn(paginaResultado);
        log.info("Mock configurado - {} resultados encontrados", ordenesFilteradas.size());

        Page<OrdenTrabajo> resultadoPaginado = ordenTrabajoService.findAllFiltered(filtro, pageable);

        assertEquals(2, resultadoPaginado.getTotalElements());
        assertEquals(2, resultadoPaginado.getContent().size());
        assertEquals(0, resultadoPaginado.getNumber());
        log.info("FUNCIONALIDAD 2 OK - Paginación: {} elementos, página {}",
                resultadoPaginado.getTotalElements(), resultadoPaginado.getNumber());

        log.info("FUNCIONALIDAD 3: Búsqueda que retorna lista vacía");
        String filtroVacio = "no existe";

        List<OrdenTrabajo> resultadoFiltro = ordenTrabajoService.findByFilterParam(filtroVacio);

        assertNotNull(resultadoFiltro);
        assertTrue(resultadoFiltro.isEmpty());
        log.info("FUNCIONALIDAD 3 OK - Lista vacía retornada correctamente");

        log.info("FUNCIONALIDAD 4: Obtener todas las órdenes");
        List<OrdenTrabajo> todasLasOrdenes = Arrays.asList(
                crearOrdenTrabajoMock(),
                crearOrdenTrabajoMock(),
                crearOrdenTrabajoMock()
        );

        when(ordenTrabajoRepository.findAll()).thenReturn(todasLasOrdenes);
        log.info("Mock configurado - {} órdenes en total", todasLasOrdenes.size());

        List<OrdenTrabajo> resultadoTodas = ordenTrabajoService.findAll();

        assertEquals(3, resultadoTodas.size());
        log.info("FUNCIONALIDAD 4 OK - {} órdenes recuperadas", resultadoTodas.size());

        verify(ordenTrabajoRepository).existsById(idInexistente);
        verify(ordenTrabajoRepository).findByFilter(filtro, pageable);
        verify(ordenTrabajoRepository).findAll();
        verify(ordenTrabajoRepository, never()).save(ordenUpdate);

        log.info("Manejo de errores y casos edge verificados");
        log.info("TEST 3 COMPLETADO - Sistema robusto ante errores");
    }

    private OrdenTrabajo crearOrdenTrabajoMock() {
        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setCodigoOrden("OT-20240315-001");
        log.debug("Orden mock creada: {}", orden.getCodigoOrden());
        return orden;
    }
}