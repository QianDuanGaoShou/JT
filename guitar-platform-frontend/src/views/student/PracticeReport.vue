<template>
  <div class="practice-report-page" v-loading="loading">
    <div v-if="report" class="page-container">
      <!-- Breadcrumb -->
      <el-breadcrumb separator="/" style="margin-bottom:20px;">
        <el-breadcrumb-item :to="{ path: '/practice' }">练习记录</el-breadcrumb-item>
        <el-breadcrumb-item>练习详情</el-breadcrumb-item>
      </el-breadcrumb>

      <div class="report-layout">
        <!-- Left: Video + Info -->
        <div class="left-col">
          <!-- Video -->
          <div class="video-wrap">
            <VideoPlayer
              v-if="report.videoUrl"
              :src="report.videoUrl"
              :initial-position="0"
              class="practice-player"
            />
          </div>

          <div class="video-meta-card">
            <h2 class="meta-title">练习详情</h2>
            <div class="meta-row">
              <span class="meta-key">课程</span>
              <span class="meta-val">{{ report.courseName || '--' }}</span>
            </div>
            <div class="meta-row" v-if="report.chapterName">
              <span class="meta-key">章节</span>
              <span class="meta-val">{{ report.chapterName }}</span>
            </div>
            <div class="meta-row">
              <span class="meta-key">提交时间</span>
              <span class="meta-val">{{ formatDate(report.createdAt) }}</span>
            </div>
            <div class="meta-row" v-if="report.gradedAt">
              <span class="meta-key">批改时间</span>
              <span class="meta-val">{{ formatDate(report.gradedAt) }}</span>
            </div>
            <div class="meta-row" v-if="report.note">
              <span class="meta-key">练习说明</span>
              <span class="meta-val">{{ report.note }}</span>
            </div>
          </div>
        </div>

        <!-- Right: Score + Feedback -->
        <div class="right-col">
          <!-- Score Card -->
          <div class="score-card" :class="scoreClass">
            <div class="score-display">
              <div class="score-label">综合评分</div>
              <div v-if="report.status === 'GRADED'" class="score-number">
                <span class="score-main" :style="{ color: scoreColor(report.score) }">
                  {{ report.score }}
                </span>
                <span class="score-total">/100</span>
              </div>
              <div v-else class="score-pending">
                <el-icon size="48" color="#666"><Clock /></el-icon>
                <p>等待教师批改</p>
              </div>
            </div>

            <!-- Score ring visualization -->
            <div v-if="report.status === 'GRADED'" class="score-ring-wrap">
              <el-progress
                type="dashboard"
                :percentage="report.score"
                :color="scoreColor(report.score)"
                :width="140"
                :stroke-width="10"
              >
                <template #default>
                  <div class="ring-inner">
                    <span class="ring-score" :style="{ color: scoreColor(report.score) }">
                      {{ report.score }}
                    </span>
                    <span class="ring-label">分</span>
                  </div>
                </template>
              </el-progress>

              <!-- Grade Tag -->
              <div class="grade-tag" :style="{ color: scoreColor(report.score), borderColor: scoreColor(report.score), background: `${scoreColor(report.score)}15` }">
                {{ gradeText(report.score) }}
              </div>
            </div>
          </div>

          <!-- Score Breakdown -->
          <div v-if="report.scoreBreakdown" class="breakdown-card">
            <h3 class="section-title">
              <el-icon color="#FFB000"><DataAnalysis /></el-icon>
              评分细项
            </h3>
            <div class="breakdown-items">
              <div
                v-for="(val, key) in report.scoreBreakdown"
                :key="key"
                class="breakdown-item"
              >
                <div class="breakdown-header">
                  <span class="b-label">{{ breakdownLabels[key] || key }}</span>
                  <span class="b-value" :style="{ color: scoreColor(val) }">{{ val }}</span>
                </div>
                <el-progress
                  :percentage="val"
                  :color="scoreColor(val)"
                  :show-text="false"
                  :stroke-width="6"
                />
              </div>
            </div>
          </div>

          <!-- Teacher Feedback -->
          <div class="feedback-card">
            <h3 class="section-title">
              <el-icon color="#FFB000"><ChatLineRound /></el-icon>
              教师评语
            </h3>

            <div v-if="report.teacherFeedback" class="feedback-content">
              <div class="teacher-info">
                <el-avatar :size="36" :src="report.teacherAvatar">
                  {{ report.teacherName?.[0] }}
                </el-avatar>
                <div>
                  <div class="teacher-name">{{ report.teacherName }}</div>
                  <div class="grade-time">{{ formatDate(report.gradedAt) }}</div>
                </div>
              </div>
              <div class="feedback-text">
                {{ report.teacherFeedback }}
              </div>
            </div>

            <div v-else class="no-feedback">
              <el-icon size="32" color="#333"><EditPen /></el-icon>
              <p>{{ report.status === 'GRADED' ? '教师未留评语' : '等待教师评语...' }}</p>
            </div>
          </div>

          <!-- Actions -->
          <div class="action-buttons">
            <el-button
              class="btn-outline-gold"
              style="flex:1;"
              @click="$router.push('/practice')"
            >
              返回列表
            </el-button>
            <el-button
              type="primary"
              class="btn-gradient"
              style="flex:1;"
              @click="$router.push(`/courses/${report.courseId}`)"
            >
              继续学习
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Clock, DataAnalysis, ChatLineRound, EditPen } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { practiceApi } from '@/api/practice'
import VideoPlayer from '@/components/VideoPlayer.vue'

