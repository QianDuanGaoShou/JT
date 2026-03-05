<template>
  <div class="course-list-page">
    <div class="page-container">
      <!-- Page Header -->
      <div class="page-top">
        <div>
          <h1 class="page-title">课程广场</h1>
          <p class="page-desc">探索专业吉他课程，找到适合你的学习路径</p>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-num">{{ totalCourses }}</span>
            <span class="stat-label">门课程</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-num">100+</span>
            <span class="stat-label">专业教师</span>
          </div>
        </div>
      </div>

      <!-- Search & Filters -->
      <div class="filter-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索课程名称、教师..."
          :prefix-icon="Search"
          clearable
          size="large"
          class="search-input"
          @input="handleSearch"
          @clear="handleSearch"
        />
        <div class="filter-tabs">
          <div
            v-for="cat in categories"
            :key="cat.value"
            class="filter-tab"
            :class="{ active: selectedCategory === cat.value }"
            @click="selectCategory(cat.value)"
          >
            {{ cat.label }}
          </div>
        </div>
        <el-select
          v-model="sortBy"
          placeholder="排序方式"
          size="large"
          style="width:140px;"
          @change="loadCourses(1)"
        >
          <el-option label="最新上架" value="newest" />
          <el-option label="最多学员" value="popular" />
          <el-option label="价格从低" value="price_asc" />
          <el-option label="价格从高" value="price_desc" />
        </el-select>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-grid">
        <el-skeleton
          v-for="i in 8"
          :key="i"
          :rows="4"
          animated
          class="skeleton-card"
        />
      </div>

      <!-- Course Grid -->
      <template v-else>
        <div v-if="courses.length" class="course-grid">
          <CourseCard
            v-for="course in courses"
            :key="course.id"
            :course="course"
            @click="goToDetail(course.id)"
          />
        </div>

        <!-- Empty State -->
        <div v-else class="empty-state">
          <el-icon size="64" color="#333"><Search /></el-icon>
          <h3>暂无相关课程</h3>
          <p>试试其他关键词或分类</p>
          <el-button @click="resetFilters" class="btn-outline-gold">重置筛选</el-button>
        </div>

        <!-- Pagination -->
        <div v-if="total > pageSize" class="pagination-wrap">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next, jumper"
            background
            @current-change="loadCourses"
          />
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { courseApi } from '@/api/course'
import CourseCard from '@/components/CourseCard.vue'

const router = useRouter()

const loading      = ref(false)
const courses      = ref([])
const total        = ref(0)
const currentPage  = ref(1)
const pageSize     = ref(12)
const searchQuery  = ref('')
const selectedCategory = ref('')
const sortBy       = ref('newest')
let   searchTimer  = null

const totalCourses = computed(() => total.value || 0)

const categories = [
  { label: '全部', value: '' },
  { label: '民谣吉他', value: '民谣' },
  { label: '古典吉他', value: '古典' },
  { label: '电吉他', value: '电吉他' },
  { label: '指弹', value: '指弹' },
  { label: '乐理基础', value: '乐理' }
]

const loadCourses = async (page = currentPage.value) => {
  loading.value = true
  currentPage.value = page
  try {
    const res = await courseApi.list({
      page: page - 1,
      size: pageSize.value,
      keyword: searchQuery.value || undefined,
      category: selectedCategory.value || undefined,
      sort: sortBy.value
    })
    const data = res.data || res
    courses.value = data.content || data.list || data || []
    total.value   = data.totalElements || data.total || courses.value.length
  } catch (err) {
    ElMessage.error('加载课程失败')
    console.error(err)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => loadCourses(1), 400)
}

const selectCategory = (cat) => {
  selectedCategory.value = cat
  loadCourses(1)
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedCategory.value = ''
  sortBy.value = 'newest'
  loadCourses(1)
}

const goToDetail = (id) => router.push(`/courses/${id}`)

onMounted(() => loadCourses(1))
</script>

<style scoped>
.course-list-page {
  min-height: calc(100vh - 64px);
  background: #121212;
  padding-bottom: 60px;
}

.page-container { max-width: 1280px; margin: 0 auto; padding: 32px 24px; }

.page-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 28px;
}

.page-title {
  font-size: 28px;
  font-weight: 800;
  color: #E8E8E8;
  margin-bottom: 4px;
}
.page-desc { font-size: 14px; color: #666; }

.header-stats {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #1E1E1E;
  border: 1px solid #2E2E2E;
  border-radius: 10px;
  padding: 12px 20px;
}
.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}
.stat-num { font-size: 20px; font-weight: 700; color: #FFB000; }
.stat-label { font-size: 12px; color: #666; }
.stat-divider { width: 1px; height: 32px; background: #333; }

/* Filter Bar */
.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.search-input { flex: 1; min-width: 240px; max-width: 360px; }

.filter-tabs {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  flex: 1;
}

.filter-tab {
  padding: 7px 14px;
  border-radius: 100px;
  font-size: 13px;
  color: #A0A0A0;
  background: #1E1E1E;
  border: 1px solid #2E2E2E;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;

  &:hover { border-color: rgba(255,176,0,0.4); color: #E8E8E8; }
  &.active {
    background: rgba(255,176,0,0.12);
    border-color: #FFB000;
    color: #FFB000;
    font-weight: 600;
  }
}

/* Course Grid */
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

/* Skeleton */
.loading-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}
.skeleton-card {
  background: #1E1E1E;
  border-radius: 12px;
  padding: 12px;
  height: 320px;
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

/* Pagination */
.pagination-wrap {
  display: flex;
  justify-content: center;
  padding-top: 8px;
}
</style>
