import { ref, computed } from "vue";
import { useFormHandler } from "./useFormHandler";

interface UseEntityFormOptions<T, U> {
  endpoint: string;
  transformPayload?: (data: T) => U;
  onSuccess?: () => void;
  onError?: (error: Error) => void;
  successMessage?: string;
  errorMessage?: string;
}

export function useEntityForm<T, U = T>(options: UseEntityFormOptions<T, U>) {
  const formRef = ref();
  const isLoading = ref(false);
  const isEditing = computed(() => false);

  const { handleSubmit } = useFormHandler<T, U>({
    endpoint: options.endpoint,
    transformPayload: options.transformPayload || ((data: T) => data as unknown as U),
    successMessage: options.successMessage || "Operación completada con éxito",
    errorMessage: options.errorMessage || "Error en la operación",
  });

  const submitForm = async (formData: T, editId?: number) => {
    try {
      const payload = editId ? { ...formData, id: editId } : formData;
      await handleSubmit(payload, !!editId, editId, options.onSuccess);
    } catch (error) {
      console.error("Error en submitForm:", error);
      options.onError?.(error as Error);
    }
  };

  const resetForm = () => {
    formRef.value?.resetValidation();
  };

  return {
    formRef,
    isLoading,
    isEditing,
    submitForm,
    resetForm,
  };
}
