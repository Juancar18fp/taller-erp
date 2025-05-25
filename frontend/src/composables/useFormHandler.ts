import { ref } from "vue";
import { useQuasar } from "quasar";
import type { QForm, QNotifyCreateOptions } from "quasar";
import tallerApi from "../api/tallerApi";
import type { AxiosError } from "axios";

interface FormHandlerOptions<T, P> {
  endpoint: string;
  successMessage: string;
  errorMessage: string;
  transformPayload?: (data: T) => P;
}

export function useFormHandler<T, P = T>(options: FormHandlerOptions<T, P>) {
  const $q = useQuasar();
  const formRef = ref<QForm>();
  const isLoading = ref(false);

  const showNotification = (
    type: "positive" | "negative" | "warning",
    message: string,
    caption?: string,
  ) => {
    const notifyOptions: QNotifyCreateOptions = {
      type,
      message,
      position: "top",
    };

    if (caption) {
      notifyOptions.caption = caption;
    }

    $q.notify(notifyOptions);
  };

  const handleSubmit = async (data: T, isEdit: boolean, id?: number, onSuccess?: () => void) => {
    try {
      isLoading.value = true;
      console.log("Enviando datos:", {
        isEdit,
        id,
        data,
        url: `${options.endpoint}${isEdit ? `/${id}` : ""}`,
        method: isEdit ? "PUT" : "POST",
      });

      const transformedData = options.transformPayload
        ? options.transformPayload(data)
        : (data as unknown as P);
      const response = isEdit
        ? await tallerApi.put(`${options.endpoint}/${id}`, transformedData)
        : await tallerApi.post(options.endpoint, transformedData);

      console.log("Respuesta del servidor:", response.data);
      onSuccess?.();
      return response.data;
    } catch (error: unknown) {
      const axiosError = error as AxiosError;
      console.error("Error en handleSubmit:", {
        error: axiosError,
        response: axiosError.response?.data,
        status: axiosError.response?.status,
        config: axiosError.config,
      });
      throw error;
    } finally {
      isLoading.value = false;
    }
  };

  return {
    formRef,
    isLoading,
    handleSubmit,
    showNotification,
  };
}
