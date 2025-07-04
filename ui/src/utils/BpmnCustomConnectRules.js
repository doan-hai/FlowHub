export default function BpmnCustomConnectRules(eventBus) {
  eventBus.on('connect.start', 1000, event => {
    const { source, target } = event.context

    console.log('source.type', source.type)
    console.log('target.type', target.type)
    console.log('source.outgoing', source.outgoing)
    console.log('target.outgoing', target.outgoing)
    console.log('source.incoming', source.incoming)
    console.log('target.incoming', target.incoming)

    // --- SWITCH TASK (bpmn:UserTask): 1 in, 1 out ---
    if (source && source.type === 'bpmn:UserTask'
      && (source.outgoing || []).length >= 1) {
      // eslint-disable-next-line no-alert
      window.alert('Switch Task chỉ được 1 outgoing connection')
      event.stopPropagation()
      event.preventDefault()

      return
    }
    if (target && target.type === 'bpmn:UserTask'
      && (target.incoming || []).length >= 1) {
      // eslint-disable-next-line no-alert
      window.alert('Switch Task chỉ nhận 1 incoming connection')
      event.stopPropagation()
      event.preventDefault()

      return
    }

    // --- JOIN TASK (bpmn:ManualTask): many in, 1 out ---
    if (source && source.type === 'bpmn:ManualTask'
      && (source.outgoing || []).length >= 1) {
      // eslint-disable-next-line no-alert
      window.alert('Join Task chỉ được 1 outgoing connection')
      event.stopPropagation()
      event.preventDefault()

      return
    }

    // --- BROADCAST TASK (bpmn:ScriptTask): 1 in, many out ---
    if (target && target.type === 'bpmn:ScriptTask'
      && (target.incoming || []).length >= 1) {
      // eslint-disable-next-line no-alert
      window.alert('Broadcast Task chỉ nhận 1 incoming connection')
      event.stopPropagation()
      event.preventDefault()
    }
  })
}

BpmnCustomConnectRules.$inject = ['eventBus']
