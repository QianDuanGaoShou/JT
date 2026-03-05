<template>
  <div class="my-courses-page">
    <div class="page-container">
      <!-- Header -->
      <div class="page-top">
        <div>
          <h1 class="page-title">我的课程</h1>
          <p class="page-desc">共购买 {{ courses.length }} 门课程</p>
        </div>
        <el-input
          v-model="searchQuery"
          placeholder="搜索我的课程..."
          :prefix-icon="Search"
          clearable
          style="width:240px;"
          @input="handleSearch"
        />
      </div>

      <!-- Stats Row -->
      <div class="stats-row">
        <div class="stat-card">
          <div class="stat-icon">📚</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalCourses }}</div>
            <div class="stat-label">已购课程</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">⏱</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalHours }}h</div>
            <div class="stat-label">学习时长</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">✅</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.completedCourses }}</div>
            <div class="stat-label">已完成</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🎯</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.avgProgress }}%</div>
            <div class="stat-label">平均进度</div>
          </div>
        </div>
      </div>

      <!-- Loading skeleton -->
      <div v-if="loading" class="course-list">
        <div v-for="i in 3" :key="i" class="course-skeleton">
          <el-skeleton :rows="3" animated />
        </div>
      </div>

      <!-- Course List -->
      <template v-else>
        <div v-if="filteredCourses.length" class="course-list">
          <div
            v-for="item in filteredCourses"
            :key="item.id"
            class="course-list-item guitar-card"
          >
            <div class="course-cover-wrap">
              <img
                :src="item.course?.coverImage || item.coverImage || '/placeholder-course.jpg'"
                :alt="item.course?.title || item.title"
                class="course-cover"
              />
              <div class="progress-overlay">
                <el-progress
                  type="circle"
                  :percentage="item.progress || 0"
                  :color="progressColor(item.progress)"
                  :width="56"
                  :stroke-width="4"
                  :show-text="true"
                />
              </div>
            </div>

            <div class="course-body">
              <div class="course-meta-top">
                <el-tag size="small" type="warning" effect="plain">
                  {{ item.course?.category || item.category }}
                </el-tag>
                <span class="purchase-date">购买于 {{ formatDate(item.createdAt) }}</span>
              </div>

              <h3 class="course-title">{{ item.course?.title || item.title }}</h3>
              <p class="course-teacher">
                <el-icon><User /></el-icon>
                {{ item.course?.teacherName || item.teacherName }}
              </p>

              <!-- Progress Bar -->
              <div class="progress-section">
                <div class="progress-header">
                  <span class="progress-text">学习进度</span>
                  <span class="progress-pct" :style="{ color: progressColor(item.progress) }">
                    {{ item.progress || 0 }}%
                  </span>
                </div>
                <el-progress
                  :percentage="item.progress || 0"
                  :color="progressColor(item.progress)"
                  :show-text="false"
                  :stroke-width="6"
                />
                <div class="progress-detail">
                  已学 {{ item.watchedVideos || 0 }} / {{ item.totalVideos || 0 }} 节
                </div>
              </div>
            </div>

            <div class="course-actions">
              <el-button
                type="primary"
                class="btn-gradient continue-btn"
                @click="continueLearning(item)"
              >
                <el-icon><VideoPlay /></el-icon>
                {{ item.progress > 0 ? '继续学习' : '开始学习' }}
              </el-button>
              <el-button
                style="background:#2A2A2A;border-color:#333;color:#A0A0A0;"
                @click="viewCourse(item)"
              >
                课程详情
              </el-button>
            </div>
          </div>
        </div>

        <!-- Empty -->
        <div v-else class="empty-state">
          <span style="font-size:64px;">🎸</span>
          <h3>{{ searchQuery ? '没有找到相关课程' : '还没有购买课程' }}</h3>
          <p>{{ searchQuery ? '换个关键词试试吧' : '去课程广场探索，找到你感兴趣的课程' }}</p>
          <el-button
            type="primary"
            class="btn-gradient"
            @click="$router.push('/courses')"
          >
            去课程广场
          </el-button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, VideoPlay, User } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { courseApi } from '@/api/course'
import { studyApi } from '@/api/study'

