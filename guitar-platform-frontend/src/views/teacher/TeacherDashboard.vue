<template>
  <div class="teacher-dashboard">
    <!-- Stats Row -->
    <div class="stats-grid">
      <div class="stat-card" v-for="s in statCards" :key="s.label">
        <div class="stat-icon-box" :style="{ background: s.bg }">
          <el-icon :size="22" :color="s.color">
            <component :is="s.icon" />
          </el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-label">{{ s.label }}</div>
          <div class="stat-value">{{ s.value }}</div>
          <div class="stat-sub" :style="{ color: s.trend > 0 ? '#52C41A' : '#FF4D4F' }" v-if="s.trend !== undefined">
            {{ s.trend > 0 ? '▲' : '▼' }} {{ Math.abs(s.trend) }}% 较上月
          </div>
        </div>
      </div>
    </div>

    <!-- Charts Row -->
    <div class="charts-row">
      <!-- Income Trend Chart -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>月收入趋势</h3>
          <div class="chart-actions">
            <el-select v-model="chartYear" style="width:90px;" size="small" @change="loadIncomeChart">
              <el-option label="2025年" :value="2025" />
              <el-option label="2026年" :value="2026" />
            </el-select>
          </div>
        </div>
        <div ref="incomeChartRef" class="echarts-box"></div>
      </div>

      <!-- Course Performance -->
      <div class="chart-card chart-card--sm">
        <div class="chart-header">
          <h3>课程分布</h3>
        </div>
        <div ref="courseChartRef" class="echarts-box"></div>
      </div>
    </div>

    <!-- Pending Grades + Recent Activity -->
    <div class="bottom-row">
      <!-- Pending Practices -->
      <div class="panel-card">
        <div class="panel-header">
          <h3>待批改练习</h3>
          <el-badge :value="pendingPractices.length" type="warning">
            <el-button
              size="small"
              class="btn-outline-gold"
              @click="$router.push('/teacher/practice')"
            >
              查看全部
            </el-button>
          </el-badge>
        </div>

        <div v-if="pendingPractices.length" class="practice-list">
          <div
            v-for="p in pendingPractices.slice(0, 5)"
            :key="p.id"
            class="practice-item"
            @click="$router.push('/teacher/practice')"
          >
            <video :src="p.videoUrl" class="practice-thumb" preload="metadata" />
            <div class="practice-info">
              <span class="p-student">{{ p.studentName }}</span>
              <span class="p-course">{{ p.courseName }}</span>
              <span class="p-time">{{ formatDate(p.createdAt) }}</span>
            </div>
            <el-tag type="warning" size="small" effect="plain">待批改</el-tag>
          </div>
        </div>
        <div v-else class="empty-panel">
          <el-icon size="36" color="#333"><CircleCheck /></el-icon>
          <p>暂无待批改练习</p>
        </div>
      </div>

      <!-- Quick Stats -->
      <div class="panel-card">
        <div class="panel-header">
          <h3>本月概览</h3>
        </div>
        <div class="monthly-stats">
          <div class="ms-item" v-for="m in monthlyStats" :key="m.label">
            <div class="ms-label">{{ m.label }}</div>
            <div class="ms-bar-wrap">
              <div class="ms-bar" :style="{ width: m.pct + '%', background: m.color }"></div>
            </div>
            <div class="ms-value" :style="{ color: m.color }">{{ m.value }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'
import { Money, User, VideoCamera, EditPen, CircleCheck } from '@element-plus/icons-vue'
import { teacherApi } from '@/api/teacher'

const router = useRouter()
const incomeChartRef = ref(null)
const courseChartRef = ref(null)
const chartYear      = ref(new Date().getFullYear())

let incomeChart = null
let courseChart = null

const dashStats       = ref({})
const pendingPractices= ref([])

const statCards = computed(() => [
  {
    label: '总收入',
    value: `¥${(dashStats.value.totalIncome || 0).toLocaleString()}`,
    icon: 'Money',
    bg: 'rgba(255,176,0,0.12)',
    color: '#FFB000',
    trend: dashStats.value.incomeTrend || 12
  },
  {
    label: '本月收入',
    value: `¥${(dashStats.value.monthIncome || 0).toLocaleString()}`,
    icon: 'TrendCharts',
    bg: 'rgba(82,196,26,0.12)',
    color: '#52C41A',
    trend: dashStats.value.monthTrend || 8
  },
  {
    label: '总学员数',
    value: dashStats.value.totalStudents || 0,
    icon: 'User',
    bg: 'rgba(24,144,255,0.12)',
    color: '#1890FF',
    trend: undefined
  },
  {
    label: '待批改',
    value: pendingPractices.value.length,
    icon: 'EditPen',
    bg: 'rgba(250,173,20,0.12)',
    color: '#FAAD14',
    trend: undefined
  }
])

const monthlyStats = computed(() => [
  { label: '新增学员', value: dashStats.value.newStudents || 0, pct: Math.min((dashStats.value.newStudents || 0) / 10 * 100, 100), color: '#FFB000' },
  { label: '视频观看', value: `${dashStats.value.totalViews || 0}次`, pct: Math.min((dashStats.value.totalViews || 0) / 200 * 100, 100), color: '#1890FF' },
  { label: '练习批改', value: dashStats.value.gradedCount || 0, pct: Math.min((dashStats.value.gradedCount || 0) / 20 * 100, 100), color: '#52C41A' },
  { label: '课程完成', value: `${dashStats.value.completionRate || 0}%`, pct: dashStats.value.completionRate || 0, color: '#9B59B6' }
])

const formatDate = (d) => d ? dayjs(d).format('MM-DD HH:mm') : ''

const initIncomeChart = (data) => {
  if (!incomeChartRef.value) return
  if (incomeChart) incomeChart.dispose()

  incomeChart = echarts.init(incomeChartRef.value, 'dark')
  const months = ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']
  const values = data || Array(12).fill(0).map(() => Math.floor(Math.random() * 5000 + 500))

  incomeChart.setOption({
    backgroundColor: 'transparent',
    grid: { top: 20, right: 20, bottom: 40, left: 60 },
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#1E1E1E',
      borderColor: '#333',
      textStyle: { color: '#E8E8E8' },
      formatter: (params) => `${params[0].name}<br/>收入：¥${params[0].value.toLocaleString()}`
    },
    xAxis: {
      type: 'category',
      data: months,
      axisLabel: { color: '#666', fontSize: 11 },
      axisLine:  { lineStyle: { color: '#333' } },
      axisTick:  { show: false }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#666', fontSize: 11, formatter: (v) => `¥${(v/1000).toFixed(0)}k` },
      splitLine: { lineStyle: { color: '#2A2A2A' } }
    },
    series: [{
      name: '月收入',
      type: 'line',
      data: values,
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: { color: '#FFB000', width: 2.5 },
      itemStyle: { color: '#FFB000', borderColor: '#121212', borderWidth: 2 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(255,176,0,0.3)' },
          { offset: 1, color: 'rgba(255,176,0,0)' }
        ])
      }
    }]
  })
}

