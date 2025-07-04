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
      {{ i18n.receiveTask }}
    </div>
    <div class="panelBody">
      <DefaultDetail
        :model="model"
        :on-change="onChange"
        :read-only="readOnly"
      />
      <div class="panelRow">
        <div>{{ i18n['receiveTask.waitState'] }}：</div>
        <ElInput
          style="width:90%; font-size:12px"
          :disabled="readOnly"
          :value="model.waitState"
          @input="(value) => { onChange('waitState', value) }"
        />
      </div>
      <div class="panelRow">
        <div>{{ i18n['receiveTask.stateValue'] }}：</div>
        <ElInput
          style="width:90%; font-size:12px"
          :disabled="readOnly"
          :value="model.stateValue"
          @input="(value) => { onChange('stateValue', value) }"
        />
      </div>
    </div>
  </div>
</template>
