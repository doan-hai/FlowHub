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
    messageDefs: {
      type: Array,
      default: () => ([]),
    },
  },
}
</script>

<template>
  <div :data-clazz="model.clazz">
    <div class="panelTitle">
      {{ i18n.messageEvent }}
    </div>
    <div class="panelBody">
      <DefaultDetail
        :model="model"
        :on-change="onChange"
        :read-only="readOnly"
      />
      <div class="panelRow">
        <div>{{ i18n['messageEvent.message'] }}：</div>
        <ElSelect
          style="width:90%; font-size: 12px"
          :placeholder="i18n['messageEvent.message']"
          :value="model.message"
          :disabled="readOnly"
          @change="(e) => { onChange('message', e) }"
        >
          <ElOption
            v-for="message in messageDefs"
            :key="message.id"
            :label="message.name"
            :value="message.id"
          />
        </ElSelect>
      </div>
    </div>
  </div>
</template>
