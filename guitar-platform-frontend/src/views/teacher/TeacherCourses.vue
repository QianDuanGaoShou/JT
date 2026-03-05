<template>
  <div class="teacher-courses">
    <!-- Header -->
    <div class="page-header">
      <div>
        <h1>我的课程</h1>
        <p>管理你创建的所有课程</p>
      </div>
      <el-button
        type="primary"
        class="btn-gradient"
        :icon="Plus"
        @click="$router.push('/teacher/course/create')"
      >
        创建新课程
      </el-button>
    </div>

    <!-- Filter -->
    <div class="filter-bar">
      <div class="status-tabs">
        <div
          v-for="t in statusTabs"
          :key="t.value"
          class="status-tab"
          :class="{ active: statusFilter === t.value }"
          @click="switchStatus(t.value)"
        >
          {{ t.label }}
        </div>
      </div>
      <el-input
        v-model="searchQuery"
        placeholder="搜索课程..."
        :prefix-icon="Search"
        clearable
        style="width:220px;"
        @input="handleSearch"
      />
    </div>

    <!-- Table -->
    <div class="table-card">
      <el-table
        v-loading="loading"
        :data="courses"
        style="width:100%;"
        row-class-name="course-row"
      >
        <el-table-column label="封面" width="100">
          <template #default="{ row }">
            <img :src="row.coverImage" :alt="row.title" class="course-thumb" />
          </template>
        </el-table-column>

        <el-table-column label="课程名称" min-width="180">
          <template #default="{ row }">
            <div class="name-cell">
              <span class="c-name">{{ row.title }}</span>
              <el-tag size="small" class="category-tag">{{ row.category }}</el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="价格" width="100" align="center">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.price }}</span>
          </template>
        </el-table-column>

        <el-table-column label="学员数" width="90" align="center">
          <template #default="{ row }">
            <span style="color:#C0C0C0;font-size:14px;">{{ row.studentCount || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column label="总收入" width="110" align="center">
          <template #default="{ row }">
            <span style="color:#FFB000;font-weight:600;">¥{{ row.totalIncome || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.status === 'PUBLISHED' ? 'success' : (row.status === 'PENDING' ? 'warning' : 'info')"
              effect="plain"
              size="small"
            >
              {{ statusLabels[row.status] || row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="创建时间" width="120">
          <template #default="{ row }">
            <span style="font-size:12px;color:#666;">{{ formatDate(row.createdAt) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-btns">
              <el-button
                size="small"
                class="btn-outline-gold"
                :icon="Edit"
                @click="editCourse(row)"
              >
                编辑
              </el-button>
              <el-button
                size="small"
                :type="row.status === 'PUBLISHED' ? 'warning' : 'success'"
                @click="togglePublish(row)"
                :loading="publishingId === row.id"
                style="border-radius:6px;"
              >
                {{ row.status === 'PUBLISHED' ? '下架' : '发布' }}
              </el-button>
              <el-popconfirm
                title="确定要删除这门课程吗？此操作不可恢复。"
                confirm-button-text="删除"
                cancel-button-text="取消"
                confirm-button-type="danger"
                @confirm="deleteCourse(row)"
              >
                <template #reference>
                  <el-button size="small" type="danger" :icon="Delete" style="border-radius:6px;" />
                </template>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-wrap" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          background
          @current-change="loadCourses"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Search, Edit, Delete } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { teacherApi } from '@/api/teacher'

const router = useRouter()
const loading     = ref(false)
const courses     = ref([])
const total       = ref(0)
const currentPage = ref(1)
const pageSize    = ref(10)
const statusFilter= ref('')
const searchQuery = ref('')
const publishingId= ref(null)
let searchTimer   = null

const statusLabels = {
  DRAFT:     '草稿',
  PENDING:   '审核中',
  PUBLISHED: '已发布',
  OFFLINE:   '已下架'
}

const statusTabs = [
  { label: '全部',   value: '' },
  { label: '已发布', value: 'PUBLISHED' },
  { label: '草稿',   value: 'DRAFT' },
  { label: '审核中', value: 'PENDING' }
]

const formatDate = (d) => d ? dayjs(d).format('YYYY-MM-DD') : '--'

const switchStatus = (v) => {
  statusFilter.value = v
  loadCourses(1)
}

const handleSearch = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => loadCourses(1), 400)
}

const editCourse = (row) => router.push(`/teacher/course/${row.id}/edit`)

const togglePublish = async (row) => {
  publishingId.value = row.id
  try {
    if (row.status === 'PUBLISHED') {
      await teacherApi.unpublishCourse(row.id)
      row.status = 'OFFLINE'
      ElMessage.success('课程已下架')
    } else {
      await teacherApi.publishCourse(row.id)
      row.status = 'PUBLISHED'
      ElMessage.success('课程已发布！')
    }
  } catch (err) {
    ElMessage.error(err.message || '操作失败')
  } finally {
    publishingId.value = null
  }
}

const deleteCourse = async (row) => {
  try {
    await teacherApi.deleteCourse(row.id)
    ElMessage.success('课程已删除')
    loadCourses()
  } catch (err) {
    ElMessage.error(err.message || '删除失败')
  }
}

const loadCourses = async (page = currentPage.value) => {
  loading.value = true
  currentPage.value = page
  try {
    const res = await teacherApi.myCourses({
      page: page - 1,
      size: pageSize.value,
      status:  statusFilter.value || undefined,
      keyword: searchQuery.value  || undefined
    })
    const data = res.data || res
    courses.value = data.content || data.list || data || []
    total.value   = data.totalElements || data.total || courses.value.length
  } catch (err) {
    ElMessage.error('加载课程失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => loadCourses(1))
</script>

<style scoped>
.teacher-courses { display: flex; flex-direction: column; gap: 20px; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  h1 { font-size: 22px; font-weight: 700; color: #E8E8E8; margin-bottom: 4px; }
  p  { font-size: 13px; color: #666; }
}

.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}
.status-tabs { display: flex; gap: 6px; }
.status-tab {
  padding: 6px 14px;
  border-radius: 100px;
  font-size: 13px;
  color: #A0A0A0;
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  cursor: pointer;
  transition: all 0.2s;
  &:hover { border-color: rgba(255,176,0,0.4); }
  &.active { background: rgba(255,176,0,0.12); border-color: #FFB000; color: #FFB000; font-weight: 600; }
}

.table-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  overflow: hidden;
}

.course-thumb {
  width: 80px;
  height: 52px;
  border-radius: 6px;
  object-fit: cover;
  background: #2A2A2A;
}

.name-cell { display: flex; flex-direction: column; gap: 4px; }
.c-name { font-size: 14px; font-weight: 600; color: #E8E8E8; }
.category-tag { background: rgba(255,176,0,0.1) !important; border-color: rgba(255,176,0,0.3) !important; color: #FFB000 !important; }

.price-text { font-size: 14px; font-weight: 600; color: #FFB000; }

.action-btns { display: flex; gap: 6px; align-items: center; }

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 16px;
  border-top: 1px solid #2A2A2A;
}
</style>
