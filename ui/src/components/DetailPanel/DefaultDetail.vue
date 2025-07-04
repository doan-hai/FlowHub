<script>
export default {
  inject: ['i18n'],
  props: {
    model: {
      type: Object,
      default: () => ({}),
    },
    onChange: {
      type: Function,
      default: () => {},
    },
    readOnly: {
      type: Boolean,
      default: false,
    },
  },
}
</script>

<template>
  <div>
    <div class="panelRow">
      <div>{{ i18n.label }}：</div>
      <ElInput
        style="width:90%; font-size:12px"
        :disabled="readOnly"
        :value="model.label"
        @input="(value) => { onChange('label', value) }"
      />
    </div>
    <div class="panelRow">
      <ElCheckbox
        :disabled="readOnly"
        :value="!!model.hideIcon"
        @change="(value) => onChange('hideIcon', value)"
      >
        {{ i18n.hideIcon }}
      </ElCheckbox>
    </div>
  </div>
</template>
