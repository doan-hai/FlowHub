<script>
import DefaultDetail from './DefaultDetail'

export default {
  components: {
    DefaultDetail,
  },
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
  <div :data-clazz="model.clazz">
    <div class="panelTitle">
      {{ i18n.scriptTask }}
    </div>
    <div class="panelBody">
      <DefaultDetail
        :model="model"
        :on-change="onChange"
        :read-only="readOnly"
      />
      <div class="panelRow">
        <div>{{ i18n['scriptTask.script'] }}：</div>
        <ElInput
          style="width:90%; font-size:12px"
          type="textarea"
          :rows="4"
          :disabled="readOnly"
          :value="model.script"
          @input="(value) => { onChange('script', value) }"
        />
      </div>
    </div>
  </div>
</template>
