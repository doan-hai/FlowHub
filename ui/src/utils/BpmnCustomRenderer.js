import BaseRenderer from 'diagram-js/lib/draw/BaseRenderer'
import { append as svgAppend, create as svgCreate } from 'tiny-svg'
import inherits from 'inherits'

const HIGH_PRIORITY = 2000

const CUSTOM_TYPES = [
  'bpmn:UserTask',
  'bpmn:ServiceTask',
  'bpmn:ScriptTask',
  'bpmn:ManualTask',
  'bpmn:ExclusiveGateway',
  'bpmn:ParallelGateway',
  'bpmn:IntermediateCatchEvent',
]

export default function BpmnCustomRenderer(eventBus, styles, bpmnRenderer) {
  BaseRenderer.call(this, eventBus, HIGH_PRIORITY)
  this.bpmnRenderer = bpmnRenderer
}

inherits(BpmnCustomRenderer, BaseRenderer)

BpmnCustomRenderer.$inject = ['eventBus', 'styles', 'bpmnRenderer']

BpmnCustomRenderer.prototype.canRender = function (element) {
  return element.businessObject
    && CUSTOM_TYPES.includes(element.businessObject.$type)
}

BpmnCustomRenderer.prototype.drawShape = function (parentNode, element) {
  const type = element.businessObject.$type
  const width = element.width
  const height = element.height

  if (type === 'bpmn:IntermediateCatchEvent') {
    const shape = this.bpmnRenderer.drawShape(parentNode, element)

    const cx = element.width / 2
    const cy = element.height / 2
    const icon = svgCreate('image')

    icon.setAttributeNS('http://www.w3.org/1999/xlink',
      'xlink:href',
      '/icons/timer.svg')

    icon.setAttribute('width', 20)
    icon.setAttribute('height', 20)
    icon.setAttribute('x', cx - 10)
    icon.setAttribute('y', cy - 10)
    svgAppend(parentNode, icon)

    return shape
  }

  if (type === 'bpmn:ExclusiveGateway'
    || type === 'bpmn:ParallelGateway') {
    const cx = width / 2
    const cy = height / 2
    const diamond = svgCreate('polygon')

    diamond.setAttribute('points',
      `${cx},0 ${width},${cy} ${cx},${height} 0,${cy}`,
    )
    diamond.setAttribute('fill', '#ffffff')
    diamond.setAttribute('stroke', '#000000')
    diamond.setAttribute('stroke-width', 2)
    svgAppend(parentNode, diamond)

    let href = '/icons/default-gateway.svg'
    if (type === 'bpmn:ExclusiveGateway')
      href = '/icons/condition-flow.svg'
    if (type === 'bpmn:ParallelGateway')
      href = '/icons/rollback.svg'

    const icon = svgCreate('image')

    icon.setAttributeNS('http://www.w3.org/1999/xlink', 'xlink:href', href)
    icon.setAttribute('width', 16)
    icon.setAttribute('height', 16)
    icon.setAttribute('x', cx - 8)
    icon.setAttribute('y', cy - 8)
    svgAppend(parentNode, icon)

    return diamond
  }

  const rect = svgCreate('rect')

  rect.setAttribute('width', width)
  rect.setAttribute('height', height)
  rect.setAttribute('rx', 8)
  rect.setAttribute('ry', 8)
  rect.setAttribute('fill', '#fff')
  rect.setAttribute('stroke', '#000000')
  rect.setAttribute('stroke-width', 2)
  svgAppend(parentNode, rect)

  const icon = svgCreate('image')
  let href = '/icons/default-task.svg'
  switch (type) {
    case 'bpmn:UserTask':
      href = '/icons/switch-task.svg'
      break
    case 'bpmn:ServiceTask':
      href = '/icons/join-broadcast-task.svg'
      break
    case 'bpmn:ScriptTask':
      href = '/icons/broadcast-task.svg'
      break
    case 'bpmn:ManualTask':
      href = '/icons/join-task.svg'
      break
  }
  icon.setAttributeNS('http://www.w3.org/1999/xlink', 'xlink:href', href)
  icon.setAttribute('width', 24)
  icon.setAttribute('height', 24)
  icon.setAttribute('x', (width - 24) / 2)
  icon.setAttribute('y', (height - 24) / 2)
  svgAppend(parentNode, icon)

  return rect
}

BpmnCustomRenderer.prototype.getShapePath = function(shape) {
  return this.bpmnRenderer.getShapePath(shape)
}