const route   = useRoute()
const loading = ref(true)
const report  = ref(null)

const breakdownLabels = {
  rhythm: '节奏准确度',
  pitch:  '音准',
  technique: '演奏技巧',
  expression: '音乐表现力',
  completeness: '完整度'
}

const scoreColor = (score) => {
  if (!score && score !== 0) return '#555'
  if (score >= 90) return '#52C41A'
  if (score >= 75) return '#FFB000'
  if (score >= 60) return '#FAAD14'
  return '#FF4D4F'
}

const gradeText = (score) => {
  if (score >= 90) return '优秀'
  if (score >= 75) return '良好'
  if (score >= 60) return '及格'
  return '需要加油'
}

const scoreClass = computed(() => {
  if (!report.value?.score) return ''
  const s = report.value.score
  if (s >= 90) return 'score-excellent'
  if (s >= 75) return 'score-good'
  if (s >= 60) return 'score-pass'
  return 'score-fail'
})

const formatDate = (d) => d ? dayjs(d).format('YYYY-MM-DD HH:mm') : '--'

onMounted(async () => {
  try {
    const res = await practiceApi.report(route.params.id)
    report.value = res.data || res
  } catch (err) {
    ElMessage.error('加载练习报告失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.practice-report-page { min-height: calc(100vh - 64px); background: #121212; padding: 32px 24px; }
.page-container { max-width: 1100px; margin: 0 auto; }

.report-layout {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 24px;
  align-items: flex-start;
}

.left-col { display: flex; flex-direction: column; gap: 16px; }

.video-wrap {
  border-radius: 12px;
  overflow: hidden;
  background: #000;
  aspect-ratio: 16/9;
}
.practice-player { width: 100%; height: 100%; }

.video-meta-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  padding: 20px;
}
.meta-title { font-size: 16px; font-weight: 700; color: #E8E8E8; margin-bottom: 16px; }
.meta-row {
  display: flex;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid #252525;
  &:last-child { border-bottom: none; }
}
.meta-key { font-size: 13px; color: #666; width: 70px; flex-shrink: 0; }
.meta-val { font-size: 13px; color: #C0C0C0; flex: 1; }

.right-col { display: flex; flex-direction: column; gap: 16px; position: sticky; top: 80px; }

/* Score Card */
.score-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  transition: border-color 0.3s;
}
.score-card.score-excellent { border-color: rgba(82,196,26,0.3); }
.score-card.score-good      { border-color: rgba(255,176,0,0.3); }
.score-card.score-pass      { border-color: rgba(250,173,20,0.3); }
.score-card.score-fail      { border-color: rgba(255,77,79,0.3); }

.score-label { font-size: 13px; color: #666; margin-bottom: 8px; }
.score-number { display: flex; align-items: baseline; gap: 4px; }
.score-main { font-size: 56px; font-weight: 900; line-height: 1; }
.score-total { font-size: 16px; color: #555; }
.score-pending { display: flex; flex-direction: column; align-items: flex-start; gap: 6px; }
.score-pending p { font-size: 13px; color: #555; }

.score-ring-wrap { display: flex; flex-direction: column; align-items: center; gap: 10px; }
.ring-inner { display: flex; flex-direction: column; align-items: center; }
.ring-score { font-size: 24px; font-weight: 800; }
.ring-label { font-size: 12px; color: #666; }
.grade-tag {
  padding: 4px 12px;
  border-radius: 100px;
  border: 1px solid;
  font-size: 13px;
  font-weight: 600;
}

/* Breakdown */
.breakdown-card, .feedback-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  padding: 20px;
}
.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 700;
  color: #E8E8E8;
  margin-bottom: 16px;
}
.breakdown-items { display: flex; flex-direction: column; gap: 12px; }
.breakdown-item {}
.breakdown-header { display: flex; justify-content: space-between; margin-bottom: 6px; }
.b-label { font-size: 13px; color: #A0A0A0; }
.b-value { font-size: 14px; font-weight: 700; }

/* Feedback */
.teacher-info { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.teacher-name  { font-size: 14px; font-weight: 600; color: #E8E8E8; }
.grade-time    { font-size: 12px; color: #555; }
.feedback-text {
  font-size: 14px;
  color: #C0C0C0;
  line-height: 1.8;
  background: #252525;
  border-left: 3px solid #FFB000;
  border-radius: 0 8px 8px 0;
  padding: 12px 16px;
}
.no-feedback {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 0;
  color: #555;
  p { font-size: 13px; }
}

.action-buttons {
  display: flex;
  gap: 12px;
}
</style>
