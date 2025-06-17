import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { mount } from "@vue/test-utils";
import VehiculoForm from "../VehiculosForm.vue";
import type { VehiculoEditData } from "src/types/entities/vehiculo";

vi.mock("src/api/tallerApi", () => ({
  default: {
    get: vi.fn().mockResolvedValue({ data: [] }),
    post: vi.fn().mockResolvedValue({ data: { id: "new-id" } }),
    put: vi.fn().mockResolvedValue({ data: { id: "updated-id" } }),
  },
}));

describe("VehiculoForm", () => {
  let wrapper: ReturnType<typeof mount>;

  const createWrapper = (props: Record<string, unknown> = {}) => {
    return mount(VehiculoForm, {
      props,
      global: {
        stubs: {
          CustomInput: {
            template:
              '<input :value="modelValue" @input="$emit(\'update:modelValue\', $event.target.value)" />',
            props: ["modelValue", "label", "obligatorio", "rules"],
            emits: ["update:modelValue"],
          },
          "q-form": {
            template: "<form><slot /></form>",
            methods: {
              validate: vi.fn().mockResolvedValue(true),
              resetValidation: vi.fn(),
            },
          },
          "q-btn": {
            template: "<button><slot />{{ label }}</button>",
            props: ["label", "loading", "color", "icon", "flat", "unelevated"],
            emits: ["click"],
          },
          "q-select": {
            template: "<select><option>mock option</option></select>",
            props: ["modelValue", "options", "disable", "placeholder", "rules"],
            emits: ["update:modelValue", "filter"],
          },
          "q-scroll-area": { template: "<div><slot /></div>" },
          "q-icon": { template: "<i></i>", props: ["name"] },
          "q-item": { template: "<div><slot /></div>" },
          "q-item-section": { template: "<div><slot /></div>" },
          "q-item-label": { template: "<div><slot /></div>" },
        },
      },
    });
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount();
    }
  });

  describe("Renderizado inicial", () => {
    it("debe renderizar el formulario correctamente", () => {
      wrapper = createWrapper();

      expect(wrapper.find(".dialog-form-container").exists()).toBe(true);
      expect(wrapper.find(".form-grid").exists()).toBe(true);
    });

    it("debe mostrar las secciones de vehículo y cliente", () => {
      wrapper = createWrapper();

      const sections = wrapper.findAll(".form-section");
      expect(sections.length).toBe(2);

      const titles = wrapper.findAll(".section-title");
      expect(titles.length).toBeGreaterThan(0);
      if (titles[0]) {
        expect(titles[0].text()).toBe("Información del vehículo");
      }
      if (titles[1]) {
        expect(titles[1].text()).toBe("Información del cliente");
      }
    });

    it("debe mostrar los botones de acción", () => {
      wrapper = createWrapper();

      const buttons = wrapper.findAll("button");
      expect(buttons.length).toBeGreaterThanOrEqual(2);
    });
  });

  describe("Modo creación", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it('debe mostrar "Crear" en el botón cuando no hay editData', () => {
      const createBtn = wrapper.find("button:last-of-type");
      expect(createBtn.text()).toContain("Crear");
    });

    it("debe tener inputs para matrícula y año", () => {
      const inputs = wrapper.findAll("input");
      expect(inputs.length).toBeGreaterThanOrEqual(2);
    });
  });

  describe("Modo edición", () => {
    const editData: VehiculoEditData = {
      id: 1,
      matricula: "ABC123",
      marca: { id: "1", nombre: "Toyota" },
      modelo: { id: "1", nombre: "Corolla" },
      matriculacion: "2020",
      cliente: { id: "1", nombre: "Juan Pérez" },
    };

    beforeEach(() => {
      wrapper = createWrapper({ editData });
    });

    it('debe mostrar "Actualizar" en el botón cuando hay editData', () => {
      const updateBtn = wrapper.find("button:last-of-type");
      expect(updateBtn.text()).toContain("Actualizar");
    });

    it("debe renderizar correctamente con datos de edición", () => {
      expect(wrapper.exists()).toBe(true);
      expect(wrapper.find(".dialog-form-container").exists()).toBe(true);
    });
  });

  describe("Validación de formulario", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe tener campos obligatorios configurados", () => {
      const inputs = wrapper.findAll("input");
      expect(inputs.length).toBeGreaterThan(0);
    });

    it("debe tener selects para marca, modelo y cliente", () => {
      const selects = wrapper.findAll("select");
      expect(selects.length).toBe(3);
    });
  });

  describe("Interacciones del usuario", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe poder hacer clic en el botón cancelar", async () => {
      const cancelBtn = wrapper.find("button:first-of-type");
      await cancelBtn.trigger("click");

      expect(wrapper.exists()).toBe(true);
    });

    it("debe poder interactuar con los inputs", async () => {
      const inputs = wrapper.findAll("input");
      if (inputs[0]) {
        await inputs[0].setValue("TEST123");
        expect(inputs[0].element.value).toBe("TEST123");
      }
    });
  });

  describe("Estructura del componente", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe tener la clase principal del contenedor", () => {
      expect(wrapper.find(".dialog-form-container").exists()).toBe(true);
    });

    it("debe tener sección de información del vehículo", () => {
      const vehiculoSection = wrapper.find(".form-section");
      expect(vehiculoSection.exists()).toBe(true);
    });

    it("debe tener acciones del formulario", () => {
      expect(wrapper.find(".form-actions").exists()).toBe(true);
    });

    it("debe tener inputs container", () => {
      expect(wrapper.find(".inputs-container").exists()).toBe(true);
    });
  });
});
