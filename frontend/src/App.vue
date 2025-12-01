<script setup lang="ts">
import {ref, watch} from 'vue'
import MicrophoneButton from "./components/microphone-button.vue";

const input = ref("");
const renderBoard = ref<HTMLDivElement>();

watch(input, neo => {
  renderBoard.value!.innerHTML = markdown.render(neo)
})


function downloadInputAsFile(){
  const blob = new Blob([input.value], {type: "text/plain"})
  const a = document.createElement("a");
  a.href = URL.createObjectURL(blob);
  const date = new Date();
  a.download = `记录__${date.getFullYear()}-${date.getMonth()+1}-${date.getDate()}__${date.getHours()}:${date.getMinutes()}:${date.getSeconds()}.md`
  a.click();
}

function concat(s: string){
  input.value += s
}

</script>

<template>
  <div
    class="w-full h-full absolute flex flex-col">
    <header
      class="w-full flex z-1
      shadow hover:shadow-xl transition-all">
      <button
        @click="downloadInputAsFile"
        class="py-3 px-5 select-none
        font-serif
        bg-gray-50
        hover:bg-gray-200
        active:bg-gray-400">
        下载
      </button>
      <microphone-button
        @parsed="concat"
        class="py-3 px-5 select-none"/>
    </header>
    <div
      class="flex h-full w-full flex-row">
      <div
        class="flex !h-full !w-full !m-0">
        <textarea
          v-model="input"
          class="w-full h-auto
            outline-none
            resize-none
            border-2 border-[#E0E0E0] rounded
            bg-[#F0F0F0] hover:bg-[#EAEAEA]
            hover:shadow-xl
            overflow-y-auto
            !m-3 !py-2 !px-3 transition-all" />
      </div>
      <div
        class="flex h-full w-full ">
      <div
        ref="renderBoard"
        class="w-full h-auto
            select-none
            border-2 border-[#D0D0D0] rounded
            bg-[#DDDDDD]
            hover:shadow-xl
            overflow-y-auto
            !m-3 !py-2 !px-3 transition-all" />
      </div>
    </div>
  </div>
</template>

<style>
/* make mathjax happy */
span {
  display: inline-block !important;
}
</style>

<style scoped>

/* 自定义滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.dark ::-webkit-scrollbar-track {
  background: #374151;
}

::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #555;
}

.dark ::-webkit-scrollbar-thumb {
  background: #6b7280;
}

.dark ::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

</style>