const router = useRouter()
const loading     = ref(true)
const courses     = ref([])
const searchQuery = ref('')
const stats       = ref({
  totalCourses: 0,
  totalHours: 0,
  completedCourses: 0,
  avgProgress: 0
})

const filteredCourses = computed(() => {
  if (!searchQuery.value) return courses.value
  const q = searchQuery.value.toLowerCase()
  return courses.value.filter(c =>
    (c.course?.title || c.title || '').toLowerCase().includes(q) ||
    (c.course?.teacherName || c.teacherName || '').toLowerCase().includes(q)
  )
})

const progressColor = (pct) => {
  if (!pct) return '#444'
  if (pct >= 80) return '#52C41A'
  if (pct >= 40) return '#FFB000'
  return '#1890FF'
}

const formatDate = (d) => d ? dayjs(d).format('YYYY/MM/DD') : ''

let searchTimer = null
const handleSearch = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {}, 300)
}

const continueLearning = (item) => {
  const id = item.courseId || item.course?.id || item.id
  router.push(`/courses/${id}`)
}
const viewCourse = (item) => {
  const id = item.courseId || item.course?.id || item.id
  router.push(`/courses/${id}`)
}

const loadData = async () => {
  loading.value = true
  try {
    const [ordersRes, statsRes] = await Promise.allSettled([
      courseApi.myOrders({ page: 0, size: 100 }),
      studyApi.stats()
    ])

    if (ordersRes.status === 'fulfilled') {
      const data = ordersRes.value?.data || ordersRes.value
      courses.value = data.content || data.list || data || []
    }

    if (statsRes.status === 'fulfilled') {
      const s = statsRes.value?.data || statsRes.value
      stats.value = {
        totalCourses:    s?.totalCourses    ?? courses.value.length,
        totalHours:      s?.totalHours      ?? 0,
        completedCourses:s?.completedCourses?? 0,
        avgProgress:     s?.avgProgress     ?? 0
      }
    } else {
      stats.value.totalCourses = courses.value.length
    }
  } catch (err) {
    ElMessage.error('加载课程列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.my-courses-page { min-height: calc(100vh - 64px); background: #121212; padding-bottom: 60px; }
.page-container { max-width: 1000px; margin: 0 auto; padding: 32px 24px; }

.page-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.page-title { font-size: 26px; font-weight: 800; color: #E8E8E8; margin-bottom: 4px; }
.page-desc  { font-size: 14px; color: #666; }

/* Stats */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 28px;
}
.stat-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.25s;
  &:hover { border-color: rgba(255,176,0,0.3); }
}
.stat-icon { font-size: 28px; }
.stat-value { font-size: 22px; font-weight: 700; color: #E8E8E8; }
.stat-label { font-size: 12px; color: #666; margin-top: 2px; }

/* Course List */
.course-list { display: flex; flex-direction: column; gap: 16px; }
.course-list-item {
  display: grid;
  grid-template-columns: 200px 1fr auto;
  gap: 20px;
  padding: 20px;
  align-items: center;
  border-radius: 12px;
  cursor: default;

  &:hover { transform: translateY(-2px); }
}

.course-cover-wrap {
  position: relative;
  width: 200px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}
.course-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.progress-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%,-50%);
  background: rgba(0,0,0,0.65);
  border-radius: 50%;
}

.course-body { flex: 1; }
.course-meta-top {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}
.purchase-date { font-size: 12px; color: #555; }
.course-title  { font-size: 17px; font-weight: 700; color: #E8E8E8; margin-bottom: 6px; }
.course-teacher {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #A0A0A0;
  margin-bottom: 12px;
}

.progress-section { max-width: 400px; }
.progress-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}
.progress-text { font-size: 12px; color: #666; }
.progress-pct  { font-size: 13px; font-weight: 600; }
.progress-detail { font-size: 11px; color: #555; margin-top: 5px; }

.course-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex-shrink: 0;
}
.continue-btn { padding: 10px 20px; display: inline-flex; align-items: center; gap: 6px; }

/* Skeleton */
.course-skeleton {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  padding: 20px;
  height: 140px;
}

/* Empty */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 80px 24px;
  color: #555;
  h3 { font-size: 18px; color: #666; }
  p  { font-size: 14px; }
}
</style>
