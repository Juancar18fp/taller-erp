<template>
  <q-input
    :model-value="modelValue"
    @update:model-value="(value) => $emit('update:modelValue', value)"
    :label="label"
    outlined
    :hint="computedHint"
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

const props = defineProps<{
  modelValue: string | null;
  label: string;
  obligatorio?: boolean;
  rules?: ((val: string | null) => boolean | string)[];
  hint?: string;
  readonly?: boolean;
}>();

const computedRules = computed(() => {
  const rules = [];
  if (props.obligatorio) {
    rules.push((val: string | null) => !!val || "Campo obligatorio");
  }
  return rules;
});

const computedHint = computed(() => {
  if (props.hint === undefined) {
    return "";
  }
  return props.hint;
});

defineEmits(["update:modelValue"]);
</script>
