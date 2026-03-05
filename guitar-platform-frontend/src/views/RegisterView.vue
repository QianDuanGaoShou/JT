<template>
  <div class="register-page">
    <div class="bg-decoration">
      <div class="bg-circle c1"></div>
      <div class="bg-circle c2"></div>
    </div>

    <div class="register-container fade-in">
      <div class="brand">
        <div class="brand-icon">🎸</div>
        <h1 class="brand-title">加入吉他教学平台</h1>
        <p class="brand-subtitle">开启你的音乐学习之旅</p>
      </div>

      <div class="register-card">
        <div class="card-header">
          <h2>创建账号</h2>
          <p>填写以下信息完成注册</p>
        </div>

        <!-- Role Selector -->
        <div class="role-selector">
          <div
            class="role-option"
            :class="{ active: form.role === 'STUDENT' }"
            @click="form.role = 'STUDENT'"
          >
            <span class="role-icon">🎓</span>
            <span class="role-label">学员</span>
            <span class="role-desc">我想学习吉他</span>
          </div>
          <div
            class="role-option"
            :class="{ active: form.role === 'TEACHER' }"
            @click="form.role = 'TEACHER'"
          >
            <span class="role-icon">🏫</span>
            <span class="role-label">教师</span>
            <span class="role-desc">我想教授课程</span>
          </div>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          @submit.prevent="handleRegister"
        >
          <div class="form-row">
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="form.username"
                placeholder="3-30个字符"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input
                v-model="form.nickname"
                placeholder="显示名称"
                :prefix-icon="Avatar"
                clearable
              />
            </el-form-item>
          </div>

          <el-form-item label="邮箱" prop="email">
            <el-input
              v-model="form.email"
              placeholder="your@email.com"
              :prefix-icon="Message"
              type="email"
              clearable
            />
          </el-form-item>

          <div class="form-row">
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="至少6个字符"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="form.confirmPassword"
                type="password"
                placeholder="再次输入密码"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
          </div>

          <!-- Teacher Qualification Upload -->
          <Transition name="slide-down">
            <el-form-item
              v-if="form.role === 'TEACHER'"
              label="教师资质证明"
              prop="qualificationFile"
            >
              <div class="upload-area">
                <el-upload
                  ref="uploadRef"
                  :auto-upload="false"
                  :limit="1"
                  accept=".pdf,.jpg,.jpeg,.png"
                  :on-change="handleFileChange"
                  :file-list="fileList"
                  drag
                >
                  <div class="upload-content">
                    <el-icon class="upload-icon" size="40" color="#FFB000"><Upload /></el-icon>
                    <div class="upload-text">
                      <p>拖拽文件到此处或 <span class="upload-link">点击上传</span></p>
                      <p class="upload-hint">支持 PDF、JPG、PNG，最大 10MB</p>
                    </div>
                  </div>
                </el-upload>
                <div class="upload-tips">
                  <el-icon color="#FFB000" size="13"><InfoFilled /></el-icon>
                  <span>教师申请需审核，通常 1-3 个工作日内完成</span>
                </div>
              </div>
            </el-form-item>
          </Transition>

          <el-form-item>
            <el-checkbox v-model="form.agreeTerms" style="color:#A0A0A0;font-size:13px;">
              我已阅读并同意
              <a href="#" style="color:#FFB000;">用户服务协议</a>
              和
              <a href="#" style="color:#FFB000;">隐私政策</a>
            </el-checkbox>
          </el-form-item>

          <el-button
            type="primary"
            size="large"
            class="register-btn btn-gradient"
            :loading="loading"
            :disabled="!form.agreeTerms"
            @click="handleRegister"
          >
            {{ loading ? '注册中...' : '立即注册' }}
          </el-button>
        </el-form>

        <div class="login-link">
          已有账号？
          <router-link to="/login">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Upload, InfoFilled, Avatar } from '@element-plus/icons-vue'
import { authApi } from '@/api/auth'
import { uploadApi } from '@/api/upload'

const router  = useRouter()
const formRef = ref(null)
const uploadRef = ref(null)
const loading   = ref(false)
const fileList  = ref([])

const form = reactive({
  username:        '',
  nickname:        '',
  email:           '',
  password:        '',
  confirmPassword: '',
  role:            'STUDENT',
  qualificationFile: null,
  agreeTerms:      false
})

