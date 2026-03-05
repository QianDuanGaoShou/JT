<template>
  <div class="video-player-wrapper" ref="wrapperRef">
    <video
      ref="videoRef"
      class="video-js vjs-default-skin vjs-big-play-centered"
      :class="{ 'vjs-fluid': true }"
    ></video>

    <!-- Custom Overlay Controls -->
    <div class="custom-controls" v-if="playerReady">
      <!-- Playback Rate -->
      <div class="speed-selector">
        <el-dropdown trigger="click" @command="setPlaybackRate">
          <span class="speed-btn">{{ currentRate }}x</span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item
                v-for="rate in playbackRates"
                :key="rate"
                :command="rate"
                :class="{ active: currentRate === rate }"
              >
                {{ rate }}x
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import videojs from 'video.js'
import 'video.js/dist/video-js.css'

const props = defineProps({
  src:             { type: String, required: true },
  initialPosition: { type: Number, default: 0 },
  autoplay:        { type: Boolean, default: false },
  poster:          { type: String, default: '' }
})

const emit = defineEmits(['progress-update', 'ended', 'play', 'pause'])

const wrapperRef   = ref(null)
const videoRef     = ref(null)
const playerReady  = ref(false)
const currentRate  = ref(1)
const playbackRates= [0.5, 0.75, 1, 1.25, 1.5, 2]

let player         = null
let progressTimer  = null
let seeked         = false

const initPlayer = () => {
  if (!videoRef.value) return

  player = videojs(videoRef.value, {
    autoplay:       props.autoplay,
    controls:       true,
    fluid:          true,
    preload:        'metadata',
    poster:         props.poster,
    playbackRates,
    userActions:    { hotkeys: true },
    sources:        [{ src: props.src, type: guessType(props.src) }],
    controlBar: {
      children: [
        'playToggle',
        'progressControl',
        'volumePanel',
        'currentTimeDisplay',
        'timeDivider',
        'durationDisplay',
        'playbackRateMenuButton',
        'fullscreenToggle'
      ]
    }
  })

  player.ready(() => {
    playerReady.value = true

    // Seek to initial position
    if (props.initialPosition > 0) {
      player.one('loadedmetadata', () => {
        player.currentTime(props.initialPosition)
        seeked = true
      })
    }

    // Auto-save progress every 5 seconds
    progressTimer = setInterval(() => {
      if (player && !player.paused() && !player.ended()) {
        const position   = player.currentTime()
        const duration   = player.duration()
        const percentage = duration ? Math.round((position / duration) * 100) : 0
        emit('progress-update', { position, percentage })
      }
    }, 5000)

    // Events
    player.on('ended', () => emit('ended'))
    player.on('play',  () => emit('play'))
    player.on('pause', () => emit('pause'))
    player.on('ratechange', () => {
      currentRate.value = player.playbackRate()
    })
  })
}

const guessType = (url) => {
  if (!url) return 'video/mp4'
  const ext = url.split('?')[0].split('.').pop().toLowerCase()
  const map = { mp4: 'video/mp4', webm: 'video/webm', ogg: 'video/ogg', m3u8: 'application/x-mpegURL', mov: 'video/mp4' }
  return map[ext] || 'video/mp4'
}

const setPlaybackRate = (rate) => {
  if (player) {
    player.playbackRate(rate)
    currentRate.value = rate
  }
}

// Reload when src changes
watch(() => props.src, (newSrc) => {
  if (player && newSrc) {
    player.src([{ src: newSrc, type: guessType(newSrc) }])
    player.load()
    if (props.initialPosition > 0) {
      player.one('loadedmetadata', () => {
        player.currentTime(props.initialPosition)
      })
    }
  }
})

onMounted(() => {
  initPlayer()
})

onUnmounted(() => {
  clearInterval(progressTimer)
  if (player) {
    // Emit final progress before destroy
    if (!player.ended()) {
      const pos = player.currentTime()
      const dur = player.duration()
      if (pos > 0 && dur > 0) {
        emit('progress-update', {
          position:   pos,
          percentage: Math.round((pos / dur) * 100)
        })
      }
    }
    player.dispose()
    player = null
  }
})
</script>

<style scoped>
.video-player-wrapper {
  position: relative;
  width: 100%;
  background: #000;
  border-radius: 10px;
  overflow: hidden;
}

.video-js {
  width: 100% !important;
  border-radius: 10px !important;
}

.custom-controls {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 10;
  pointer-events: auto;
}

.speed-btn {
  display: inline-flex;
  align-items: center;
  padding: 3px 8px;
  background: rgba(0,0,0,0.7);
  color: #FFB000;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  border: 1px solid rgba(255,176,0,0.4);
  transition: all 0.2s;
  &:hover { background: rgba(255,176,0,0.2); }
}

/* Video.js style overrides */
:deep(.video-js) {
  font-family: inherit;

  .vjs-big-play-button {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    background: rgba(255,176,0,0.2);
    border: 2px solid #FFB000;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    margin: 0;
    transition: all 0.2s;

    .vjs-icon-placeholder::before {
      font-size: 28px;
      color: #FFB000;
      line-height: 60px;
    }

    &:hover {
      background: rgba(255,176,0,0.85);
      .vjs-icon-placeholder::before { color: #121212; }
    }
  }

  .vjs-control-bar {
    background: linear-gradient(transparent, rgba(0,0,0,0.8));
    height: 40px;
    padding: 0 8px;
  }

  .vjs-play-progress,
  .vjs-volume-level {
    background: #FFB000;
  }

  .vjs-play-progress::before {
    color: #FFB000;
  }

  .vjs-slider:focus { outline: none; }

  .vjs-current-time,
  .vjs-duration,
  .vjs-time-divider { font-size: 12px; line-height: 40px; }

  .vjs-playback-rate .vjs-playback-rate-value {
    font-size: 12px;
    line-height: 40px;
    color: #FFB000;
  }

  .vjs-menu {
    background: #1E1E1E;
    border: 1px solid #333;
    border-radius: 6px;
  }

  .vjs-menu-item {
    color: #A0A0A0;
    &:hover { background: rgba(255,176,0,0.1); color: #FFB000; }
    &.vjs-selected { color: #FFB000; font-weight: 600; }
  }
}

/* Dropdown active item */
:deep(.el-dropdown-menu__item.active) {
  color: #FFB000 !important;
  font-weight: 600;
}
</style>
