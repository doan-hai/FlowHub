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
    users: {
      type: Array,
      default: () => ([]),
    },
    groups: {
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
      usersCopy: this.users,
      groupsCopy: this.groups,
    }
  },
  methods: {
    filterUsers(input) {
      if (input) {
        this.usersCopy = this.users.filter(item => {
          if (!!~item.name.indexOf(input) || !!~item.name.toLowerCase().indexOf(input.toLowerCase()))
            return true
        })
      }
      else {
        this.usersCopy = this.users
      }
    },
    filterGroups(input) {
      if (input) {
        this.groupsCopy = this.groups.filter(item => {
          if (!!~item.name.indexOf(input) || !!~item.name.toLowerCase().indexOf(input.toLowerCase()))
            return true
        })
      }
      else {
        this.groupsCopy = this.groups
      }
    },
  },
}
</script>

<template>
  <div :data-clazz="model.clazz">
    <div class="panelTitle">
      {{ i18n.userTask }}
    </div>
    <div class="panelBody">
      <DefaultDetail
        :model="model"
        :on-change="onChange"
        :read-only="readOnly"
      />
      <div class="panelRow">
        <div>{{ i18n['userTask.assignType'] }}：</div>
        <ElSelect
          style="width:90%; font-size: 12px"
          :placeholder="i18n['userTask.assignType.placeholder']"
          :value="model.assignType"
          :disabled="readOnly"
          @change="(e) => { onChange('assignValue', []);onChange('assignType', e) }"
        >
          <ElOption
            key="assignee"
            value="assignee"
            :label="i18n['userTask.assignType.assignee']"
          />
          <ElOption
            key="person"
            value="person"
            :label="i18n['userTask.assignType.person']"
          />
          <ElOption
            key="persongroup"
            value="persongroup"
            :label="i18n['userTask.assignType.persongroup']"
          />
        </ElSelect>
      </div>
      <div
        v-if="model.assignType === 'assignee'"
        class="panelRow"
      >
        <div>{{ i18n['userTask.assignType.assignee.title'] }}：</div>
        <ElSelect
          style="width:90%; font-size:12px"
          :placeholder="i18n['userTask.assignType.assignee.placeholder']"
          :disabled="readOnly"
          :value="model.assignValue"
          :multiple="true"
          :multiple-limit="1"
          allow-create
          :filterable="true"
          :filter-method="filterUsers"
          @change="(e) => onChange('assignValue', e)"
        >
          <ElOption
            v-for="user in usersCopy"
            :key="user.id"
            :label="user.name"
            :value="user.id"
          />
        </ElSelect>
      </div>
      <div
        v-if="model.assignType === 'person'"
        class="panelRow"
      >
        <div>{{ i18n['userTask.assignType.person.title'] }}：</div>
        <ElSelect
          style="width:90%; font-size:12px"
          :placeholder="i18n['userTask.assignType.person.placeholder']"
          :disabled="readOnly"
          :value="model.assignValue"
          :multiple="true"
          allow-create
          :filterable="true"
          :filter-method="filterUsers"
          @change="(e) => onChange('assignValue', e)"
        >
          <ElOption
            v-for="user in usersCopy"
            :key="user.id"
            :label="user.name"
            :value="user.id"
          />
        </ElSelect>
      </div>
      <div
        v-else-if="model.assignType === 'persongroup'"
        class="panelRow"
      >
        <div>{{ i18n['userTask.assignType.persongroup.title'] }}：</div>
        <ElSelect
          style="width:90%; font-size:12px"
          :placeholder="i18n['userTask.assignType.persongroup.placeholder']"
          :value="model.assignValue"
          :disabled="readOnly"
          :multiple="true"
          allow-create
          :filterable="true"
          :filter-method="filterGroups"
          @change="(e) => onChange('assignValue', e)"
        >
          <ElOption
            v-for="group in groupsCopy"
            :key="group.id"
            :label="group.name"
            :value="group.id"
          />
        </ElSelect>
      </div>
      <div class="panelRow">
        <div style="display:inline">
          {{ i18n['userTask.dueDate'] }}：
        </div>
        <ElDatePicker
          type="datetime"
          style="width:90%; min-width:null"
          :placeholder="i18n['userTask.dueDate.placeholder']"
          :disabled="readOnly"
          :value="model.dueDate"
          value-format="yyyy-MM-dd HH:mm:ss"
          @input="(value) => onChange('dueDate', value)"
        />
      </div>
      <div class="panelRow">
        <ElCheckbox
          :disabled="readOnly"
          :value="!!model.isSequential"
          @change="(value) => onChange('isSequential', value)"
        >
          {{ i18n['userTask.counterSign'] }}
        </ElCheckbox>
      </div>
    </div>
  </div>
</template>
