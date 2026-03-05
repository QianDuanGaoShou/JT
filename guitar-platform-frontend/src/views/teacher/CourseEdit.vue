<template>
  <div class="course-edit-page">
    <!-- Header -->
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/teacher/courses' }">我的课程</el-breadcrumb-item>
        <el-breadcrumb-item>{{ isEdit ? '编辑课程' : '创建课程' }}</el-breadcrumb-item>
      </el-breadcrumb>
      <div class="header-actions">
        <el-button @click="saveDraft" :loading="saving === 'draft'" style="background:#2A2A2A;border-color:#333;color:#A0A0A0;">
          保存草稿
        </el-button>
        <el-button type="primary" class="btn-gradient" @click="publishCourse" :loading="saving === 'publish'">
          {{ isEdit ? '更新发布' : '发布课程' }}
        </el-button>
      </div>
    </div>

    <div class="edit-layout">
      <!-- Left: Basic Info -->
      <div class="left-col">
        <div class="form-card">
          <h3 class="form-section-title">基本信息</h3>
          <el-form
            ref="formRef"
            :model="courseForm"
            :rules="rules"
            label-position="top"
          >
            <el-form-item label="课程标题" prop="title">
              <el-input
                v-model="courseForm.title"
                placeholder="输入吸引人的课程标题"
                maxlength="60"
                show-word-limit
              />
            </el-form-item>

            <div class="form-row">
              <el-form-item label="课程分类" prop="category">
                <el-select v-model="courseForm.category" placeholder="选择分类" style="width:100%;">
                  <el-option label="民谣吉他" value="民谣" />
                  <el-option label="古典吉他" value="古典" />
                  <el-option label="电吉他"   value="电吉他" />
                  <el-option label="指弹"     value="指弹" />
                  <el-option label="乐理基础" value="乐理" />
                </el-select>
              </el-form-item>

              <el-form-item label="课程价格 (¥)" prop="price">
                <el-input-number
                  v-model="courseForm.price"
                  :min="0"
                  :max="9999"
                  :precision="2"
                  style="width:100%;"
                  placeholder="0 = 免费"
                />
              </el-form-item>
            </div>

            <el-form-item label="课程简介" prop="description">
              <el-input
                v-model="courseForm.description"
                type="textarea"
                :rows="5"
                placeholder="描述课程内容、目标学员、学完后能掌握什么..."
                maxlength="1000"
                show-word-limit
              />
            </el-form-item>

            <!-- Cover Image -->
            <el-form-item label="课程封面">
              <div class="cover-upload-area">
                <div v-if="courseForm.coverImage" class="cover-preview">
                  <img :src="courseForm.coverImage" alt="封面" class="cover-img" />
                  <div class="cover-overlay">
                    <el-button size="small" class="btn-outline-gold" @click="courseForm.coverImage = ''">
                      更换图片
                    </el-button>
                  </div>
                </div>
                <el-upload
                  v-else
                  :auto-upload="false"
                  accept="image/*"
                  :limit="1"
                  :on-change="handleCoverChange"
                  drag
                  class="cover-uploader"
                >
                  <div class="cover-upload-inner">
                    <el-icon size="40" color="#FFB000"><Picture /></el-icon>
                    <p>上传课程封面</p>
                    <p class="upload-hint">建议 16:9，JPG/PNG，最大 5MB</p>
                  </div>
                </el-upload>
              </div>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <!-- Right: Chapters -->
      <div class="right-col">
        <div class="chapters-card">
          <div class="chapters-header">
            <h3 class="form-section-title" style="margin:0;">课程章节</h3>
            <el-button
              size="small"
              type="primary"
              class="btn-gradient"
              :icon="Plus"
              @click="addChapter"
            >
              添加章节
            </el-button>
          </div>

          <!-- Chapters List -->
          <div class="chapters-container">
            <div
              v-for="(chapter, cIdx) in courseForm.chapters"
              :key="chapter._key"
              class="chapter-block"
            >
              <div class="chapter-row">
                <el-icon class="drag-icon" color="#444"><Rank /></el-icon>
                <el-input
                  v-model="chapter.title"
                  placeholder="章节名称"
                  size="small"
                  style="flex:1;"
                />
                <el-button size="small" :icon="Plus" @click="addVideo(cIdx)" class="btn-outline-gold" style="padding:6px 10px;" />
                <el-popconfirm title="删除此章节及其所有视频？" @confirm="removeChapter(cIdx)">
                  <template #reference>
                    <el-button size="small" type="danger" :icon="Delete" style="padding:6px 10px;" />
                  </template>
                </el-popconfirm>
              </div>

              <!-- Videos -->
              <div class="videos-container">
                <div
                  v-for="(video, vIdx) in chapter.videos"
                  :key="video._key"
                  class="video-block"
                >
                  <el-icon class="drag-icon" color="#333" size="14"><Rank /></el-icon>
                  <div class="video-form">
                    <el-input
                      v-model="video.title"
                      placeholder="视频标题"
                      size="small"
                      style="flex:1;"
                    />
                    <!-- Video Upload -->
                    <div v-if="!video.videoUrl" class="video-upload-area">
                      <el-upload
                        :auto-upload="false"
                        accept="video/*"
                        :limit="1"
                        :on-change="(file) => handleVideoFile(cIdx, vIdx, file)"
                        class="video-uploader"
                      >
                        <el-button size="small" style="background:#2A2A2A;border-color:#333;color:#A0A0A0;">
                          <el-icon><Upload /></el-icon>
                          选择视频
                        </el-button>
                      </el-upload>
                      <el-progress
                        v-if="video.uploadProgress > 0 && video.uploadProgress < 100"
                        :percentage="video.uploadProgress"
                        :color="'#FFB000'"
                        size="small"
                        style="flex:1;margin-left:8px;"
                        :show-text="false"
                      />
                      <span v-if="video.uploadProgress === 100" style="color:#52C41A;font-size:12px;">已上传</span>
                    </div>
                    <div v-else class="video-uploaded">
                      <el-icon color="#52C41A"><CircleCheck /></el-icon>
                      <span class="video-url-text">{{ video.videoUrl.split('/').pop() }}</span>
                      <el-button size="small" text @click="video.videoUrl = ''" style="color:#FF4D4F;">更换</el-button>
                    </div>
                  </div>
                  <el-popconfirm title="删除此视频？" @confirm="removeVideo(cIdx, vIdx)">
                    <template #reference>
                      <el-button size="small" type="danger" :icon="Close" circle style="flex-shrink:0;" />
                    </template>
                  </el-popconfirm>
                </div>

                <div v-if="!chapter.videos.length" class="empty-videos">
                  <el-icon size="20" color="#444"><VideoPlay /></el-icon>
                  <span>点击上方按钮添加视频</span>
                </div>
              </div>
            </div>

            <div v-if="!courseForm.chapters.length" class="empty-chapters">
              <el-icon size="48" color="#333"><FolderOpened /></el-icon>
              <p>还没有章节</p>
              <el-button type="primary" class="btn-gradient" size="small" :icon="Plus" @click="addChapter">
                添加第一个章节
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Plus, Delete, Upload, Picture, Rank, CircleCheck, VideoPlay,
  FolderOpened, Close
} from '@element-plus/icons-vue'
import { teacherApi } from '@/api/teacher'
import { uploadApi } from '@/api/upload'

