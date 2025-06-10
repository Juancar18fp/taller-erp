import { config } from "@vue/test-utils";
import { vi } from "vitest";
import { Quasar, Notify } from "quasar";

// Interfaces para los tipos
interface MockOption {
  id: string;
  nombre: string;
}

interface MockSelectProps {
  modelValue?: MockOption | string | null;
  options?: MockOption[];
  optionValue?: string;
  optionLabel?: string;
  useInput?: boolean;
  mapOptions?: boolean;
  emitValue?: boolean;
  inputDebounce?: string | number;
  placeholder?: string;
  outlined?: boolean;
  dense?: boolean;
  clearable?: boolean;
  rules?: Array<(val: string) => boolean | string>;
  disable?: boolean;
}

// Configurar Quasar para las pruebas
config.global.plugins = [
  [
    Quasar,
    {
      plugins: {
        Notify,
      },
    },
  ],
];

// Mock de las utilidades de Quasar
config.global.mocks = {
  $q: {
    notify: vi.fn(),
    loading: {
      show: vi.fn(),
      hide: vi.fn(),
    },
  },
};

// Mock de los componentes de Quasar que usas en tu componente
config.global.components = {
  "q-form": {
    template: "<form @submit.prevent=\"$emit('submit')\"><slot /></form>",
    emits: ["submit"],
    methods: {
      validate: vi.fn().mockResolvedValue(true),
      resetValidation: vi.fn(),
    },
  },
  "q-btn": {
    template: '<button @click="$emit(\'click\')" :disabled="loading"><slot />{{ label }}</button>',
    props: ["label", "loading", "color", "icon", "flat", "unelevated"],
    emits: ["click"],
  },
  "q-select": {
    template: `
      <select
        :value="modelValue"
        @input="$emit('update:modelValue', $event.target.value)"
        @change="$emit('filter', $event.target.value, mockUpdate)"
      >
        <option v-for="option in options" :key="option?.id || option" :value="option">
          {{ option?.nombre || option }}
        </option>
      </select>
    `,
    props: [
      "modelValue",
      "options",
      "optionValue",
      "optionLabel",
      "useInput",
      "mapOptions",
      "emitValue",
      "inputDebounce",
      "placeholder",
      "outlined",
      "dense",
      "clearable",
      "rules",
      "disable",
    ] as Array<keyof MockSelectProps>,
    emits: ["update:modelValue", "filter"],
    setup() {
      const mockUpdate = vi.fn((fn: () => void) => fn());
      return { mockUpdate };
    },
  },
  "q-scroll-area": {
    template: '<div class="scroll-area"><slot /></div>',
    props: ["class"],
  },
  "q-icon": {
    template: '<i :class="name"></i>',
    props: ["name", "class"],
  },
  "q-item": {
    template: '<div class="q-item"><slot /></div>',
  },
  "q-item-section": {
    template: '<div class="q-item-section"><slot /></div>',
    props: ["class"],
  },
  "q-item-label": {
    template: '<div class="q-item-label"><slot /></div>',
    props: ["caption"],
  },
  CustomInput: {
    template: `
      <input
        :value="modelValue"
        @input="$emit('update:modelValue', $event.target.value)"
        :placeholder="label"
        :readonly="readonly"
      />
    `,
    props: ["modelValue", "label", "obligatorio", "rules", "hint", "readonly"],
    emits: ["update:modelValue"],
  },
};

// Mock de APIs globales
global.fetch = vi.fn();

// Mock de axios (ya que lo usas en tallerApi)
vi.mock("src/api/tallerApi", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

// Mock de useQuasar
vi.mock("quasar", async () => {
  const actual = await vi.importActual("quasar");
  return {
    ...actual,
    useQuasar: () => ({
      notify: vi.fn(),
      loading: {
        show: vi.fn(),
        hide: vi.fn(),
      },
    }),
  };
});
