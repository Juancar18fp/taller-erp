<template>
  <q-input
    :model-value="modelValue"
    @update:model-value="(value) => $emit('update:modelValue', value)"
    :label="label"
    outlined
    :hint="hint"
    dense
    lazy-rules
    :rules="computedRules"
    class="q-mb-md"
    v-bind="$attrs"
  >
    <slot name="append"></slot>
  </q-input>
</template>

<script setup lang="ts">
import { computed } from "vue";

interface CustomInputProps {
  modelValue: string;
  obligatorio?: boolean;
  label: string;
  hint?: string;
  rules?: Array<(val: string) => boolean | string>;
}

const props = withDefaults(defineProps<CustomInputProps>(), {
  hint: "",
  obligatorio: false,
});

const computedRules = computed(() => {
  const rules = [];
  if (props.obligatorio) {
    rules.push((val: string) => !!val || "Campo obligatorio");
  }
  return rules;
});

defineEmits(["update:modelValue"]);
</script>
