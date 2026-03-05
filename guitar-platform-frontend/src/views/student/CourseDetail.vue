<template>
  <div class="course-detail-page" v-loading="loading">
    <div v-if="course" class="detail-layout">

      <!-- Left: Video + Info -->
      <div class="left-panel">
        <!-- Video Player -->
        <div class="video-section">
          <VideoPlayer
            v-if="currentVideo"
            :key="currentVideo.id"
            :src="currentVideo.videoUrl"
            :initial-position="currentProgress"
            @progress-update="onProgressUpdate"
            @ended="onVideoEnded"
            class="main-player"
          />
          <div v-else class="video-placeholder">
            <el-icon size="64" color="#333"><VideoPlay /></el-icon>
            <p>选择章节开始学习</p>
          </div>
        </div>

        <!-- Video Info -->
        <div class="video-info" v-if="currentVideo">
          <h2 class="video-title">{{ currentVideo.title }}</h2>
          <div class="video-meta">
            <el-tag type="warning" effect="plain" size="small">{{ course.category }}</el-tag>
            <span class="meta-text">
              <el-icon><Clock /></el-icon>
              {{ formatDuration(currentVideo.duration) }}
            </span>
            <span class="meta-text" v-if="currentProgress > 0">
              <el-icon><Position /></el-icon>
              上次学到 {{ formatTime(currentProgress) }}
            </span>
          </div>
        </div>

        <!-- Course Info Card -->
        <div class="course-info-card">
          <div class="course-header-info">
            <div>
              <h1 class="course-name">{{ course.title }}</h1>
              <div class="teacher-info">
                <el-avatar :size="32" :src="course.teacherAvatar">{{ course.teacherName?.[0] }}</el-avatar>
                <span class="teacher-name">{{ course.teacherName }}</span>
                <el-tag size="small" style="background:rgba(255,176,0,0.1);border-color:#FFB000;color:#FFB000;">认证教师</el-tag>
              </div>
            </div>
            <div class="course-price-block">
              <div class="price-tag">¥{{ course.price }}</div>
              <el-button
                v-if="!isPurchased"
                type="primary"
                class="btn-gradient purchase-btn"
                :loading="purchasing"
                @click="handlePurchase"
              >
                立即购买
              </el-button>
              <div v-else class="purchased-badge">
                <el-icon color="#52C41A"><CircleCheck /></el-icon>
                已购买
              </div>
            </div>
          </div>

          <el-divider style="border-color:#2A2A2A;" />

          <div class="course-stats">
            <div class="c-stat">
              <el-icon color="#FFB000"><VideoPlay /></el-icon>
              {{ course.totalVideos || 0 }} 个视频
            </div>
            <div class="c-stat">
              <el-icon color="#FFB000"><User /></el-icon>
              {{ course.studentCount || 0 }} 名学员
            </div>
            <div class="c-stat">
              <el-icon color="#FFB000"><Star /></el-icon>
              {{ course.rating || 5.0 }} 分
            </div>
          </div>

          <p class="course-desc">{{ course.description }}</p>
        </div>
      </div>

      <!-- Right: Tabs -->
      <div class="right-panel">
        <el-tabs v-model="activeTab" class="detail-tabs">
          <!-- Tab 1: Chapters -->
          <el-tab-pane label="课程目录" name="chapters">
            <div class="chapters-list">
              <div
                v-for="chapter in course.chapters"
                :key="chapter.id"
                class="chapter-item"
              >
                <div
                  class="chapter-header"
                  @click="toggleChapter(chapter.id)"
                >
                  <el-icon class="chapter-icon" :class="{ open: expandedChapters.includes(chapter.id) }">
                    <ArrowRight />
                  </el-icon>
                  <span class="chapter-title">{{ chapter.title }}</span>
                  <span class="chapter-count">{{ chapter.videos?.length || 0 }} 节</span>
                </div>

                <Transition name="chapter-expand">
                  <div
                    v-show="expandedChapters.includes(chapter.id)"
                    class="videos-list"
                  >
                    <div
                      v-for="video in chapter.videos"
                      :key="video.id"
                      class="video-item"
                      :class="{
                        active: currentVideo?.id === video.id,
                        locked: !isPurchased && video.sortOrder > 1
                      }"
                      @click="playVideo(video)"
                    >
                      <el-icon class="video-icon" size="14">
                        <component :is="currentVideo?.id === video.id ? VideoPlay : CirclePlay" />
                      </el-icon>
                      <span class="v-title">{{ video.title }}</span>
                      <span class="v-duration">{{ formatDuration(video.duration) }}</span>
                      <el-icon v-if="!isPurchased && video.sortOrder > 1" size="12" color="#555">
                        <Lock />
                      </el-icon>
                    </div>
                  </div>
                </Transition>
              </div>
            </div>
          </el-tab-pane>

          <!-- Tab 2: Submit Practice -->
          <el-tab-pane label="上传练习" name="practice" :disabled="!isPurchased">
            <div class="practice-upload">
              <div class="practice-intro">
                <el-icon size="32" color="#FFB000"><VideoCamera /></el-icon>
                <h3>上传练习视频</h3>
                <p>录制你的练习，获得专业教师的点评和指导</p>
              </div>

              <el-form :model="practiceForm" label-position="top">
                <el-form-item label="选择关联章节">
                  <el-select
                    v-model="practiceForm.chapterId"
                    placeholder="选择练习对应的章节"
                    style="width:100%;"
                  >
                    <el-option
                      v-for="ch in course.chapters"
                      :key="ch.id"
                      :label="ch.title"
                      :value="ch.id"
                    />
                  </el-select>
                </el-form-item>

                <el-form-item label="练习说明">
                  <el-input
                    v-model="practiceForm.note"
                    type="textarea"
                    :rows="3"
                    placeholder="描述你练习的内容，或者想请教师重点点评的部分..."
                    maxlength="200"
                    show-word-limit
                  />
                </el-form-item>

                <el-form-item label="练习视频">
                  <el-upload
                    :auto-upload="false"
                    accept="video/*"
                    :limit="1"
                    :on-change="handlePracticeFile"
                    drag
                    class="practice-uploader"
                  >
                    <div class="upload-body">
                      <el-icon size="36" color="#FFB000"><Upload /></el-icon>
                      <p>点击或拖拽视频文件</p>
                      <p class="upload-hint">支持 MP4、MOV、AVI，最大 500MB</p>
                    </div>
                  </el-upload>

                  <el-progress
                    v-if="uploadProgress > 0"
                    :percentage="uploadProgress"
                    :color="'#FFB000'"
                    style="margin-top:8px;"
                  />
                </el-form-item>

                <el-button
                  type="primary"
                  class="btn-gradient"
                  style="width:100%;height:40px;"
                  :loading="submitting"
                  :disabled="!practiceFile"
                  @click="submitPractice"
                >
                  提交练习
                </el-button>
              </el-form>
            </div>
          </el-tab-pane>

          <!-- Tab 3: Practice Feedback -->
          <el-tab-pane label="练习反馈" name="feedback" :disabled="!isPurchased">
            <div class="feedback-list">
              <div v-if="practices.length === 0" class="empty-feedback">
                <el-icon size="48" color="#333"><EditPen /></el-icon>
                <p>还没有练习记录</p>
                <p style="font-size:12px;color:#555;">上传你的第一个练习视频吧！</p>
              </div>

              <div
                v-for="p in practices"
                :key="p.id"
                class="feedback-card"
              >
                <div class="feedback-header">
                  <div class="feedback-status">
                    <el-tag
                      :type="p.status === 'GRADED' ? 'success' : 'warning'"
                      effect="plain"
                      size="small"
                    >
                      {{ p.status === 'GRADED' ? '已批改' : '待批改' }}
                    </el-tag>
                    <span class="feedback-date">{{ formatDate(p.createdAt) }}</span>
                  </div>
                  <div v-if="p.score !== null" class="score-badge">
                    <span class="score-num">{{ p.score }}</span>
                    <span class="score-max">/100</span>
                  </div>
                </div>

                <p class="feedback-note">{{ p.note || '无说明' }}</p>

                <div v-if="p.teacherFeedback" class="teacher-comment">
                  <el-icon color="#FFB000" size="14"><ChatLineRound /></el-icon>
                  <p>{{ p.teacherFeedback }}</p>
                </div>

                <el-button
                  size="small"
                  class="btn-outline-gold"
                  style="margin-top:8px;"
                  @click="$router.push(`/practice/${p.id}/report`)"
                >
                  查看详情
                </el-button>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- Purchase Dialog -->
    <el-dialog
      v-model="showPurchaseDialog"
      title="确认购买"
      width="400px"
      :close-on-click-modal="false"
    >
      <div class="purchase-dialog-content">
        <div class="purchase-course-info">
          <img :src="course?.coverImage" alt="cover" class="purchase-cover" />
          <div>
            <h4>{{ course?.title }}</h4>
            <p>{{ course?.teacherName }}</p>
          </div>
        </div>
        <div class="purchase-amount">
          实付金额：<span class="amount">¥{{ course?.price }}</span>
        </div>
        <p style="font-size:12px;color:#666;text-align:center;">余额将从账户中扣除</p>
      </div>
      <template #footer>
        <el-button @click="showPurchaseDialog = false">取消</el-button>
        <el-button type="primary" class="btn-gradient" :loading="purchasing" @click="confirmPurchase">
          确认购买
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  VideoPlay, Clock, Position, ArrowRight, CircleCheck,
  CirclePlay, Lock, VideoCamera, Upload, EditPen,
  ChatLineRound, User, Star
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { courseApi } from '@/api/course'
import { studyApi } from '@/api/study'
import { practiceApi } from '@/api/practice'
import { uploadApi } from '@/api/upload'
import { useAuthStore } from '@/store/auth'
import VideoPlayer from '@/components/VideoPlayer.vue'

