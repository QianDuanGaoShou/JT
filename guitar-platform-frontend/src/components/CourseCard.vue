<template>
  <div class="course-card guitar-card" @click="emit('click', course)">
    <!-- Cover Image -->
    <div class="card-cover">
      <img
        :src="course.coverImage || '/placeholder-course.jpg'"
        :alt="course.title"
        class="cover-img"
        loading="lazy"
      />
      <!-- Category Badge -->
      <div class="category-badge">{{ course.category }}</div>
      <!-- Duration Badge -->
      <div class="duration-badge" v-if="course.totalDuration">
        <el-icon size="11"><VideoPlay /></el-icon>
        {{ formatDuration(course.totalDuration) }}
      </div>
      <!-- Purchased Badge -->
      <div class="purchased-badge" v-if="course.purchased">
        <el-icon size="11"><CircleCheck /></el-icon>
        已购买
      </div>
      <!-- Hover Overlay -->
      <div class="cover-overlay">
        <el-icon size="32" color="#FFB000"><VideoPlay /></el-icon>
        <span>查看课程</span>
      </div>
    </div>

    <!-- Card Body -->
    <div class="card-body">
      <h3 class="course-title" :title="course.title">{{ course.title }}</h3>

      <!-- Teacher Info -->
      <div class="teacher-row">
        <el-avatar
          :size="20"
          :src="course.teacherAvatar"
          class="teacher-avatar"
        >
          {{ (course.teacherName || '?')[0] }}
        </el-avatar>
        <span class="teacher-name">{{ course.teacherName }}</span>
      </div>

      <!-- Stats Row -->
      <div class="stats-row">
        <div class="stat-item">
          <el-icon size="12" color="#A0A0A0"><User /></el-icon>
          <span>{{ course.studentCount || 0 }} 学员</span>
        </div>
        <div class="stat-item" v-if="course.rating">
          <el-icon size="12" color="#FFB000"><Star /></el-icon>
          <span style="color:#FFB000;">{{ course.rating }}</span>
        </div>
        <div class="stat-item" v-if="course.totalVideos">
          <el-icon size="12" color="#A0A0A0"><Film /></el-icon>
          <span>{{ course.totalVideos }} 节</span>
        </div>
      </div>

      <!-- Price Row -->
      <div class="price-row">
        <div class="price-block">
          <span v-if="course.price === 0" class="price-free">免费</span>
          <span v-else class="price-main">¥{{ course.price }}</span>
          <span v-if="course.originalPrice && course.originalPrice > course.price" class="price-original">
            ¥{{ course.originalPrice }}
          </span>
        </div>
        <el-button
          v-if="!course.purchased"
          type="primary"
          size="small"
          class="buy-btn btn-gradient"
          @click.stop="emit('click', course)"
        >
          {{ course.price === 0 ? '免费学习' : '立即购买' }}
        </el-button>
        <el-button
          v-else
          size="small"
          class="continue-btn btn-outline-gold"
          @click.stop="emit('click', course)"
        >
          继续学习
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { VideoPlay, CircleCheck, User, Star, Film } from '@element-plus/icons-vue'

const props = defineProps({
  course: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['click'])

const formatDuration = (totalMinutes) => {
  if (!totalMinutes) return ''
  const h = Math.floor(totalMinutes / 60)
  const m = totalMinutes % 60
  if (h > 0) return `${h}h${m > 0 ? m + 'm' : ''}`
  return `${m}m`
}
</script>

<style scoped>
.course-card {
  display: flex;
  flex-direction: column;
  cursor: pointer;
  user-select: none;
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.25s ease;

  &:hover {
    transform: translateY(-4px);
    border-color: rgba(255,176,0,0.25);
    box-shadow: 0 8px 24px rgba(0,0,0,0.5);

    .cover-overlay { opacity: 1; }
    .cover-img { transform: scale(1.04); }
  }
}

/* Cover */
.card-cover {
  position: relative;
  width: 100%;
  aspect-ratio: 16/9;
  overflow: hidden;
  background: #252525;
  flex-shrink: 0;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
  display: block;
}

.category-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  padding: 2px 8px;
  background: rgba(255,176,0,0.85);
  color: #121212;
  font-size: 11px;
  font-weight: 700;
  border-radius: 100px;
  letter-spacing: 0.2px;
}

.duration-badge {
  position: absolute;
  bottom: 8px;
  left: 8px;
  display: flex;
  align-items: center;
  gap: 3px;
  padding: 2px 7px;
  background: rgba(0,0,0,0.75);
  color: #E8E8E8;
  font-size: 11px;
  border-radius: 4px;
}

.purchased-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  display: flex;
  align-items: center;
  gap: 3px;
  padding: 2px 7px;
  background: rgba(82,196,26,0.9);
  color: #fff;
  font-size: 11px;
  font-weight: 600;
  border-radius: 4px;
}

.cover-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.55);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.25s;
  color: #E8E8E8;
  font-size: 13px;
  font-weight: 600;
}

/* Card Body */
.card-body {
  flex: 1;
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.course-title {
  font-size: 14px;
  font-weight: 700;
  color: #E8E8E8;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 40px;
  transition: color 0.2s;
}
.course-card:hover .course-title { color: #FFB000; }

.teacher-row {
  display: flex;
  align-items: center;
  gap: 6px;
}
.teacher-avatar { background: #FFB000; color: #121212; font-weight: 700; flex-shrink: 0; }
.teacher-name   { font-size: 12px; color: #A0A0A0; }

.stats-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 2px;
}
.stat-item {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  color: #666;
}

/* Price */
.price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
  padding-top: 8px;
  border-top: 1px solid #252525;
}

.price-block { display: flex; align-items: baseline; gap: 6px; }
.price-main {
  font-size: 18px;
  font-weight: 800;
  color: #FFB000;
}
.price-free {
  font-size: 16px;
  font-weight: 700;
  color: #52C41A;
}
.price-original {
  font-size: 12px;
  color: #444;
  text-decoration: line-through;
}

.buy-btn, .continue-btn {
  font-size: 12px;
  padding: 6px 14px;
  border-radius: 6px;
  font-weight: 600;
}
</style>
