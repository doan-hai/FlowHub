<script setup>
import BpmnCustomContextPad from '@/utils/BpmnCustomContextPad.js'
import { onMounted, ref } from 'vue'
import BpmnModeler from 'bpmn-js/lib/Modeler'

import BpmnCustomRenderer from '@/utils/BpmnCustomRenderer'
import BpmnCustomPalette from '@/utils/BpmnCustomPalette'

const bpmnContainer = ref(null)

onMounted(() => {
  const modeler = new BpmnModeler({
    container: bpmnContainer.value,
    // keyboard: { bindTo: window },
    additionalModules: [
      {
        __init__: ['bpmnCustomRenderer'],
        bpmnCustomRenderer: ['type', BpmnCustomRenderer],
      },
      { paletteProvider: ['type', BpmnCustomPalette] },
      {
        __init__: ['customContextPad'],
        customContextPad: ['type', BpmnCustomContextPad],
      },
      {
        __init__: ['customConnectRules'],
        customConnectRules: ['type', BpmnCustomConnectRules],
      },
    ],
  })

  // Load diagram trắng ban đầu
  const emptyDiagram = `<?xml version="1.0" encoding="UTF-8"?>
    <bpmn:definitions xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                      xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
                      xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                      xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
                      id="Definitions_1"
                      targetNamespace="http://bpmn.io/schema/bpmn">
      <bpmn:process id="Process_1" isExecutable="false">
      </bpmn:process>
      <bpmndi:BPMNDiagram id="BPMNDiagram_1">
        <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="Process_1" />
      </bpmndi:BPMNDiagram>
    </bpmn:definitions>`

  modeler.importXML(emptyDiagram)
})
</script>

<template>
  <div ref="bpmnContainer" class="bpmn-container"></div>
</template>

<style scoped>
.bpmn-container {
  width: 100%;
  height: 600px;
  border: 1px solid #ccc;
}
</style>

<style>
.bpmn-icon-user-task:before {
  content: '';
  display: inline-block;
  width: 24px;
  height: 24px;
  background-image: url('/icons/switch-task.svg') !important;
  background-size: contain !important;
  background-repeat: no-repeat !important;

  font-family: initial !important;
  color: transparent !important;
}
.entry.bpmn-icon-user-task:hover:before {
  background-image: url('/icons/switch-task-hover.svg') !important;
}
.bpmn-icon-manual-task:before {
  content: '';
  display: inline-block;
  width: 24px;
  height: 24px;
  background-image: url('/icons/join-task.svg') !important;
  background-size: contain !important;
  background-repeat: no-repeat !important;

  font-family: initial !important;
  color: transparent !important;
}
.entry.bpmn-icon-manual-task:hover:before {
  background-image: url('/icons/join-task-hover.svg') !important;
}
.bpmn-icon-script-task:before {
  content: '';
  display: inline-block;
  width: 24px;
  height: 24px;
  background-image: url('/icons/broadcast-task.svg') !important;
  background-size: contain !important;
  background-repeat: no-repeat !important;

  font-family: initial !important;
  color: transparent !important;
}
.entry.bpmn-icon-script-task:hover:before {
  background-image: url('/icons/broadcast-task-hover.svg') !important;
}
.bpmn-icon-service-task:before {
  content: '';
  display: inline-block;
  width: 24px;
  height: 24px;
  background-image: url('/icons/join-broadcast-task.svg') !important;
  background-size: contain !important;
  background-repeat: no-repeat !important;

  font-family: initial !important;
  color: transparent !important;
}
.entry.bpmn-icon-service-task:hover:before {
  background-image: url('/icons/join-broadcast-task-hover.svg') !important;
}
.bpmn-icon-gateway-xor:before {
  content: '';
  display: inline-block;
  width: 24px;
  height: 24px;
  background-image: url('/icons/condition-flow.svg') !important;
  background-size: contain !important;
  background-repeat: no-repeat !important;

  font-family: initial !important;
  color: transparent !important;
}
.entry.bpmn-icon-gateway-xor:hover:before {
  background-image: url('/icons/condition-flow-hover.svg') !important;
}
.bpmn-icon-gateway-parallel:before {
  content: '';
  display: inline-block;
  width: 24px;
  height: 24px;
  background-image: url('/icons/rollback.svg') !important;
  background-size: contain !important;
  background-repeat: no-repeat !important;

  font-family: initial !important;
  color: transparent !important;
}
.entry.bpmn-icon-gateway-parallel:hover:before {
  background-image: url('/icons/rollback-hover.svg') !important;
}
.bpmn-icon-intermediate-event-catch-timer:before {
  content: '';
  display: inline-block;
  width: 24px;
  height: 24px;
  background-image: url('/icons/timer.svg') !important;
  background-size: contain !important;
  background-repeat: no-repeat !important;

  font-family: initial !important;
  color: transparent !important;
}
.entry.bpmn-icon-intermediate-event-catch-timer:hover:before {
  background-image: url('/icons/timer-hover.svg') !important;
}
.djs-context-pad .entry {
  display: none !important;
}

.djs-context-pad .entry.bpmn-icon-connection-multi {
  display: inline-block !important;
}

.djs-context-pad .entry.bpmn-icon-trash {
  display: inline-block !important;
}
</style>