const route     = useRoute()
const router    = useRouter()
const authStore = useAuthStore()

const loading    = ref(true)
const course     = ref(null)
const activeTab  = ref('chapters')
const purchasing = ref(false)
const submitting = ref(false)

const isPurchased = ref(false)
const currentVideo = ref(null)
const currentProgress = ref(0)
const expandedChapters = ref([])
const showPurchaseDialog = ref(false)

const practices     = ref([])
const practiceFile  = ref(null)
const uploadProgress= ref(0)
const practiceForm  = reactive({ chapterId: null, note: '' })

// Progress auto-save
let progressTimer = null
let lastSavedPosition = 0

const loadCourse = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const res = await courseApi.detail(id)
    course.value = res.data || res

    // Check if purchased
    try {
      const ordersRes = await courseApi.myOrders({ courseId: id })
      const orders = (ordersRes.data || ordersRes).content || ordersRes.data || []
      isPurchased.value = Array.isArray(orders) ? orders.some(o => o.courseId == id) : false
    } catch {}

    // Expand first chapter by default
    if (course.value.chapters?.length) {
      expandedChapters.value = [course.value.chapters[0].id]
    }

    // Load practice records
    if (isPurchased.value) {
      loadPractices()
      loadStudyProgress()
    }
  } catch (err) {
    ElMessage.error('加载课程失败')
    router.push('/courses')
  } finally {
    loading.value = false
  }
}

