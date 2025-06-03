import { ref } from "vue";
import { useQuasar } from "quasar";
import type { QNotifyCreateOptions } from "quasar";
import tallerApi from "../api/tallerApi";
import type {
  EstadoCivil,
  JornadaLaboral,
  Puesto,
  Rol,
  TipoContrato,
} from "src/types/entities/empleado";
export function useEmpleadosForm() {
  const $q = useQuasar();
  const estadosCiviles = ref<EstadoCivil[]>([]);
  const jornadasLaborales = ref<JornadaLaboral[]>([]);
  const tiposContrato = ref<TipoContrato[]>([]);
  const puestos = ref<Puesto[]>([]);
  const roles = ref<Rol[]>([]);

  const estadoCivilSeleccionado = ref<string>("");
  const jornadaLaboralSeleccionada = ref<string>("");
  const tipoContratoSeleccionado = ref<string>("");
  const puestoSeleccionado = ref<string>("");
  const rolSeleccionado = ref<string>("");

  const cargarEstadoCivil = async () => {
    try {
      const { data } = await tallerApi.get<EstadoCivil[]>("/estados-civiles");
      estadosCiviles.value = data.map((m) => ({
        id: m.id.toString(),
        nombre: m.nombre,
      }));
    } catch (error) {
      console.error("Error cargando datos:", error);
      $q.notify({
        type: "negative",
        message: "Error cargando datos de empleados",
        position: "top",
      } as QNotifyCreateOptions);
    }
  };
  const cargarJornadasLaborales = async () => {
    try {
      const { data } = await tallerApi.get<JornadaLaboral[]>("/jornadas-laborales");
      jornadasLaborales.value = data.map((m) => ({
        id: m.id.toString(),
        nombre: m.nombre,
      }));
    } catch (error) {
      console.error("Error cargando jornadas laborales:", error);
      $q.notify({
        type: "negative",
        message: "Error cargando jornadas laborales",
        position: "top",
      } as QNotifyCreateOptions);
    }
  };
  const cargarTiposContrato = async () => {
    try {
      const { data } = await tallerApi.get<TipoContrato[]>("/tipos-contrato");
      tiposContrato.value = data.map((m) => ({
        id: m.id.toString(),
        nombre: m.nombre,
      }));
    } catch (error) {
      console.error("Error cargando tipos de contrato:", error);
      $q.notify({
        type: "negative",
        message: "Error cargando tipos de contrato",
        position: "top",
      } as QNotifyCreateOptions);
    }
  };
  const cargarPuestos = async () => {
    try {
      const { data } = await tallerApi.get<Puesto[]>("/puestos");
      puestos.value = data.map((m) => ({
        id: m.id.toString(),
        nombre: m.nombre,
      }));
    } catch (error) {
      console.error("Error cargando puestos:", error);
      $q.notify({
        type: "negative",
        message: "Error cargando puestos",
        position: "top",
      } as QNotifyCreateOptions);
    }
  };
  const cargarRoles = async () => {
    try {
      const { data } = await tallerApi.get<Rol[]>("/roles");
      roles.value = data.map((m) => ({
        id: m.id.toString(),
        nombre: m.nombre,
      }));
    } catch (error) {
      console.error("Error cargando roles:", error);
      $q.notify({
        type: "negative",
        message: "Error cargando puestos",
        position: "top",
      } as QNotifyCreateOptions);
    }
  };

  return {
    estadoCivilSeleccionado,
    jornadaLaboralSeleccionada,
    tipoContratoSeleccionado,
    puestoSeleccionado,
    rolSeleccionado,
    estadosCiviles,
    jornadasLaborales,
    tiposContrato,
    puestos,
    roles,
    cargarEstadoCivil,
    cargarJornadasLaborales,
    cargarTiposContrato,
    cargarPuestos,
    cargarRoles,
  };
}