const route  = useRoute()
const router = useRouter()

const isEdit    = computed(() => !!route.params.id)
const formRef   = ref(null)
const saving    = ref('')
let   keyCounter= 0
const nextKey   = () => ++keyCounter

const courseForm = reactive({
  title:       '',
  category:    '',
  price:       0,
  description: '',
  coverImage:  '',
  chapters:    []
})

const rules = {
  title:       [{ required: true, message: '请输入课程标题', trigger: 'blur' }],
  category:    [{ required: true, message: '请选择课程分类', trigger: 'change' }],
  description: [{ required: true, message: '请输入课程简介', trigger: 'blur' }]
}

const addChapter = () => {
  courseForm.chapters.push({
    _key:      nextKey(),
    id:        null,
    title:     `第 ${courseForm.chapters.length + 1} 章`,
    sortOrder: courseForm.chapters.length,
    videos:    []
  })
}

const removeChapter = (cIdx) => {
  courseForm.chapters.splice(cIdx, 1)
}

const addVideo = (cIdx) => {
  courseForm.chapters[cIdx].videos.push({
    _key:           nextKey(),
    id:             null,
    title:          '',
    videoUrl:       '',
    duration:       0,
    sortOrder:      courseForm.chapters[cIdx].videos.length,
    uploadProgress: 0
  })
}

const removeVideo = (cIdx, vIdx) => {
  courseForm.chapters[cIdx].videos.splice(vIdx, 1)
}

const handleCoverChange = async (file) => {
  const fd = new FormData()
  fd.append('file', file.raw)
  try {
    const res = await uploadApi.image(fd)
    courseForm.coverImage = (res.data || res).url
    ElMessage.success('封面上传成功')
  } catch {
    ElMessage.error('封面上传失败')
  }
}

const handleVideoFile = async (cIdx, vIdx, file) => {
  const video = courseForm.chapters[cIdx].videos[vIdx]
  const fd    = new FormData()
  fd.append('file', file.raw)

  video.uploadProgress = 1
  try {
    const res = await uploadApi.video(fd, (p) => { video.uploadProgress = p })
    video.videoUrl = (res.data || res).url
    video.duration = (res.data || res).duration || 0
    video.uploadProgress = 100
    ElMessage.success('视频上传成功')
  } catch {
    video.uploadProgress = 0
    ElMessage.error('视频上传失败')
  }
}

const buildPayload = (status) => ({
  title:       courseForm.title,
  category:    courseForm.category,
  price:       courseForm.price,
  description: courseForm.description,
  coverImage:  courseForm.coverImage,
  status,
  chapters: courseForm.chapters.map((ch, cIdx) => ({
    id:        ch.id,
    title:     ch.title,
    sortOrder: cIdx,
    videos:    ch.videos.map((v, vIdx) => ({
      id:        v.id,
      title:     v.title,
      videoUrl:  v.videoUrl,
      duration:  v.duration,
      sortOrder: vIdx
    }))
  }))
})

