<script setup lang="ts">
import {ref} from "vue";

const emits = defineEmits<{
  'parsed': [string];
}>()


// region recording
let recorder: MediaRecorder | null
let schedule = 0
const recording = ref(false)

async function clickRecording(){
  if (recording.value)
    await stopRecording()
  else
    await startRecording()
}

async function makeRecording() {
  const stream = await navigator.mediaDevices.getUserMedia({audio: true})
  recorder = new MediaRecorder(stream, {
    mimeType: "audio/webm"
  })
  recorder.ondataavailable = event => {
    handle(fetch("/api/parse", {
      method: "POST",
      body: event.data
    }).then(r=>r.text()))
  }
  recorder.start();
}

function flushRecording() {
  recorder!.stop();
}

async function startRecording() {
  recording.value = true;
  
  await makeRecording()
  schedule = setInterval(async ()=>{
    flushRecording()
    await makeRecording()
  }, 3000)
}

async function stopRecording() {
  recording.value = false
  clearInterval(schedule)
  flushRecording()
}

// endregion

// region handle
async function handle(text: Promise<string>){
  const body = (await text).trim()
  if (body.length == 0)
    return;
  const resp = await fetch('/api/translate', {
    method: "POST",
    body
  })
  emits('parsed', await resp.text())
}
// endregion
</script>

<template>
  <button
    @click="clickRecording"
    class="bg-gray-50
        hover:bg-gray-200
        active:bg-gray-400">
    <svg
      class="w-8 h-8"
      width="14" height="19"
      viewBox="0 0 14 19">
      <g id="Page-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
        <path d="M7,12 C8.66,12 9.99,10.66 9.99,9 L10,3 C10,1.34 8.66,0 7,0 C5.34,0 4,1.34 4,3 L4,9 C4,10.66 5.34,12 7,12 Z M12.3,9 C12.3,12 9.76,14.1 7,14.1 C4.24,14.1 1.7,12 1.7,9 L0,9 C0,12.41 2.72,15.23 6,15.72 L6,19 L8,19 L8,15.72 C11.28,15.24 14,12.42 14,9 L12.3,9 Z"
              id="mic"
              :fill="recording ? '#FF0000' : '#A0A0A0'"
              fill-rule="nonzero"></path>
      </g>
    </svg>
  </button>
</template>