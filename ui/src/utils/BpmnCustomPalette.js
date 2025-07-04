export default function BpmnCustomPalette(create, elementFactory, palette, globalConnect) {
  this._create = create
  this._elementFactory = elementFactory
  this._globalConnect = globalConnect

  palette.registerProvider(this)
}

BpmnCustomPalette.$inject = ['create', 'elementFactory', 'palette', 'globalConnect']

BpmnCustomPalette.prototype.getPaletteEntries = function () {

  const {
    _create: create,
    _elementFactory: elementFactory,
    _globalConnect: globalConnect,
  } = this

  function createTask(type) {
    return function (event) {
      const shape = elementFactory.createShape({ type })

      create.start(event, shape)
    }
  }

  return {
    'create.start-event': {
      group: 'event',
      className: 'bpmn-icon-start-event-none',
      title: 'Start Transition',
      action: { dragstart: createTask('bpmn:StartEvent'), click: createTask('bpmn:StartEvent') },
    },
    'create.end-event': {
      group: 'event',
      className: 'bpmn-icon-end-event-none',
      title: 'End Transition',
      action: { dragstart: createTask('bpmn:EndEvent'), click: createTask('bpmn:EndEvent') },
    },
    'create.wait-event': {
      group: 'event',
      className: 'bpmn-icon-intermediate-event-catch-timer',
      title: 'Wait Task',
      action: { dragstart: createTask('bpmn:IntermediateCatchEvent'), click: createTask('bpmn:IntermediateCatchEvent') },
    },
    'create.switch-task': {
      group: 'activity',
      className: 'bpmn-icon-user-task',
      title: 'Switch Task',
      action: { dragstart: createTask('bpmn:UserTask'), click: createTask('bpmn:UserTask') },
    },
    'create.join-task': {
      group: 'activity',
      className: 'bpmn-icon-manual-task',
      title: 'Join Task',
      action: { dragstart: createTask('bpmn:ManualTask'), click: createTask('bpmn:ManualTask') },
    },
    'create.broadcast-task': {
      group: 'activity',
      className: 'bpmn-icon-script-task',
      title: 'Broadcast Task',
      action: { dragstart: createTask('bpmn:ScriptTask'), click: createTask('bpmn:ScriptTask') },
    },
    'create.join-broadcast-task': {
      group: 'activity',
      className: 'bpmn-icon-service-task',
      title: 'Join & Broadcast Task',
      action: { dragstart: createTask('bpmn:ServiceTask'), click: createTask('bpmn:ServiceTask') },
    },
    'create.condition-flow-gateway': {
      group: 'gateway',
      className: 'bpmn-icon-gateway-xor',
      title: 'Condition Flow',
      action: {
        dragstart: createTask('bpmn:ExclusiveGateway'),
        click: createTask('bpmn:ExclusiveGateway'),
      },
    },
    'create.rollback-gateway': {
      group: 'gateway',
      className: 'bpmn-icon-gateway-parallel',
      title: 'Rollback Flow',
      action: {
        dragstart: createTask('bpmn:ParallelGateway'),
        click: createTask('bpmn:ParallelGateway'),
      },
    },
    'tool-separator': {
      group: 'tools',
      separator: true,
    },
    'tool-global-connect': {
      group: 'tools',
      className: 'bpmn-icon-connection-multi',
      title: 'Kích hoạt công cụ nối',
      action: {
        click(event) {
          globalConnect.toggle(event)
        },
      },
    },
  }
}
