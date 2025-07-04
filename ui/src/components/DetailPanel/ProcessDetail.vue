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
    categorys: {
      type: Array,
      default: () => ([]),
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
  data() {
    return {
      categoryCopy: this.categorys,
    }
  },
  methods: {
    filterCategory(input) {
      if (input) {
        this.categoryCopy = this.categorys.filter(item => {
          if (!!~item.name.indexOf(input) || !!~item.name.toLowerCase().indexOf(input.toLowerCase()))
            return true
        })
      }
      else {
        this.categoryCopy = this.categorys
      }
    },
  },

}
</script>

<template>
  <div :data-clazz="model.clazz">
    <div class="panelTitle">
      {{ i18n.process }}
    </div>
    <div class="panelBody">
      <DefaultDetail
        :model="model"
        :on-change="onChange"
        :read-only="readOnly"
      />
      <div class="panelRow">
        <div>{{ i18n['process.category'] }}：</div>
        <ElSelect
          style="width:90%; font-size:12px"
          :disabled="readOnly"
          :value="model.category"
          allow-create
          :filterable="true"
          :filter-method="filterCategory"
          @change="(e) => onChange('category', e)"
        >
          <ElOption
            v-for="category in categoryCopy"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </ElSelect>
      </div>
      <div class="panelRow">
        <div>{{ i18n['process.id'] }}：</div>
        <ElInput
          style="width:90%; font-size:12px"
          :disabled="readOnly"
          :value="model.id"
          @input="(value) => { onChange('id', value) }"
        />
      </div>
      <div class="panelRow">
        <div>{{ i18n['process.name'] }}：</div>
        <ElInput
          style="width:90%; font-size:12px"
          :disabled="readOnly"
          :value="model.name"
          @input="(value) => { onChange('name', value) }"
        />
      </div>
      <div class="panelRow">
        <div>
          {{ i18n['process.dataObjs'] }}：
          <ElButton
            :disabled="readOnly"
            size="mini"
            @click="() => {}"
          >
            {{ i18n['tooltip.edit'] }}
          </ElButton>
        </div>
      </div>
      <div class="panelRow">
        <div>
          {{ i18n['process.signalDefs'] }}：
          <ElButton
            :disabled="readOnly"
            size="mini"
            @click="() => {}"
          >
            {{ i18n['tooltip.edit'] }}
          </ElButton>
        </div>
      </div>
      <div class="panelRow">
        <div>
          {{ i18n['process.messageDefs'] }}：
          <ElButton
            :disabled="readOnly"
            size="mini"
            @click="() => {}"
          >
            {{ i18n['tooltip.edit'] }}
          </ElButton>
        </div>
      </div>
    </div>
  </div>
</template>