const initCourseChart = (data) => {
  if (!courseChartRef.value) return
  if (courseChart) courseChart.dispose()

  courseChart = echarts.init(courseChartRef.value, 'dark')
  const categories = data || [
    { name: '民谣吉他', value: 35 },
    { name: '电吉他',   value: 25 },
    { name: '古典吉他', value: 20 },
    { name: '指弹',     value: 15 },
    { name: '乐理',     value: 5 }
  ]

  courseChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: '#1E1E1E',
      borderColor: '#333',
      textStyle: { color: '#E8E8E8' },
      formatter: '{b}: {c}人 ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      textStyle: { color: '#A0A0A0', fontSize: 12 }
    },
    series: [{
      type: 'pie',
      radius: ['50%', '75%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: false,
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: 'bold', color: '#FFB000' }
      },
      data: categories,
      color: ['#FFB000', '#1890FF', '#52C41A', '#9B59B6', '#E74C3C']
    }]
  })
}

const loadData = async () => {
  try {
    const [dashRes, practiceRes] = await Promise.allSettled([
      teacherApi.dashboardStats(),
      teacherApi.pendingPractices({ page: 0, size: 10 })
    ])

    if (dashRes.status === 'fulfilled') {
      dashStats.value = dashRes.value?.data || dashRes.value || {}
    }
    if (practiceRes.status === 'fulfilled') {
      const d = practiceRes.value?.data || practiceRes.value
      pendingPractices.value = d.content || d.list || d || []
    }
  } catch {}
}

const loadIncomeChart = async () => {
  try {
    const res = await teacherApi.incomeStats({ year: chartYear.value })
    const data = res.data || res
    initIncomeChart(data.monthly || null)
  } catch {
    initIncomeChart(null)
  }
}

const handleResize = () => {
  incomeChart?.resize()
  courseChart?.resize()
}

onMounted(async () => {
  await loadData()
  await nextTick()
  await loadIncomeChart()
  initCourseChart(dashStats.value.courseDistribution || null)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  incomeChart?.dispose()
  courseChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.teacher-dashboard { padding: 0; display: flex; flex-direction: column; gap: 20px; }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.stat-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  transition: all 0.25s;
  &:hover { border-color: rgba(255,176,0,0.3); transform: translateY(-2px); }
}
.stat-icon-box {
  width: 52px;
  height: 52px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-label { font-size: 12px; color: #666; margin-bottom: 4px; }
.stat-value { font-size: 22px; font-weight: 700; color: #E8E8E8; }
.stat-sub   { font-size: 12px; margin-top: 4px; }

/* Charts */
.charts-row {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 16px;
}
.chart-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  padding: 20px;
}
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  h3 { font-size: 15px; font-weight: 700; color: #E8E8E8; }
}
.echarts-box { height: 260px; }

/* Bottom */
.bottom-row {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 16px;
}
.panel-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  padding: 20px;
}
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  h3 { font-size: 15px; font-weight: 700; color: #E8E8E8; }
}

.practice-list { display: flex; flex-direction: column; gap: 10px; }
.practice-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  background: #252525;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
  &:hover { background: #2A2A2A; }
}
.practice-thumb {
  width: 72px;
  height: 44px;
  border-radius: 6px;
  object-fit: cover;
  background: #333;
  flex-shrink: 0;
}
.practice-info { flex: 1; display: flex; flex-direction: column; gap: 2px; }
.p-student { font-size: 13px; font-weight: 600; color: #E8E8E8; }
.p-course  { font-size: 12px; color: #A0A0A0; }
.p-time    { font-size: 11px; color: #555; }

.empty-panel { display: flex; flex-direction: column; align-items: center; gap: 8px; padding: 30px 0; color: #555; p { font-size: 13px; } }

/* Monthly Stats */
.monthly-stats { display: flex; flex-direction: column; gap: 14px; }
.ms-item { display: grid; grid-template-columns: 80px 1fr 60px; align-items: center; gap: 10px; }
.ms-label { font-size: 13px; color: #A0A0A0; }
.ms-bar-wrap { height: 6px; background: #2A2A2A; border-radius: 3px; overflow: hidden; }
.ms-bar { height: 100%; border-radius: 3px; transition: width 0.5s ease; }
.ms-value { font-size: 14px; font-weight: 700; text-align: right; }
</style>