const saveDraft = async () => {
  saving.value = 'draft'
  try {
    const payload = buildPayload('DRAFT')
    if (isEdit.value) {
      await teacherApi.updateCourse(route.params.id, payload)
    } else {
      await teacherApi.createCourse(payload)
    }
    ElMessage.success('草稿已保存')
    router.push('/teacher/courses')
  } catch (err) {
    ElMessage.error(err.message || '保存失败')
  } finally {
    saving.value = ''
  }
}

const publishCourse = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (!courseForm.chapters.length) {
    ElMessage.warning('请至少添加一个章节')
    return
  }

  saving.value = 'publish'
  try {
    const payload = buildPayload('PUBLISHED')
    if (isEdit.value) {
      await teacherApi.updateCourse(route.params.id, payload)
    } else {
      await teacherApi.createCourse(payload)
    }
    ElMessage.success(isEdit.value ? '课程已更新！' : '课程已提交，等待审核！')
    router.push('/teacher/courses')
  } catch (err) {
    ElMessage.error(err.message || '发布失败')
  } finally {
    saving.value = ''
  }
}

const loadCourse = async () => {
  if (!isEdit.value) return
  try {
    const res = await teacherApi.myCourses({ id: route.params.id })
    // Also try direct detail
    const { default: request } = await import('@/api/axios')
    const detail = await request.get(`/teacher/courses/${route.params.id}`)
    const c = detail.data || detail
    courseForm.title       = c.title
    courseForm.category    = c.category
    courseForm.price       = c.price
    courseForm.description = c.description
    courseForm.coverImage  = c.coverImage
    courseForm.chapters    = (c.chapters || []).map(ch => ({
      _key:      nextKey(),
      id:        ch.id,
      title:     ch.title,
      sortOrder: ch.sortOrder,
      videos:    (ch.videos || []).map(v => ({
        _key:           nextKey(),
        id:             v.id,
        title:          v.title,
        videoUrl:       v.videoUrl,
        duration:       v.duration,
        sortOrder:      v.sortOrder,
        uploadProgress: 0
      }))
    }))
  } catch (err) {
    ElMessage.error('加载课程数据失败')
  }
}

onMounted(loadCourse)
</script>

<style scoped>
.course-edit-page { display: flex; flex-direction: column; gap: 20px; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-actions { display: flex; gap: 10px; }

.edit-layout {
  display: grid;
  grid-template-columns: 1fr 420px;
  gap: 20px;
  align-items: flex-start;
}

.left-col, .right-col { display: flex; flex-direction: column; gap: 20px; }

.form-card, .chapters-card {
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 12px;
  padding: 24px;
}

.form-section-title {
  font-size: 15px;
  font-weight: 700;
  color: #E8E8E8;
  margin-bottom: 20px;
}

.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }

:deep(.el-form-item__label) { color: #C0C0C0 !important; font-size: 13px; }
:deep(.el-input__inner) { color: #E8E8E8 !important; }
:deep(.el-textarea__inner) { background: #2A2A2A !important; color: #E8E8E8 !important; border-color: #333 !important; }
:deep(.el-input-number .el-input__inner) { text-align: left !important; }

/* Cover Upload */
.cover-upload-area { width: 100%; }
.cover-preview {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  aspect-ratio: 16/9;
}
.cover-img { width: 100%; height: 100%; object-fit: cover; }
.cover-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
}
.cover-preview:hover .cover-overlay { opacity: 1; }

:deep(.cover-uploader .el-upload-dragger) {
  background: #252525 !important;
  border-color: #333 !important;
  border-radius: 8px !important;
  &:hover { border-color: #FFB000 !important; }
}
.cover-upload-inner { display: flex; flex-direction: column; align-items: center; gap: 8px; padding: 20px; }
.cover-upload-inner p { color: #A0A0A0; font-size: 13px; }
.upload-hint { font-size: 11px !important; color: #555 !important; }

/* Chapters */
.chapters-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.chapters-container { display: flex; flex-direction: column; gap: 12px; }

.chapter-block {
  background: #252525;
  border: 1px solid #2E2E2E;
  border-radius: 10px;
  overflow: hidden;
}
.chapter-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  background: #2A2A2A;
  border-bottom: 1px solid #333;
}
.drag-icon { cursor: grab; flex-shrink: 0; }

.videos-container { padding: 8px 12px; display: flex; flex-direction: column; gap: 8px; }
.video-block {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  background: #1E1E1E;
  border: 1px solid #2A2A2A;
  border-radius: 6px;
}
.video-form { flex: 1; display: flex; flex-direction: column; gap: 6px; }
.video-upload-area { display: flex; align-items: center; gap: 8px; }
.video-uploaded { display: flex; align-items: center; gap: 6px; font-size: 12px; color: #A0A0A0; }
.video-url-text { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 140px; }

:deep(.video-uploader .el-upload) { display: inline-block; }

.empty-videos {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  color: #444;
  font-size: 13px;
}

.empty-chapters {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 40px 0;
  color: #555;
  p { font-size: 14px; }
}
</style>
