package com.jcfp.tallererp.service;

import com.jcfp.tallererp.entity.ArticuloUsado;
import com.jcfp.tallererp.entity.OrdenTrabajo;
import com.jcfp.tallererp.repository.ArticuloUsadoRepository;
import com.jcfp.tallererp.repository.OrdenTrabajoRepository;
import com.jcfp.tallererp.util.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenTrabajoServiceImpl extends BaseServiceImpl<OrdenTrabajo,Long, OrdenTrabajoRepository> implements OrdenTrabajoService{
    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final ArticuloUsadoRepository articuloUsadoRepository;
    private final ArticuloService articuloService;
    @Autowired
    public OrdenTrabajoServiceImpl(OrdenTrabajoRepository repository,
                                   ArticuloUsadoRepository articuloUsadoRepository,
                                   ArticuloService articuloService) {
        super(repository);
        this.ordenTrabajoRepository = repository;
        this.articuloUsadoRepository = articuloUsadoRepository;
        this.articuloService = articuloService;
    }

    @Override
    public List<OrdenTrabajo> findByFilterParam(String filter) {
        return List.of();
    }

    public int obtenerSiguienteNumeroOrden(LocalDate fecha) {
        String year = String.valueOf(fecha.getYear());
        String month = String.format("%02d", fecha.getMonthValue());
        String day = String.format("%02d", fecha.getDayOfMonth());

        String patron = "OT-" + year + month + day + "-%";
        List<OrdenTrabajo> ordenesDelDia = ordenTrabajoRepository
                .findByCodigoOrdenLikeOrderByCodigoOrdenDesc(patron);
        if (ordenesDelDia.isEmpty()) {
            return 1;
        }

        String ultimoCodigo = ordenesDelDia.get(0).getCodigoOrden();
        String[] partes = ultimoCodigo.split("-");

        if (partes.length >= 3) {
            try {
                int ultimoNumero = Integer.parseInt(partes[2]);
                return ultimoNumero + 1;
            } catch (NumberFormatException e) {
                return 1;
            }
        }
        return 1;
    }

    public void cancelarOrden(Long ordenId) {
        Optional<OrdenTrabajo> ordenOpt = ordenTrabajoRepository.findById(ordenId);
        if (ordenOpt.isPresent()) {
            OrdenTrabajo orden = ordenOpt.get();

            if ("FINALIZADA".equals(orden.getEstadoOrden().getNombre()) ||
                    "COMPLETADA".equals(orden.getEstadoOrden().getNombre()) ||
                    "PENDIENTE_PAGO".equals(orden.getEstadoOrden().getNombre())) {

                restaurarStockDeOrden(ordenId);
            }
        } else {
            throw new RuntimeException("Orden no encontrada con ID: " + ordenId);
        }
    }

    private void restaurarStockDeOrden(Long ordenId) {
        List<ArticuloUsado> articulosUsados = articuloUsadoRepository.findByOrdenTrabajoId(ordenId);

        for (ArticuloUsado articuloUsado : articulosUsados) {
            if (articuloUsado.getArticulo() != null && articuloUsado.getCantidad() > 0) {
                articuloService.restaurarStock(
                        articuloUsado.getArticulo().getId(),
                        articuloUsado.getCantidad()
                );
            }
        }
    }

}