const validateConfirmPassword = (_rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateQualification = (_rule, _value, callback) => {
  if (form.role === 'TEACHER' && !form.qualificationFile) {
    callback(new Error('请上传教师资质证明'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 30, message: '用户名长度 3-30 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '只允许字母、数字和下划线', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { max: 20, message: '昵称最多 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度 6-32 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  qualificationFile: [
    { validator: validateQualification, trigger: 'change' }
  ]
}

const handleFileChange = (file) => {
  form.qualificationFile = file.raw
  fileList.value = [file]
}

const handleRegister = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (!form.agreeTerms) {
    ElMessage.warning('请先同意用户服务协议和隐私政策')
    return
  }

  loading.value = true
  try {
    let qualificationUrl = null

    // Upload qualification file if teacher
    if (form.role === 'TEACHER' && form.qualificationFile) {
      const fd = new FormData()
      fd.append('file', form.qualificationFile)
      const uploadRes = await uploadApi.document(fd)
      qualificationUrl = (uploadRes.data || uploadRes).url
    }

    await authApi.register({
      username: form.username,
      nickname: form.nickname,
      email:    form.email,
      password: form.password,
      role:     form.role,
      qualificationUrl
    })

    if (form.role === 'TEACHER') {
      ElMessage.success('注册成功！您的教师申请已提交，请等待管理员审核。')
    } else {
      ElMessage.success('注册成功！')
    }
    router.push('/login')
  } catch (err) {
    ElMessage.error(err.message || '注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: #121212;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 40px 16px;
  position: relative;
  overflow: hidden;
}

.bg-decoration { position: absolute; inset: 0; pointer-events: none; }
.bg-circle { position: absolute; border-radius: 50%; filter: blur(60px); }
.c1 { width: 400px; height: 400px; background: rgba(255,176,0,0.06); top: -80px; right: -80px; }
.c2 { width: 250px; height: 250px; background: rgba(255,176,0,0.04); bottom: 60px; left: 5%; }

.register-container {
  width: 100%;
  max-width: 520px;
  position: relative;
  z-index: 1;
}

.brand {
  text-align: center;
  margin-bottom: 24px;
  .brand-icon { font-size: 44px; margin-bottom: 8px; }
  .brand-title { font-size: 24px; font-weight: 800; color: #E8E8E8; margin-bottom: 4px; }
  .brand-subtitle { font-size: 14px; color: #666; }
}

.register-card {
  background: #1E1E1E;
  border: 1px solid #2E2E2E;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.5);
}

.card-header {
  margin-bottom: 20px;
  h2 { font-size: 20px; font-weight: 700; color: #E8E8E8; margin-bottom: 4px; }
  p  { font-size: 13px; color: #666; }
}

/* Role Selector */
.role-selector {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 24px;
}

.role-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 16px 12px;
  background: #252525;
  border: 2px solid #333;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  text-align: center;

  &:hover { border-color: rgba(255,176,0,0.4); background: #2A2A2A; }
  &.active {
    border-color: #FFB000;
    background: rgba(255,176,0,0.08);
    box-shadow: 0 0 0 1px rgba(255,176,0,0.2);
  }

  .role-icon { font-size: 28px; }
  .role-label { font-size: 14px; font-weight: 600; color: #E8E8E8; }
  .role-desc  { font-size: 11px; color: #666; }
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

:deep(.el-form-item__label) {
  color: #C0C0C0 !important;
  font-size: 13px;
  font-weight: 500;
}
:deep(.el-input__inner) { color: #E8E8E8 !important; }

/* Upload */
.upload-area {
  width: 100%;
  .upload-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    padding: 20px;
  }
  .upload-icon { opacity: 0.8; }
  .upload-text p { color: #A0A0A0; font-size: 14px; margin: 0; }
  .upload-link { color: #FFB000; cursor: pointer; }
  .upload-hint { font-size: 12px; color: #555; margin-top: 4px !important; }
}

.upload-tips {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  font-size: 12px;
  color: #666;
}

:deep(.el-upload-dragger) {
  background: #252525 !important;
  border-color: #333 !important;
  border-radius: 8px !important;
  &:hover { border-color: #FFB000 !important; }
}

.register-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 8px;
  margin-top: 4px;
}

.login-link {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #666;
  a { color: #FFB000; font-weight: 500; }
  a:hover { color: #FFC833; }
}

/* Transition */
.slide-down-enter-active, .slide-down-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}
.slide-down-enter-from, .slide-down-leave-to {
  opacity: 0;
  max-height: 0;
  transform: translateY(-10px);
}
.slide-down-enter-to, .slide-down-leave-from {
  opacity: 1;
  max-height: 300px;
  transform: translateY(0);
}
</style>
