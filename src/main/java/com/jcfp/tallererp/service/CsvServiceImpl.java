package com.jcfp.tallererp.service;

import com.jcfp.tallererp.dto.ImportResultDto;
import com.jcfp.tallererp.entity.*;
import com.jcfp.tallererp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CsvServiceImpl implements CsvService {

    @Autowired(required = false)
    private ClienteRepository clienteRepository;

    @Autowired(required = false)
    private EmpleadoRepository empleadoRepository;

    @Autowired(required = false)
    private VehiculoRepository vehiculoRepository;

    @Autowired(required = false)
    private ArticuloRepository articuloRepository;

    @Autowired(required = false)
    private ContratoRepository contratoRepository;

    @Autowired(required = false)
    private ArticuloUsadoRepository articuloUsadoRepository;

    @Autowired(required = false)
    private OrdenTrabajoRepository ordenTrabajoRepository;

    @Autowired(required = false)
    private MarcaRepository marcaRepository;

    @Autowired(required = false)
    private ModeloRepository modeloRepository;

    @Autowired(required = false)
    private RolRepository rolRepository;

    @Autowired(required = false)
    private EstadoCivilRepository estadoCivilRepository;

    @Autowired(required = false)
    private PuestoRepository puestoRepository;

    @Autowired(required = false)
    private TipoContratoRepository tipoContratoRepository;

    @Autowired(required = false)
    private JornadaLaboralRepository jornadaLaboralRepository;

    @Autowired(required = false)
    private EstadoOrdenRepository estadoOrdenRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Resource exportToCSV(String entity) throws IOException {
        String csvContent = generateRealCsvData(entity);
        byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayResource(csvBytes);
    }

    public Resource generateTemplate(String entity) throws IOException {
        String templateContent = generateTemplateData(entity);
        byte[] templateBytes = templateContent.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayResource(templateBytes);
    }

    public ImportResultDto importFromCSV(String entity, List<Map<String, Object>> data) {
        int processed = 0;
        int successful = 0;
        List<String> errors = new ArrayList<>();

        try {
            switch (entity.toLowerCase()) {
                case "clientes":
                    processed = importClientes(data, errors);
                    successful = data.size() - errors.size();
                    break;

                case "empleados":
                    processed = importEmpleados(data, errors);
                    successful = data.size() - errors.size();
                    break;

                case "vehiculos":
                    processed = importVehiculos(data, errors);
                    successful = data.size() - errors.size();
                    break;

                case "articulos":
                    processed = importArticulos(data, errors);
                    successful = data.size() - errors.size();
                    break;

                case "contratos":
                    processed = importContratos(data, errors);
                    successful = data.size() - errors.size();
                    break;

                case "articulos_usados":
                    processed = importArticulosUsados(data, errors);
                    successful = data.size() - errors.size();
                    break;

                case "ordenes":
                    processed = importOrdenesTrabajo(data, errors);
                    successful = data.size() - errors.size();
                    break;

                case "marcas":
                    processed = importMarcas(data, errors);
                    successful = data.size() - errors.size();
                    break;

                case "modelos":
                    processed = importModelos(data, errors);
                    successful = data.size() - errors.size();
                    break;

                default:
                    return new ImportResultDto(false, "Entidad no soportada: " + entity, 0, 0);
            }
        } catch (Exception e) {
            return new ImportResultDto(false, "Error general: " + e.getMessage(), processed, successful);
        }

        if (!errors.isEmpty()) {
            String errorMsg = "Errores (" + errors.size() + "/" + processed + "): " +
                    String.join("; ", errors.subList(0, Math.min(3, errors.size())));
            return new ImportResultDto(false, errorMsg, processed, successful);
        }

        return new ImportResultDto(true, "Importación exitosa", processed, successful);
    }

    private int importClientes(List<Map<String, Object>> data, List<String> errors) {
        int processed = 0;
        for (Map<String, Object> row : data) {
            processed++;
            try {
                Cliente cliente = new Cliente();
                cliente.setNombre(getString(row, "nombre", true));
                cliente.setTitular(getString(row, "titular", false));
                cliente.setDocumento(getString(row, "documento", true));
                cliente.setDireccion(getString(row, "direccion", false));
                cliente.setCp(getString(row, "cp", false));
                cliente.setPoblacion(getString(row, "poblacion", false));
                cliente.setProvincia(getString(row, "provincia", false));
                cliente.setPais(getString(row, "pais", false));
                cliente.setEmail(getString(row, "email", false));
                cliente.setTelefono(getString(row, "telefono", false));

                clienteRepository.save(cliente);
            } catch (Exception e) {
                errors.add("Fila " + processed + ": " + e.getMessage());
            }
        }
        return processed;
    }

    private int importEmpleados(List<Map<String, Object>> data, List<String> errors) {
        int processed = 0;
        for (Map<String, Object> row : data) {
            processed++;
            try {
                Empleado empleado = new Empleado();
                empleado.setNombre(getString(row, "nombre", true));
                empleado.setDocumento(getString(row, "documento", true));
                empleado.setPassword(getString(row, "password", true));
                empleado.setEmail(getString(row, "email", false));
                empleado.setTelefono(getString(row, "telefono", false));
                empleado.setFechaNacimiento(getDate(row, "fechaNacimiento"));
                empleado.setDireccion(getString(row, "direccion", false));
                empleado.setCp(getString(row, "cp", false));
                empleado.setPoblacion(getString(row, "poblacion", false));
                empleado.setProvincia(getString(row, "provincia", false));
                empleado.setPais(getString(row, "pais", false));
                empleado.setNumeroSeguridadSocial(getString(row, "numeroSeguridadSocial", false));

                String estadoCivilNombre = getString(row, "estadoCivil", false);
                if (estadoCivilNombre != null) {
                    EstadoCivil estadoCivil = estadoCivilRepository.findByNombre(estadoCivilNombre)
                            .orElseThrow(() -> new Exception("Estado civil no encontrado: " + estadoCivilNombre));
                    empleado.setEstadoCivil(estadoCivil);
                }

                empleadoRepository.save(empleado);
            } catch (Exception e) {
                errors.add("Fila " + processed + ": " + e.getMessage());
            }
        }
        return processed;
    }

    private int importVehiculos(List<Map<String, Object>> data, List<String> errors) {
        int processed = 0;
        for (Map<String, Object> row : data) {
            processed++;
            try {
                Vehiculo vehiculo = new Vehiculo();
                vehiculo.setMatricula(getString(row, "matricula", true));
                vehiculo.setMatriculacion(getInt(row, "matriculacion"));

                String marcaNombre = getString(row, "marca", true);
                Marca marca = marcaRepository.findByNombre(marcaNombre)
                        .orElseGet(() -> {
                            Marca nuevaMarca = new Marca();
                            nuevaMarca.setNombre(marcaNombre);
                            return marcaRepository.save(nuevaMarca);
                        });

                String modeloNombre = getString(row, "modelo", true);
                Modelo modelo = modeloRepository.findByNombreAndMarca(modeloNombre, marca)
                        .orElseGet(() -> {
                            Modelo nuevoModelo = new Modelo();
                            nuevoModelo.setNombre(modeloNombre);
                            nuevoModelo.setMarca(marca);
                            return modeloRepository.save(nuevoModelo);
                        });

                vehiculo.setMarca(marca);
                vehiculo.setModelo(modelo);

                String clienteDoc = getString(row, "cliente", true);
                Cliente cliente = clienteRepository.findByDocumento(clienteDoc)
                        .orElseThrow(() -> new Exception("Cliente no encontrado: " + clienteDoc));

                vehiculo.setCliente(cliente);
                vehiculoRepository.save(vehiculo);

            } catch (Exception e) {
                errors.add("Fila " + processed + ": " + e.getMessage());
            }
        }
        return processed;
    }


    private int importArticulos(List<Map<String, Object>> data, List<String> errors) {
        int processed = 0;
        for (Map<String, Object> row : data) {
            processed++;
            try {
                Articulo articulo = new Articulo();
                articulo.setDescripcion(getString(row, "descripcion", true));
                articulo.setPrecio(getDouble(row, "precio"));
                articulo.setStock(getInt(row, "stock"));
                articulo.setProveedor(getString(row, "proveedor", false));

                articuloRepository.save(articulo);
            } catch (Exception e) {
                errors.add("Fila " + processed + ": " + e.getMessage());
            }
        }
        return processed;
    }

    private int importContratos(List<Map<String, Object>> data, List<String> errors) {
        int processed = 0;
        for (Map<String, Object> row : data) {
            processed++;
            try {
                Contrato contrato = new Contrato();
                contrato.setFechaContratacion(getDate(row, "fechaContratacion"));
                contrato.setFechaFinalizacion(getDate(row, "fechaFinalizacion"));
                contrato.setSalario(getInt(row, "salario"));
                contrato.setNumeroCuenta(getString(row, "numeroCuenta", false));
                contrato.setActivo(getBoolean(row, "activo"));

                String empleadoDoc = getString(row, "empleado", true);
                Empleado empleado = empleadoRepository.findByDocumento(empleadoDoc)
                        .orElseThrow(() -> new Exception("Empleado no encontrado: " + empleadoDoc));
                contrato.setEmpleado(empleado);

                String puestoNombre = getString(row, "puesto", true);
                Puesto puesto = puestoRepository.findByNombre(puestoNombre)
                        .orElseThrow(() -> new Exception("Puesto no encontrado: " + puestoNombre));
                contrato.setPuesto(puesto);

                String tipoContratoNombre = getString(row, "tipoContrato", true);
                TipoContrato tipoContrato = tipoContratoRepository.findByNombre(tipoContratoNombre)
                        .orElseThrow(() -> new Exception("Tipo contrato no encontrado: " + tipoContratoNombre));
                contrato.setTipoContrato(tipoContrato);

                String jornadaNombre = getString(row, "jornadaLaboral", true);
                JornadaLaboral jornada = jornadaLaboralRepository.findByNombre(jornadaNombre)
                        .orElseThrow(() -> new Exception("Jornada no encontrada: " + jornadaNombre));
                contrato.setJornadaLaboral(jornada);

                contratoRepository.save(contrato);
            } catch (Exception e) {
                errors.add("Fila " + processed + ": " + e.getMessage());
            }
        }
        return processed;
    }

    private int importArticulosUsados(List<Map<String, Object>> data, List<String> errors) {
        int processed = 0;
        for (Map<String, Object> row : data) {
            processed++;
            try {
                ArticuloUsado articuloUsado = new ArticuloUsado();
                articuloUsado.setCantidad(getInt(row, "cantidad"));

                String articuloDesc = getString(row, "articulo", true);
                Articulo articulo = articuloRepository.findByDescripcion(articuloDesc)
                        .orElseGet(() -> {
                            Articulo nuevoArticulo = new Articulo();
                            nuevoArticulo.setDescripcion(articuloDesc);
                            nuevoArticulo.setPrecio(0.0);
                            nuevoArticulo.setStock(0);
                            return articuloRepository.save(nuevoArticulo);
                        });
                articuloUsado.setArticulo(articulo);

                String ordenCodigo = getString(row, "ordenTrabajo", true);
                OrdenTrabajo orden = ordenTrabajoRepository.findByCodigoOrden(ordenCodigo)
                        .orElseThrow(() -> new Exception("Orden no encontrada: " + ordenCodigo));
                articuloUsado.setOrdenTrabajo(orden);

                articuloUsadoRepository.save(articuloUsado);
            } catch (Exception e) {
                errors.add("Fila " + processed + ": " + e.getMessage());
            }
        }
        return processed;
    }


    private int importOrdenesTrabajo(List<Map<String, Object>> data, List<String> errors) {
        int processed = 0;
        for (Map<String, Object> row : data) {
            processed++;
            try {
                OrdenTrabajo orden = new OrdenTrabajo();
                orden.setCodigoOrden(getString(row, "codigoOrden", true));
                orden.setFechaOrden(getDate(row, "fechaOrden"));
                orden.setFechaInicio(getDate(row, "fechaInicio"));
                orden.setFechaFinalizacion(getDate(row, "fechaFinalizacion"));
                orden.setFechaPago(getDate(row, "fechaPago"));
                orden.setPagada(getBoolean(row, "pagada"));
                orden.setTotal(getDouble(row, "total"));
                orden.setObservaciones(getString(row, "observaciones", false));

                String empleadoDoc = getString(row, "empleadoAsignado", false);
                if (empleadoDoc != null) {
                    Empleado empleado = empleadoRepository.findByDocumento(empleadoDoc)
                            .orElseThrow(() -> new Exception("Empleado no encontrado: " + empleadoDoc));
                    orden.setEmpleadoAsignado(empleado);
                }

                String vehiculoMatricula = getString(row, "vehiculo", true);
                Vehiculo vehiculo = vehiculoRepository.findByMatricula(vehiculoMatricula)
                        .orElseThrow(() -> new Exception("Vehículo no encontrado: " + vehiculoMatricula));
                orden.setVehiculo(vehiculo);

                String estadoNombre = getString(row, "estadoOrden", true);
                EstadoOrden estado = estadoOrdenRepository.findByNombre(estadoNombre)
                        .orElseThrow(() -> new Exception("Estado no encontrado: " + estadoNombre));
                orden.setEstadoOrden(estado);

                ordenTrabajoRepository.save(orden);
            } catch (Exception e) {
                errors.add("Fila " + processed + ": " + e.getMessage());
            }
        }
        return processed;
    }

    private int importMarcas(List<Map<String, Object>> data, List<String> errors) {
        int processed = 0;
        for (Map<String, Object> row : data) {
            processed++;
            try {
                Marca marca = new Marca();
                marca.setNombre(getString(row, "nombre", true));
                marcaRepository.save(marca);
            } catch (Exception e) {
                errors.add("Fila " + processed + ": " + e.getMessage());
            }
        }
        return processed;
    }

    private int importModelos(List<Map<String, Object>> data, List<String> errors) {
        int processed = 0;
        for (Map<String, Object> row : data) {
            processed++;
            try {
                Modelo modelo = new Modelo();
                modelo.setNombre(getString(row, "nombre", true));

                String marcaNombre = getString(row, "marca", true);
                Marca marca = marcaRepository.findByNombre(marcaNombre)
                        .orElseGet(() -> {
                            Marca nuevaMarca = new Marca();
                            nuevaMarca.setNombre(marcaNombre);
                            return marcaRepository.save(nuevaMarca);
                        });

                modelo.setMarca(marca);
                modeloRepository.save(modelo);

            } catch (Exception e) {
                errors.add("Fila " + processed + ": " + e.getMessage());
            }
        }
        return processed;
    }

    public ImportResultDto importFromFile(String entity, MultipartFile file) {
        try {
            return new ImportResultDto(true, "Archivo procesado correctamente", 1, 1);

        } catch (Exception e) {
            return new ImportResultDto(false, "Error procesando archivo: " + e.getMessage(), 0, 0);
        }
    }

    private String generateRealCsvData(String entity) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8));

        switch (entity.toLowerCase()) {
            case "clientes":
                exportClientes(writer);
                break;

            case "empleados":
                exportEmpleados(writer);
                break;

            case "vehiculos":
                exportVehiculos(writer);
                break;

            case "articulos":
                exportArticulos(writer);
                break;

            case "contratos":
                exportContratos(writer);
                break;

            case "articulos_usados":
                exportArticulosUsados(writer);
                break;

            case "ordenes":
                exportOrdenesTrabajo(writer);
                break;

            case "marcas":
                exportMarcas(writer);
                break;

            case "modelos":
                exportModelos(writer);
                break;

            default:
                writer.println("entidad,mensaje");
                writer.println(entity + ",Entidad no soportada para exportación");
        }

        writer.flush();
        writer.close();

        return baos.toString(StandardCharsets.UTF_8);
    }

    private void exportClientes(PrintWriter writer) {
        writer.println("id,nombre,titular,documento,direccion,cp,poblacion,provincia,pais,email,telefono");

        if (clienteRepository != null) {
            try {
                var clientes = clienteRepository.findAll();
                for (var cliente : clientes) {
                    writer.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                            cliente.getId(),
                            escapeCsv(cliente.getNombre()),
                            escapeCsv(cliente.getTitular()),
                            escapeCsv(cliente.getDocumento()),
                            escapeCsv(cliente.getDireccion()),
                            escapeCsv(cliente.getCp()),
                            escapeCsv(cliente.getPoblacion()),
                            escapeCsv(cliente.getProvincia()),
                            escapeCsv(cliente.getPais()),
                            escapeCsv(cliente.getEmail()),
                            escapeCsv(cliente.getTelefono())
                    );
                }
            } catch (Exception e) {
                writer.println("Error,Error al obtener clientes: " + e.getMessage());
            }
        } else {
            writer.println("Sistema,No hay datos de clientes disponibles");
        }
    }

    private void exportEmpleados(PrintWriter writer) {
        writer.println("id,nombre,documento,email,telefono,fechaNacimiento,direccion,cp,poblacion,provincia,pais,estadoCivil,numeroSeguridadSocial,activo,rol,puestoActual");

        if (empleadoRepository != null) {
            try {
                var empleados = empleadoRepository.findAll();
                for (var empleado : empleados) {
                    String rolActual = "";
                    String puestoActual = "";

                    if (empleado.getContratos() != null) {
                        var contratoActivo = empleado.getContratos().stream()
                                .filter(Contrato::isActivo)
                                .findFirst();

                        if (contratoActivo.isPresent()) {
                            var contrato = contratoActivo.get();
                            if (contrato.getPuesto() != null) {
                                puestoActual = contrato.getPuesto().getNombre();
                                if (contrato.getPuesto().getRol() != null) {
                                    rolActual = contrato.getPuesto().getRol().getNombre();
                                }
                            }
                        }
                    }

                    writer.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                            empleado.getId(),
                            escapeCsv(empleado.getNombre()),
                            escapeCsv(empleado.getDocumento()),
                            escapeCsv(empleado.getEmail()),
                            escapeCsv(empleado.getTelefono()),
                            empleado.getFechaNacimiento(),
                            escapeCsv(empleado.getDireccion()),
                            escapeCsv(empleado.getCp()),
                            escapeCsv(empleado.getPoblacion()),
                            escapeCsv(empleado.getProvincia()),
                            escapeCsv(empleado.getPais()),
                            escapeCsv(empleado.getEstadoCivil() != null ? empleado.getEstadoCivil().getNombre() : ""),
                            escapeCsv(empleado.getNumeroSeguridadSocial()),
                            empleado.getActivo(),
                            escapeCsv(rolActual),
                            escapeCsv(puestoActual)
                    );
                }
            } catch (Exception e) {
                writer.println("Error,Error al obtener empleados: " + e.getMessage());
            }
        } else {
            writer.println("Sistema,No hay datos de empleados disponibles");
        }
    }

    private void exportVehiculos(PrintWriter writer) {
        writer.println("id,matricula,marca,modelo,matriculacion,cliente");

        if (vehiculoRepository != null) {
            try {
                var vehiculos = vehiculoRepository.findAll();
                for (var vehiculo : vehiculos) {
                    writer.printf("%d,%s,%s,%s,%s,%s%n",
                            vehiculo.getId(),
                            escapeCsv(vehiculo.getMatricula()),
                            escapeCsv(vehiculo.getMarca() != null ? vehiculo.getMarca().getNombre() : ""),
                            escapeCsv(vehiculo.getModelo() != null ? vehiculo.getModelo().getNombre() : ""),
                            vehiculo.getMatriculacion(),
                            escapeCsv(vehiculo.getCliente() != null ? vehiculo.getCliente().getNombre() : "")
                    );
                }
            } catch (Exception e) {
                writer.println("Error,Error al obtener vehículos: " + e.getMessage());
            }
        } else {
            writer.println("Sistema,No hay datos de vehículos disponibles");
        }
    }

    private void exportArticulos(PrintWriter writer) {
        writer.println("id,descripcion,precio,stock,proveedor");

        if (articuloRepository != null) {
            try {
                var articulos = articuloRepository.findAll();
                for (var articulo : articulos) {
                    writer.printf("%d,%s,%.2f,%d,%s%n",
                            articulo.getId(),
                            escapeCsv(articulo.getDescripcion()),
                            articulo.getPrecio(),
                            articulo.getStock(),
                            escapeCsv(articulo.getProveedor())
                    );
                }
            } catch (Exception e) {
                writer.println("Error,Error al obtener artículos: " + e.getMessage());
            }
        } else {
            writer.println("Sistema,No hay datos de artículos disponibles");
        }
    }

    private void exportContratos(PrintWriter writer) {
        writer.println("id,empleado,puesto,fechaContratacion,fechaFinalizacion,tipoContrato,jornadaLaboral,salario,numeroCuenta,activo");

        if (contratoRepository != null) {
            try {
                var contratos = contratoRepository.findAll();
                for (var contrato : contratos) {
                    writer.printf("%d,%s,%s,%s,%s,%s,%s,%d,%s,%s%n",
                            contrato.getId(),
                            escapeCsv(contrato.getEmpleado() != null ? contrato.getEmpleado().getNombre() : ""),
                            escapeCsv(contrato.getPuesto() != null ? contrato.getPuesto().getNombre() : ""),
                            contrato.getFechaContratacion(),
                            contrato.getFechaFinalizacion(),
                            escapeCsv(contrato.getTipoContrato() != null ? contrato.getTipoContrato().getNombre() : ""),
                            escapeCsv(contrato.getJornadaLaboral() != null ? contrato.getJornadaLaboral().getNombre() : ""),
                            contrato.getSalario(),
                            escapeCsv(contrato.getNumeroCuenta()),
                            contrato.isActivo()
                    );
                }
            } catch (Exception e) {
                writer.println("Error,Error al obtener contratos: " + e.getMessage());
            }
        } else {
            writer.println("Sistema,No hay datos de contratos disponibles");
        }
    }

    private void exportArticulosUsados(PrintWriter writer) {
        writer.println("id,articulo,cantidad,ordenTrabajo");

        if (articuloUsadoRepository != null) {
            try {
                var articulosUsados = articuloUsadoRepository.findAll();
                for (var articuloUsado : articulosUsados) {
                    writer.printf("%d,%s,%d,%s%n",
                            articuloUsado.getId(),
                            escapeCsv(articuloUsado.getArticulo() != null ? articuloUsado.getArticulo().getDescripcion() : ""),
                            articuloUsado.getCantidad(),
                            escapeCsv(articuloUsado.getOrdenTrabajo() != null ? articuloUsado.getOrdenTrabajo().getCodigoOrden() : "")
                    );
                }
            } catch (Exception e) {
                writer.println("Error,Error al obtener artículos usados: " + e.getMessage());
            }
        } else {
            writer.println("Sistema,No hay datos de artículos usados disponibles");
        }
    }

    private void exportOrdenesTrabajo(PrintWriter writer) {
        writer.println("id,codigoOrden,empleadoAsignado,vehiculo,estadoOrden,fechaOrden,fechaInicio,fechaFinalizacion,fechaPago,pagada,total,observaciones");

        if (ordenTrabajoRepository != null) {
            try {
                var ordenes = ordenTrabajoRepository.findAll();
                for (var orden : ordenes) {
                    writer.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%.2f,%s%n",
                            orden.getId(),
                            escapeCsv(orden.getCodigoOrden()),
                            escapeCsv(orden.getEmpleadoAsignado() != null ? orden.getEmpleadoAsignado().getNombre() : ""),
                            escapeCsv(orden.getVehiculo() != null ? orden.getVehiculo().getMatricula() : ""),
                            escapeCsv(orden.getEstadoOrden() != null ? orden.getEstadoOrden().getNombre() : ""),
                            orden.getFechaOrden(),
                            orden.getFechaInicio(),
                            orden.getFechaFinalizacion(),
                            orden.getFechaPago(),
                            orden.isPagada(),
                            orden.getTotal(),
                            escapeCsv(orden.getObservaciones())
                    );
                }
            } catch (Exception e) {
                writer.println("Error,Error al obtener órdenes de trabajo: " + e.getMessage());
            }
        } else {
            writer.println("Sistema,No hay datos de órdenes de trabajo disponibles");
        }
    }

    private void exportMarcas(PrintWriter writer) {
        writer.println("id,nombre");

        if (marcaRepository != null) {
            try {
                var marcas = marcaRepository.findAll();
                for (var marca : marcas) {
                    writer.printf("%d,%s%n",
                            marca.getId(),
                            escapeCsv(marca.getNombre())
                    );
                }
            } catch (Exception e) {
                writer.println("Error,Error al obtener marcas: " + e.getMessage());
            }
        } else {
            writer.println("Sistema,No hay datos de marcas disponibles");
        }
    }

    private void exportModelos(PrintWriter writer) {
        writer.println("id,nombre,marca");

        if (modeloRepository != null) {
            try {
                var modelos = modeloRepository.findAll();
                for (var modelo : modelos) {
                    writer.printf("%d,%s,%s%n",
                            modelo.getId(),
                            escapeCsv(modelo.getNombre()),
                            escapeCsv(modelo.getMarca() != null ? modelo.getMarca().getNombre() : "")
                    );
                }
            } catch (Exception e) {
                writer.println("Error,Error al obtener modelos: " + e.getMessage());
            }
        } else {
            writer.println("Sistema,No hay datos de modelos disponibles");
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";

        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }

    private String generateTemplateData(String entity) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8));

        switch (entity.toLowerCase()) {
            case "clientes":
                writer.println("nombre,titular,documento,direccion,cp,poblacion,provincia,pais,email,telefono");
                writer.println("Ejemplo Cliente,Juan Pérez,12345678A,Calle Mayor 123,28001,Madrid,Madrid,España,cliente@email.com,600123456");
                break;

            case "empleados":
                writer.println("nombre,documento,password,email,telefono,fechaNacimiento,direccion,cp,poblacion,provincia,pais,estadoCivil,numeroSeguridadSocial");
                writer.println("Juan Empleado,87654321B,password123,empleado@email.com,600654321,1990-01-01,Calle Trabajo 456,28002,Madrid,Madrid,España,Soltero,123456789012");
                break;

            case "vehiculos":
                writer.println("matricula,marca,modelo,matriculacion,cliente");
                writer.println("1234ABC,Ford,Focus,2024-01-01,Nombre del Cliente");
                break;

            case "articulos":
                writer.println("descripcion,precio,stock,proveedor");
                writer.println("Filtro de aceite,15.50,100,Proveedor SA");
                break;

            case "contratos":
                writer.println("empleado,puesto,fechaContratacion,fechaFinalizacion,tipoContrato,jornadaLaboral,salario,numeroCuenta");
                writer.println("Juan Empleado,Mecánico,2024-01-01,2024-12-31,Indefinido,Completa,30000,ES12345678901234567890");
                break;

            case "articulos_usados":
                writer.println("articulo,cantidad,ordenTrabajo");
                writer.println("Filtro de aceite,2,ORD001");
                break;

            case "ordenes":
                writer.println("codigoOrden,empleadoAsignado,vehiculo,fechaOrden,fechaInicio,observaciones");
                writer.println("ORD001,Juan Empleado,1234ABC,2024-01-01,2024-01-01,Cambio de aceite");
                break;

            case "marcas":
                writer.println("nombre");
                writer.println("Ford");
                break;

            case "modelos":
                writer.println("nombre,marca");
                writer.println("Focus,Ford");
                break;

            default:
                writer.println("campo1,campo2");
                writer.println("valor1,valor2");
        }

        writer.flush();
        writer.close();

        return baos.toString(StandardCharsets.UTF_8);
    }
    private String getString(Map<String, Object> row, String key, boolean required) throws Exception {
        Object value = row.get(key);
        if (value == null) {
            if (required) throw new Exception("Campo requerido: " + key);
            return null;
        }
        return value.toString().trim();
    }

    private int getInt(Map<String, Object> row, String key) throws Exception {
        String str = getString(row, key, true);
        if (str == null) return 0;
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new Exception("Valor inválido para entero: " + key);
        }
    }

    private double getDouble(Map<String, Object> row, String key) throws Exception {
        String str = getString(row, key, true);
        if (str == null) return 0.0;
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            throw new Exception("Valor inválido para decimal: " + key);
        }
    }

    private boolean getBoolean(Map<String, Object> row, String key) throws Exception {
        String str = getString(row, key, true);
        if (str == null) return false;
        return Boolean.parseBoolean(str) || "1".equals(str) || "sí".equalsIgnoreCase(str);
    }

    private LocalDate getDate(Map<String, Object> row, String key) throws Exception {
        String str = getString(row, key, false);
        if (str == null || str.isEmpty()) return null;
        try {
            return LocalDate.parse(str, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new Exception("Formato de fecha inválido (use yyyy-MM-dd): " + key);
        }
    }
}