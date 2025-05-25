import { ref } from "vue";
import { useQuasar } from "quasar";
import type { QNotifyCreateOptions } from "quasar";
import tallerApi from "../api/tallerApi";

interface ClienteInfo {
  id: string;
  nombre: string;
}

export function useClienteSearch() {
  const $q = useQuasar();
  const clienteInfo = ref<ClienteInfo>({
    id: "",
    nombre: "",
  });

  const buscarCliente = async (clienteId: string) => {
    if (!clienteId) {
      $q.notify({
        type: "warning",
        message: "Ingrese un ID de cliente",
        position: "top",
      } as QNotifyCreateOptions);
      return;
    }

    try {
      const response = await tallerApi.get(`/clientes/${clienteId}`);
      clienteInfo.value = {
        id: clienteId,
        nombre: response.data.nombre,
      };
    } catch {
      clienteInfo.value = {
        id: clienteId,
        nombre: "Cliente no encontrado",
      };
    }
  };

  const resetClienteInfo = () => {
    clienteInfo.value = {
      id: "",
      nombre: "",
    };
  };

  return {
    clienteInfo,
    buscarCliente,
    resetClienteInfo,
  };
}
