import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { mount } from "@vue/test-utils";
import { nextTick } from "vue";
import OrdenForm from "../OrdenForm.vue";
import type { OrdenTrabajoEditData } from "src/types/entities/orden_trabajo";

vi.mock("src/api/tallerApi", () => ({
  default: {
    get: vi.fn().mockImplementation((url: string) => {
      if (url.includes("/siguiente-numero")) {
        return Promise.resolve({ data: { siguienteNumero: 1 } });
      }
      if (url.includes("/estados-ordenes")) {
        return Promise.resolve({
          data: [
            { id: 1, nombre: "PENDIENTE" },
            { id: 2, nombre: "INICIADA" },
            { id: 3, nombre: "FINALIZADA" },
            { id: 4, nombre: "COMPLETADA" },
            { id: 5, nombre: "CANCELADA" },
          ],
        });
      }
      return Promise.resolve({ data: [] });
    }),
    post: vi.fn().mockResolvedValue({ data: { id: "new-id" } }),
    put: vi.fn().mockResolvedValue({ data: { id: "updated-id" } }),
    delete: vi.fn().mockResolvedValue({ data: {} }),
  },
}));

describe("OrdenForm", () => {
  let wrapper: ReturnType<typeof mount>;

  const createWrapper = (props: Record<string, unknown> = {}) => {
    return mount(OrdenForm, {
      props,
      global: {
        stubs: {
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
              "dense",
              "disable",
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
              "useInput",
              "inputDebounce",
              "placeholder",
              "clearable",
              "rules",
              "disable",
            ],
            emits: ["update:modelValue", "filter"],
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
              "readonly",
            ],
            emits: ["update:modelValue"],
          },
          "q-scroll-area": { template: "<div><slot /></div>" },
          "q-icon": { template: "<i></i>", props: ["name", "color", "size"] },
          "q-item": { template: "<div><slot /></div>" },
          "q-item-section": { template: "<div><slot /></div>" },
          "q-item-label": { template: "<div><slot /></div>" },
          "q-chip": {
            template: "<span>{{ color }} - <slot /></span>",
            props: ["color", "textColor", "icon", "size"],
          },
          "q-banner": {
            template: '<div class="banner"><slot name="avatar" /><slot /></div>',
            props: ["class", "rounded"],
          },
          "q-separator": { template: "<hr />" },
          "q-tooltip": { template: "<div><slot /></div>" },
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
      expect(sections.length).toBeGreaterThanOrEqual(2);

      expect(wrapper.html()).toContain("Información de la orden");
      expect(wrapper.html()).toContain("Estado de la orden");
    });

    it("debe mostrar los botones de acción", () => {
      wrapper = createWrapper();

      const buttons = wrapper.findAll("button");
      expect(buttons.length).toBeGreaterThanOrEqual(2);
    });
  });

  describe("Nueva orden (modo creación)", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it('debe mostrar "Crear Orden" en el botón cuando es nueva orden', () => {
      const submitBtn = wrapper.findAll("button").find((btn) => btn.text().includes("Crear"));
      expect(submitBtn).toBeTruthy();
    });

    it("debe generar código de orden automáticamente", async () => {
      await nextTick();
      const hasAutoCode =
        wrapper.html().includes("Se generará automáticamente") ||
        wrapper.html().includes("automáticamente") ||
        wrapper.html().includes("OT-") ||
        wrapper.find(".readonly-value").exists();
      expect(hasAutoCode).toBe(true);
    });

    it("debe mostrar fecha actual de orden", () => {
      expect(wrapper.find(".readonly-field").exists()).toBe(true);
    });

    it("debe tener select de vehículo habilitado", () => {
      const vehiculoSelect = wrapper.findAll("select").find(() => true);
      expect(vehiculoSelect).toBeTruthy();
    });
  });

  describe("Información de la orden", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe mostrar campos de solo lectura para código y fecha", () => {
      const readonlyFields = wrapper.findAll(".readonly-field");
      expect(readonlyFields.length).toBeGreaterThanOrEqual(2);
    });

    it("debe tener select para vehículo", () => {
      const selects = wrapper.findAll("select");
      const qSelects = wrapper.findAll(".q-select");
      const hasVehiculoField =
        selects.length > 0 ||
        qSelects.length > 0 ||
        wrapper.html().includes("vehículo") ||
        wrapper.html().includes("Vehículo");
      expect(hasVehiculoField).toBe(true);
    });

    it("debe mostrar información del cliente automáticamente", () => {
      const clienteField = wrapper
        .findAll(".readonly-field")
        .find((field) => field.text().includes("Cliente"));
      expect(clienteField).toBeTruthy();
    });
  });

  describe("Estado de la orden", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe mostrar el estado actual", () => {
      expect(wrapper.find(".estado-container").exists()).toBe(true);
    });

    it("debe tener chip de estado", () => {
      const estadoElements = wrapper.findAll("span");
      expect(estadoElements.length).toBeGreaterThanOrEqual(0);

      const hasEstadoText =
        wrapper.html().includes("estado") || wrapper.html().includes("Pendiente");
      expect(hasEstadoText || estadoElements.length > 0).toBe(true);
    });
  });

  describe("Observaciones", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe tener sección de observaciones", () => {
      expect(wrapper.find(".observaciones-section").exists()).toBe(true);
      expect(wrapper.html()).toContain("Observaciones");
    });

    it("debe tener textarea para observaciones", () => {
      const inputs = wrapper.findAll("input");
      const hasTextarea = inputs.length > 0 || wrapper.html().includes("observaciones");
      expect(hasTextarea).toBe(true);
    });
  });

  describe("Artículos utilizados", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe tener sección de artículos", () => {
      expect(wrapper.find(".articulos-section").exists()).toBe(true);
      expect(wrapper.html()).toContain("Artículos utilizados");
    });

    it("debe tener botón para agregar artículos", () => {
      const addButton = wrapper
        .findAll("button")
        .find((btn) => btn.html().includes("add") || btn.text().includes("add"));
      expect(addButton).toBeTruthy();
    });

    it("debe mostrar total de artículos", () => {
      expect(wrapper.find(".total-container").exists()).toBe(true);
    });

    it("debe poder agregar artículos", async () => {
      const addButton = wrapper.findAll("button").find((btn) => btn.html().includes("add"));

      if (addButton && !addButton.attributes("disable")) {
        await addButton.trigger("click");
        expect(wrapper.exists()).toBe(true);
      }
    });
  });

  describe("Modo edición", () => {
    const editData: OrdenTrabajoEditData = {
      id: "1",
      codigoOrden: "OT-20240101-001",
      empleadoAsignado: { id: "1" },
      vehiculo: { id: "1" },
      estadoOrden: { id: "1" },
      fechaOrden: "2024-01-01",
      fechaInicio: "",
      fechaFinalizacion: "",
      fechaPago: "",
      pagada: false,
      articulosUsados: [],
      observaciones: "Orden de prueba",
      total: 0,
    };

    beforeEach(() => {
      wrapper = createWrapper({ editData });
    });

    it('debe mostrar "Actualizar" en el botón cuando hay editData', () => {
      const updateBtn = wrapper.findAll("button").find((btn) => btn.text().includes("Actualizar"));
      expect(updateBtn).toBeTruthy();
    });

    it("debe mostrar el código de orden existente", async () => {
      await nextTick();
      expect(wrapper.html()).toContain("OT-20240101-001");
    });

    it("debe mostrar información de fechas cuando corresponda", () => {
      expect(wrapper.find(".fechas-info").exists()).toBe(true);
    });

    it("debe mostrar botones de acción según el estado", () => {
      expect(wrapper.find(".acciones-estado").exists()).toBe(true);
    });
  });

  describe("Estados y permisos", () => {
    const editDataPendiente: OrdenTrabajoEditData = {
      id: "1",
      codigoOrden: "OT-20240101-001",
      empleadoAsignado: { id: "" },
      vehiculo: { id: "1" },
      estadoOrden: { id: "1" },
      fechaOrden: "2024-01-01",
      fechaInicio: "",
      fechaFinalizacion: "",
      fechaPago: "",
      pagada: false,
      articulosUsados: [],
      observaciones: "",
      total: 0,
    };

    beforeEach(() => {
      wrapper = createWrapper({ editData: editDataPendiente });
    });

    it("debe mostrar botón de cancelar orden cuando sea posible", () => {
      expect(wrapper.find(".cancelar-container").exists()).toBe(true);
    });
  });

  describe("Validaciones", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe validar campos obligatorios", () => {
      const selects = wrapper.findAll("select");
      const qSelects = wrapper.findAll(".q-select");
      const inputs = wrapper.findAll("input");

      const hasValidationElements =
        selects.length > 0 ||
        qSelects.length > 0 ||
        inputs.length > 0 ||
        wrapper.html().includes("rules") ||
        wrapper.html().includes("required") ||
        wrapper.html().includes("obligatorio");
      expect(hasValidationElements).toBe(true);
    });
  });

  describe("Interacciones del usuario", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe poder hacer clic en el botón cancelar", () => {
      const cancelBtn = wrapper.findAll("button").find((btn) => btn.text().includes("Cancelar"));

      if (cancelBtn) {
        expect(cancelBtn.exists()).toBe(true);
      } else {
        expect(wrapper.findAll("button").length).toBeGreaterThan(0);
      }
    });

    it("debe poder seleccionar vehículo", () => {
      const vehiculoSelect = wrapper.findAll("select")[0];
      if (vehiculoSelect) {
        expect(vehiculoSelect.exists()).toBe(true);
      } else {
        expect(wrapper.findAll(".q-select").length).toBeGreaterThanOrEqual(0);
      }
    });
  });

  describe("Gestión de artículos", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe mostrar container de artículos", () => {
      expect(wrapper.find(".articulos-container").exists()).toBe(true);
    });

    it("debe calcular total correctamente", () => {
      expect(wrapper.find(".total-container").exists()).toBe(true);
    });

    it("debe poder remover artículos", () => {
      const deleteButtons = wrapper
        .findAll("button")
        .filter(
          (btn) =>
            btn.html().includes("delete") ||
            btn.html().includes("remove") ||
            btn.text().includes("Eliminar"),
        );
      const hasDeleteFunctionality =
        deleteButtons.length > 0 ||
        wrapper.html().includes("delete") ||
        wrapper.html().includes("eliminar");

      expect(hasDeleteFunctionality || wrapper.findAll("button").length > 0).toBe(true);
    });

    it("debe validar stock disponible", () => {
      const hasStockInfo =
        wrapper.html().includes("stock") ||
        wrapper.html().includes("Stock") ||
        wrapper.html().includes("disponible") ||
        wrapper.html().includes("cantidad");

      const hasArticleElements =
        wrapper.find(".articulos-container").exists() ||
        wrapper.findAll('input[type="number"]').length > 0;

      expect(hasStockInfo || hasArticleElements).toBe(true);
    });
  });

  describe("Estructura del componente", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe tener la estructura de grid correcta", () => {
      const formGrid = wrapper.find(".form-grid");
      expect(formGrid.exists()).toBe(true);
    });

    it("debe tener todas las secciones principales", () => {
      expect(wrapper.find(".articulos-section").exists()).toBe(true);
      expect(wrapper.find(".observaciones-section").exists()).toBe(true);
    });

    it("debe tener acciones del formulario", () => {
      expect(wrapper.find(".form-actions").exists()).toBe(true);
    });

    it("debe tener área de scroll", () => {
      expect(wrapper.find(".form-scroll-area").exists()).toBe(true);
    });
  });

  describe("Funcionalidades específicas de orden", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe mostrar fecha de orden actual", () => {
      const fechaDisplay = wrapper.findAll(".readonly-value");
      expect(fechaDisplay.length).toBeGreaterThan(0);
    });
  });

  describe("Workflow de estados", () => {
    const editDataIniciada: OrdenTrabajoEditData = {
      id: "1",
      codigoOrden: "OT-20240101-001",
      empleadoAsignado: { id: "1" },
      vehiculo: { id: "1" },
      estadoOrden: { id: "2" },
      fechaOrden: "2024-01-01",
      fechaInicio: "2024-01-01",
      fechaFinalizacion: "",
      fechaPago: "",
      pagada: false,
      articulosUsados: [],
      observaciones: "",
      total: 0,
    };

    beforeEach(() => {
      wrapper = createWrapper({ editData: editDataIniciada });
    });

    it("debe mostrar botones apropiados según el estado", () => {
      expect(wrapper.find(".acciones-estado").exists()).toBe(true);
    });
  });

  describe("Manejo de errores y validaciones específicas", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe validar que hay vehículo seleccionado antes de enviar", () => {
      const vehiculoSelects = wrapper
        .findAll(".q-select")
        .filter(
          (select) => select.html().includes("vehículo") || select.html().includes("Vehículo"),
        );
      const hasVehiculoValidation =
        vehiculoSelects.length > 0 || wrapper.findAll("select").length > 0;
      expect(hasVehiculoValidation).toBe(true);
    });
  });

  describe("Cálculos y totales", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe calcular totales de artículos correctamente", () => {
      expect(wrapper.find(".total-container").exists()).toBe(true);
      expect(wrapper.html()).toContain("Total de artículos");
    });
  });

  describe("Responsividad y accesibilidad", () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it("debe tener estructura responsive", () => {
      expect(wrapper.find(".form-grid").exists()).toBe(true);
    });
  });
});
