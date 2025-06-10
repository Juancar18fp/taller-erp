import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { mount } from "@vue/test-utils";
import EmpleadosForm from "../EmpleadosForm.vue";
import type { EmpleadoEditData } from "src/types/entities/empleado";

vi.mock("src/api/tallerApi", () => ({
  default: {
    get: vi.fn().mockResolvedValue({ data: [] }),
    post: vi.fn().mockResolvedValue({ data: { id: "new-id" } }),
    put: vi.fn().mockResolvedValue({ data: { id: "updated-id" } }),
    delete: vi.fn().mockResolvedValue({ data: {} }),
  },
}));

vi.mock("src/composables/useEmpleadosForm", () => ({
  useEmpleadosForm: () => ({
    estadoCivilSeleccionado: vi.fn(),
    estadosCiviles: { value: [] },
    jornadasLaborales: { value: [] },
    tiposContrato: { value: [] },
    puestos: { value: [] },
    cargarEstadoCivil: vi.fn(),
    cargarJornadasLaborales: vi.fn(),
    cargarTiposContrato: vi.fn(),
    cargarPuestos: vi.fn(),
  }),
}));

describe("EmpleadosForm", () => {
  let wrapper: ReturnType<typeof mount>;

  const createWrapper = (props: Record<string, unknown> = {}) => {
    return mount(EmpleadosForm, {
      props,
      global: {
        stubs: {
          CustomInput: {
            template:
              '<div class="custom-input">{{ label }}<input :value="modelValue" @input="$emit(\'update:modelValue\', $event.target.value)" /></div>',
            props: ["modelValue", "label", "obligatorio", "rules", "type", "placeholder"],
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
            props: [
              "label",
              "loading",
              "color",
              "icon",
              "flat",
              "unelevated",
              "size",
              "round",
              "outline",
            ],
            emits: ["click"],
          },
          "q-select": {
            template:
              '<div class="q-select">{{ label }}<select><option>mock option</option></select></div>',
            props: [
              "modelValue",
              "options",
              "label",
              "optionLabel",
              "optionValue",
              "outlined",
              "dense",
              "emitValue",
              "mapOptions",
            ],
            emits: ["update:modelValue"],
          },
          "q-input": {
            template:
              '<div class="q-input">{{ label }}<input :value="modelValue" @input="$emit(\'update:modelValue\', $event.target.value)" /></div>',
            props: [
              "modelValue",
              "label",
              "type",
              "outlined",
              "dense",
              "rows",
              "min",
              "max",
              "rules",
              "prefix",
            ],
            emits: ["update:modelValue"],
          },
          "q-scroll-area": { template: "<div><slot /></div>" },
          "q-icon": { template: "<i></i>", props: ["name", "color", "size"] },
          "q-space": { template: "<div></div>" },
          "q-tooltip": { template: "<div><slot /></div>" },
          "q-banner": {
            template: '<div class="banner"><slot name="avatar" /><slot /></div>',
            props: ["class"],
          },
          "q-chip": {
            template: "<span>{{ label }}</span>",
            props: ["color", "textColor", "size", "class"],
          },
          "q-slide-transition": { template: "<div><slot /></div>" },
          "q-toggle": {
            template:
              '<div class="q-toggle">{{ label }}<input type="checkbox" :checked="modelValue" @change="$emit(\'update:modelValue\', $event.target.checked)" /></div>',
            props: ["modelValue", "label", "color"],
            emits: ["update:modelValue"],
          },
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

    it("debe mostrar las secciones principales", () => {
      wrapper = createWrapper();
      const sections = wrapper.findAll(".form-section");
      expect(sections.length).toBeGreaterThanOrEqual(4);

      const titles = wrapper.findAll(".section-title");
      expect(titles.length).toBeGreaterThan(0);
    });

    it("debe mostrar los botones de acción", () => {
      wrapper = createWrapper();
      const buttons = wrapper.findAll("button");
      expect(buttons.length).toBeGreaterThanOrEqual(2);
    });
  });

  describe("Campos del formulario", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe tener campos de entrada para información personal", () => {
      const inputs = wrapper.findAll("input");
      const customInputs = wrapper.findAll(".custom-input");
      const selects = wrapper.findAll("select");
      expect(inputs.length + customInputs.length + selects.length).toBeGreaterThan(0);
    });

    it("debe tener campos de fecha", () => {
      const dateInputs = wrapper.findAll('input[type="date"]');
      const hasDateField = dateInputs.length > 0 || wrapper.html().includes("fecha");
      expect(hasDateField).toBe(true);
    });

    it("debe tener selects para opciones", () => {
      const selects = wrapper.findAll("select");
      const qSelects = wrapper.findAll(".q-select");
      expect(selects.length + qSelects.length).toBeGreaterThan(0);
    });
  });

  describe("Secciones específicas", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe tener sección de seguridad", () => {
      expect(wrapper.find(".security-section").exists()).toBe(true);
      expect(wrapper.html()).toContain("Configuración de Cuenta");
    });

    it("debe tener sección de contratos", () => {
      expect(wrapper.find(".contracts-section").exists()).toBe(true);
      expect(wrapper.html()).toContain("Contratos");
    });

    it("debe mostrar mensaje cuando no hay contratos", () => {
      expect(wrapper.find(".no-contracts").exists()).toBe(true);
      expect(wrapper.html()).toContain("No hay contratos registrados");
    });
  });

  describe("Modo creación vs edición", () => {
    it('debe mostrar "Crear Empleado" cuando no hay editData', () => {
      wrapper = createWrapper();
      const createBtn = wrapper.findAll("button").find((btn) => btn.text().includes("Crear"));
      expect(createBtn).toBeTruthy();
    });

    it('debe mostrar "Actualizar Empleado" cuando hay editData', () => {
      const editData: EmpleadoEditData = {
        id: 1,
        nombre: "Juan Pérez",
        password: "",
        documento: "12345678A",
        fechaNacimiento: "1990-01-01",
        direccion: "Calle Test 123",
        cp: "28001",
        poblacion: "Madrid",
        provincia: "Madrid",
        pais: "España",
        email: "juan@test.com",
        telefono: "600123456",
        numeroSeguridadSocial: "123456789012",
        estadoCivil: { id: "1", nombre: "Soltero" },
        contratos: [],
      };

      wrapper = createWrapper({ editData });
      const updateBtn = wrapper.findAll("button").find((btn) => btn.text().includes("Actualizar"));
      expect(updateBtn).toBeTruthy();
    });
  });

  describe("Validaciones", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe tener elementos de validación", () => {
      const hasValidationText =
        wrapper.html().includes("obligatorio") ||
        wrapper.html().includes("*") ||
        wrapper.html().includes("required");
      const hasValidationElements =
        wrapper.findAll(".custom-input").length > 0 || wrapper.findAll("[required]").length > 0;
      expect(hasValidationText || hasValidationElements).toBe(true);
    });
  });

  describe("Interacciones", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe tener botón para agregar contrato", () => {
      const buttons = wrapper.findAll("button");
      const hasAddButton = buttons.some(
        (btn) => btn.text().includes("Agregar") || btn.html().includes("add"),
      );
      expect(hasAddButton).toBe(true);
    });

    it("debe poder interactuar con inputs", async () => {
      const inputs = wrapper.findAll("input");
      if (inputs[0]) {
        await inputs[0].setValue("Test Value");
        expect(inputs[0].element.value).toBe("Test Value");
      }
    });

    it("debe tener botón cancelar", () => {
      const cancelBtn = wrapper.findAll("button").find((btn) => btn.text().includes("Cancelar"));
      expect(cancelBtn).toBeTruthy();
    });
  });

  describe("Estructura", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe tener estructura de grid", () => {
      expect(wrapper.find(".form-grid").exists()).toBe(true);
    });

    it("debe tener acciones del formulario", () => {
      expect(wrapper.find(".form-actions").exists()).toBe(true);
    });

    it("debe tener contenedores de inputs", () => {
      expect(wrapper.find(".inputs-container").exists()).toBe(true);
    });
  });
});
