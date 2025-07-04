// src/utils/FilterContextPad.js

export default function BpmnCustomContextPad(contextPad) {
  // Lưu nguyên hàm gốc (nếu bạn cần call dần)
  const originalGetEntries = contextPad.getContextPadEntries

  // Ghi đè luôn method
  contextPad.getContextPadEntries = function(element) {
    // Lấy tất cả entry mặc định
    const entries = originalGetEntries.call(contextPad, element)
    console.log(entries)

    // Chỉ giữ 2 key sau
    const allowed = ['delete', 'connect']

    const filtered = {}
    Object.keys(entries).forEach(key => {
      if (allowed.includes(key)) {
        filtered[key] = entries[key]
      }
    })

    return filtered
  }
}

BpmnCustomContextPad.$inject = ['contextPad']