const loadStudyProgress = async () => {
  try {
    const res = await studyApi.courseProgress(route.params.id)
    const data = res.data || res
    if (data?.lastVideoId) {
      // Find and set current video
      for (const ch of course.value.chapters || []) {
        const v = ch.videos?.find(v => v.id === data.lastVideoId)
        if (v) {
          currentVideo.value = v
          currentProgress.value = data.position || 0
          break
        }
      }
    }
  } catch {}
}

const loadPractices = async () => {
  try {
    const res = await practiceApi.list({ courseId: route.params.id })
    practices.value = (res.data || res).content || res.data || []
  } catch {}
}

const toggleChapter = (id) => {
  const idx = expandedChapters.value.indexOf(id)
  if (idx > -1) expandedChapters.value.splice(idx, 1)
  else expandedChapters.value.push(id)
}

const playVideo = (video) => {
  if (!isPurchased.value && video.sortOrder > 1) {
    ElMessage.warning('请先购买课程后观看')
    showPurchaseDialog.value = true
    return
  }
  currentVideo.value = video
  currentProgress.value = 0
  // Load saved progress for this video
  loadVideoProgress(video.id)
}

const loadVideoProgress = async (videoId) => {
  try {
    const res = await studyApi.records({ videoId })
    const data = res.data || res
    currentProgress.value = data?.position || 0
  } catch {}
}

