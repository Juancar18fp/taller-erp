package com.jcfp.tallererp.service;

import com.jcfp.tallererp.entity.OrdenTrabajo;
import com.jcfp.tallererp.repository.ArticuloUsadoRepository;
import com.jcfp.tallererp.repository.OrdenTrabajoRepository;
import com.jcfp.tallererp.util.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrdenTrabajoServiceImpl extends BaseServiceImpl<OrdenTrabajo,Long, OrdenTrabajoRepository> implements OrdenTrabajoService{
    private OrdenTrabajoRepository ordenTrabajoRepository;
    private ArticuloUsadoRepository articuloUsadoRepository;
    private ArticuloService articuloService;

    @Autowired
    public OrdenTrabajoServiceImpl(OrdenTrabajoRepository repository,
                                   ArticuloUsadoRepository articuloUsadoRepository,
                                   ArticuloService articuloService) {
        super(repository);
        this.ordenTrabajoRepository = repository;
        this.articuloUsadoRepository = articuloUsadoRepository;
        this.articuloService = articuloService;
    }

    public OrdenTrabajoServiceImpl(){

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

}
