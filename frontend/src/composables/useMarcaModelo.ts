import { ref } from "vue";
import { useQuasar } from "quasar";
import type { QNotifyCreateOptions } from "quasar";
import tallerApi from "../api/tallerApi";

interface Marca {
  id: string;
  nombre: string;
}

interface Modelo {
  id: string;
  nombre: string;
  marca_id: string;
}

export function useMarcaModelo() {
  const $q = useQuasar();
  const marcas = ref<Marca[]>([]);
  const modelos = ref<Modelo[]>([]);
  const marcaSeleccionada = ref<string>("");
  const modeloSeleccionado = ref<string>("");

  const cargarMarcas = async () => {
    try {
      const { data } = await tallerApi.get<Marca[]>("/marcas");
      marcas.value = data.map((m) => ({
        id: m.id.toString(),
        nombre: m.nombre,
      }));
    } catch (error) {
      console.error("Error cargando marcas:", error);
      $q.notify({
        type: "negative",
        message: "Error cargando marcas",
        position: "top",
      } as QNotifyCreateOptions);
    }
  };

  const cargarModelosPorMarca = async (marcaId: string) => {
    try {
      const { data } = await tallerApi.get<Modelo[]>(`/modelos/${marcaId}`);
      modelos.value = data.map((m) => ({
        id: m.id.toString(),
        nombre: m.nombre,
        marca_id: marcaId,
      }));
    } catch (error) {
      console.error("Error cargando modelos:", error);
      $q.notify({
        type: "negative",
        message: "Error cargando modelos de la marca",
        position: "top",
      } as QNotifyCreateOptions);
    }
  };

  const cambiarMarca = async (nuevaMarcaId: string) => {
    marcaSeleccionada.value = nuevaMarcaId;
    modeloSeleccionado.value = "";
    modelos.value = [];

    if (nuevaMarcaId) {
      await cargarModelosPorMarca(nuevaMarcaId);
    }
  };

  const seleccionarModelo = (modeloId: string) => {
    modeloSeleccionado.value = modeloId;
  };

  const resetMarcaModelo = () => {
    marcaSeleccionada.value = "";
    modeloSeleccionado.value = "";
    modelos.value = [];
  };

  return {
    marcas,
    modelos,
    marcaSeleccionada,
    modeloSeleccionado,
    cargarMarcas,
    cargarModelosPorMarca,
    cambiarMarca,
    seleccionarModelo,
    resetMarcaModelo,
  };
}