const onProgressUpdate = async ({ position, percentage }) => {
  if (Math.abs(position - lastSavedPosition) < 5) return
  lastSavedPosition = position

  try {
    await studyApi.updateProgress({
      courseId: route.params.id,
      videoId:  currentVideo.value?.id,
      position,
      percentage
    })
  } catch {}
}

const onVideoEnded = () => {
  ElMessage.success('视频学习完成！')
}

// Purchase
const handlePurchase = () => {
  showPurchaseDialog.value = true
}

const confirmPurchase = async () => {
  purchasing.value = true
  try {
    await courseApi.purchase(route.params.id)
    isPurchased.value = true
    showPurchaseDialog.value = false
    ElMessage.success('购买成功！')
    loadPractices()
  } catch (err) {
    ElMessage.error(err.message || '购买失败，请检查余额')
  } finally {
    purchasing.value = false
  }
}

// Practice upload
const handlePracticeFile = (file) => {
  practiceFile.value = file.raw
}

const submitPractice = async () => {
  if (!practiceFile.value) return
  submitting.value = true
  uploadProgress.value = 0
  try {
    const fd = new FormData()
    fd.append('video', practiceFile.value)
    fd.append('courseId', route.params.id)
    if (practiceForm.chapterId) fd.append('chapterId', practiceForm.chapterId)
    if (practiceForm.note) fd.append('note', practiceForm.note)

    await practiceApi.submit(fd)
    ElMessage.success('练习已提交，等待老师批改！')
    practiceFile.value = null
    uploadProgress.value = 0
    practiceForm.chapterId = null
    practiceForm.note = ''
    activeTab.value = 'feedback'
    loadPractices()
  } catch (err) {
    ElMessage.error(err.message || '提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

// Formatters
const formatDuration = (sec) => {
  if (!sec) return '--'
  const m = Math.floor(sec / 60)
  const s = sec % 60
  return `${m}:${String(s).padStart(2, '0')}`
}
const formatTime = (sec) => {
  const m = Math.floor(sec / 60)
  const s = Math.floor(sec % 60)
  return `${m}分${s}秒`
}
const formatDate = (d) => d ? dayjs(d).format('MM-DD HH:mm') : ''

onMounted(loadCourse)
</script>

<style scoped>
.course-detail-page {
  min-height: calc(100vh - 64px);
  background: #121212;
  padding: 24px;
}

.detail-layout {
  max-width: 1280px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 24px;
  align-items: flex-start;
}

/* Left Panel */
.left-panel { display: flex; flex-direction: column; gap: 16px; }

.video-section {
  border-radius: 12px;
  overflow: hidden;
  background: #000;
  aspect-ratio: 16/9;
}

.video-placeholder {
  aspect-ratio: 16/9;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  background: #161616;
  border-radius: 12px;
  color: #555;
  font-size: 14px;
}

.main-player { width: 100%; height: 100%; }

.video-info { padding: 12px 0 0; }
.video-title { font-size: 18px; font-weight: 700; color: #E8E8E8; margin-bottom: 8px; }
.video-meta { display: flex; align-items: center; gap: 16px; flex-wrap: wrap; }
.meta-text { display: inline-flex; align-items: center; gap: 4px; font-size: 13px; color: #A0A0A0; }

.course-info-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  padding: 20px;
}
.course-header-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}
.course-name { font-size: 20px; font-weight: 700; color: #E8E8E8; margin-bottom: 10px; }
.teacher-info { display: flex; align-items: center; gap: 8px; }
.teacher-name { font-size: 14px; color: #C0C0C0; }

.course-price-block { text-align: right; flex-shrink: 0; }
.price-tag { font-size: 28px; font-weight: 800; color: #FFB000; margin-bottom: 10px; }
.purchase-btn { padding: 10px 24px; font-size: 14px; font-weight: 600; }
.purchased-badge { display: flex; align-items: center; gap: 6px; color: #52C41A; font-weight: 600; font-size: 15px; }

.course-stats {
  display: flex;
  gap: 24px;
  margin: 12px 0;
}
.c-stat { display: inline-flex; align-items: center; gap: 6px; font-size: 13px; color: #A0A0A0; }
.course-desc { font-size: 14px; color: #A0A0A0; line-height: 1.7; }

/* Right Panel */
.right-panel {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  overflow: hidden;
  position: sticky;
  top: 80px;
  max-height: calc(100vh - 100px);
  overflow-y: auto;
}

:deep(.el-tabs__header) { padding: 0 16px; }
:deep(.el-tabs__content) { padding: 0; }

/* Chapters */
.chapters-list { padding: 8px 0; }
.chapter-item { border-bottom: 1px solid #252525; }
.chapter-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;
  &:hover { background: rgba(255,255,255,0.03); }
}
.chapter-icon {
  color: #A0A0A0;
  transition: transform 0.2s;
  &.open { transform: rotate(90deg); }
}
.chapter-title { flex: 1; font-size: 13px; font-weight: 600; color: #E8E8E8; }
.chapter-count { font-size: 11px; color: #555; }

.videos-list { padding: 4px 0 8px; }
.video-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px 8px 32px;
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 6px;
  margin: 0 8px;

  &:hover { background: rgba(255,255,255,0.04); }
  &.active { background: rgba(255,176,0,0.1); }
  &.locked { opacity: 0.5; cursor: not-allowed; }
}
.video-icon { color: #A0A0A0; flex-shrink: 0; }
.video-item.active .video-icon { color: #FFB000; }
.v-title { flex: 1; font-size: 13px; color: #C0C0C0; }
.video-item.active .v-title { color: #FFB000; font-weight: 500; }
.v-duration { font-size: 11px; color: #555; }

/* Practice Upload */
.practice-upload { padding: 20px; }
.practice-intro { text-align: center; margin-bottom: 20px; h3 { font-size: 16px; font-weight: 600; color: #E8E8E8; margin: 8px 0 4px; } p { font-size: 13px; color: #666; } }
.upload-body { display: flex; flex-direction: column; align-items: center; gap: 8px; padding: 20px; }
.upload-body p { font-size: 14px; color: #A0A0A0; }
.upload-hint { font-size: 12px; color: #555 !important; }

:deep(.practice-uploader .el-upload-dragger) {
  background: #252525 !important;
  border-color: #333 !important;
  border-radius: 8px !important;
  &:hover { border-color: #FFB000 !important; }
}

/* Feedback */
.feedback-list { padding: 16px; display: flex; flex-direction: column; gap: 12px; }
.empty-feedback {
  text-align: center;
  padding: 40px 0;
  color: #555;
  p { margin: 8px 0; font-size: 14px; }
}
.feedback-card {
  background: #252525;
  border-radius: 10px;
  border: 1px solid #333;
  padding: 14px;
}
.feedback-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.feedback-status { display: flex; align-items: center; gap: 8px; }
.feedback-date { font-size: 12px; color: #555; }
.score-badge { display: flex; align-items: baseline; gap: 2px; }
.score-num { font-size: 24px; font-weight: 700; color: #FFB000; }
.score-max { font-size: 12px; color: #666; }
.feedback-note { font-size: 13px; color: #A0A0A0; margin: 4px 0; }
.teacher-comment {
  display: flex;
  gap: 8px;
  background: rgba(255,176,0,0.06);
  border-left: 3px solid #FFB000;
  border-radius: 0 6px 6px 0;
  padding: 8px 10px;
  margin: 8px 0;
  p { font-size: 13px; color: #C0C0C0; line-height: 1.5; }
}

/* Chapter animation */
.chapter-expand-enter-active, .chapter-expand-leave-active {
  transition: all 0.25s ease;
  overflow: hidden;
}
.chapter-expand-enter-from, .chapter-expand-leave-to {
  max-height: 0;
  opacity: 0;
}
.chapter-expand-enter-to, .chapter-expand-leave-from {
  max-height: 1000px;
  opacity: 1;
}

/* Purchase Dialog */
.purchase-dialog-content { padding: 8px 0; }
.purchase-course-info {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  padding: 12px;
  background: #252525;
  border-radius: 8px;
}
.purchase-cover { width: 80px; height: 56px; object-fit: cover; border-radius: 6px; }
.purchase-course-info h4 { font-size: 14px; color: #E8E8E8; margin-bottom: 4px; }
.purchase-course-info p { font-size: 12px; color: #666; }
.purchase-amount { text-align: center; font-size: 15px; color: #A0A0A0; margin-bottom: 8px; }
.purchase-amount .amount { font-size: 24px; font-weight: 700; color: #FFB000; margin-left: 4px; }
</style>
