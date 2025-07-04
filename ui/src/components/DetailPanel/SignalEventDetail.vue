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
    signalDefs: {
      type: Array,
      default: () => ([]),
    },
  },
}
</script>

<template>
  <div :data-clazz="model.clazz">
    <div class="panelTitle">
      {{ i18n.signalEvent }}
    </div>
    <div class="panelBody">
      <DefaultDetail
        :model="model"
        :on-change="onChange"
        :read-only="readOnly"
      />
      <div class="panelRow">
        <div>{{ i18n['signalEvent.signal'] }}：</div>
        <ElSelect
          style="width:90%; font-size: 12px"
          :placeholder="i18n['signalEvent.signal']"
          :value="model.signal"
          :disabled="readOnly"
          @change="(e) => { onChange('signal', e) }"
        >
          <ElOption
            v-for="signal in signalDefs"
            :key="signal.id"
            :label="signal.name"
            :value="signal.id"
          />
        </ElSelect>
      </div>
    </div>
  </div>
</template>
