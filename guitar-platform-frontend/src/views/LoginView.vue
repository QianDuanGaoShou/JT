<template>
  <div class="login-page">
    <!-- Background decoration -->
    <div class="bg-decoration">
      <div class="bg-circle c1"></div>
      <div class="bg-circle c2"></div>
      <div class="bg-circle c3"></div>
    </div>

    <div class="login-container fade-in">
      <!-- Logo / Branding -->
      <div class="brand">
        <div class="brand-icon">🎸</div>
        <h1 class="brand-title">吉他教学平台</h1>
        <p class="brand-subtitle">专业吉他在线学习社区</p>
      </div>

      <!-- Login Card -->
      <div class="login-card">
        <div class="card-header">
          <h2>欢迎回来</h2>
          <p>登录你的账号，继续学习之旅</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          @submit.prevent="handleLogin"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
              clearable
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="rememberMe" label="记住我" style="color:#A0A0A0;" />
            <a href="#" style="font-size:13px;color:#A0A0A0;">忘记密码?</a>
          </div>

          <el-button
            type="primary"
            size="large"
            class="login-btn btn-gradient"
            :loading="loading"
            @click="handleLogin"
          >
            <span v-if="!loading">登录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form>

        <div class="register-link">
          还没有账号？
          <router-link to="/register">立即注册</router-link>
        </div>
      </div>

      <!-- Feature tags -->
      <div class="features">
        <div class="feature-tag" v-for="f in features" :key="f">
          <el-icon color="#FFB000" size="14"><Check /></el-icon>
          {{ f }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Check } from '@element-plus/icons-vue'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/store/auth'

const router    = useRouter()
const route     = useRoute()
const authStore = useAuthStore()

const formRef   = ref(null)
const loading   = ref(false)
const rememberMe= ref(true)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 30, message: '用户名长度 3-30 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 个字符', trigger: 'blur' }
  ]
}

const features = ['专业教师团队', '高清视频课程', '实时练习反馈', '个性化学习路径']

const handleLogin = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await authApi.login({ username: form.username, password: form.password })
    const data = res.data || res

    authStore.setTokens(data.accessToken, data.refreshToken)

    // Fetch user profile
    try {
      const profileRes = await authApi.getProfile()
      authStore.setUserInfo(profileRes.data || profileRes)
    } catch {}

    ElMessage.success('登录成功！')

    // Redirect based on role or query param
    const redirect = route.query.redirect
    const userRole = authStore.userInfo?.role

    if (redirect) {
      router.push(redirect)
    } else if (userRole === 'ADMIN') {
      router.push('/admin/dashboard')
    } else if (userRole === 'TEACHER') {
      router.push('/teacher/dashboard')
    } else {
      router.push('/courses')
    }
  } catch (err) {
    ElMessage.error(err.message || '登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: #121212;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* Background circles */
.bg-decoration { position: absolute; inset: 0; pointer-events: none; }
.bg-circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
}
.c1 {
  width: 400px; height: 400px;
  background: rgba(255,176,0,0.06);
  top: -100px; left: -100px;
}
.c2 {
  width: 300px; height: 300px;
  background: rgba(255,176,0,0.04);
  bottom: -50px; right: 10%;
}
.c3 {
  width: 200px; height: 200px;
  background: rgba(255,176,0,0.03);
  top: 40%; left: 60%;
}

.login-container {
  width: 100%;
  max-width: 420px;
  padding: 24px 16px;
  position: relative;
  z-index: 1;
}

/* Brand */
.brand {
  text-align: center;
  margin-bottom: 28px;
}
.brand-icon {
  font-size: 52px;
  margin-bottom: 8px;
  filter: drop-shadow(0 0 20px rgba(255,176,0,0.5));
}
.brand-title {
  font-size: 28px;
  font-weight: 800;
  color: #E8E8E8;
  letter-spacing: -0.5px;
  margin-bottom: 4px;
}
.brand-subtitle {
  font-size: 14px;
  color: #666;
}

/* Login Card */
.login-card {
  background: #1E1E1E;
  border: 1px solid #2E2E2E;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.5);
}
.card-header {
  margin-bottom: 24px;
  h2 {
    font-size: 22px;
    font-weight: 700;
    color: #E8E8E8;
    margin-bottom: 6px;
  }
  p { font-size: 14px; color: #666; }
}

:deep(.el-form-item__label) {
  color: #C0C0C0 !important;
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 4px;
}

:deep(.el-input__inner) {
  color: #E8E8E8 !important;
  background: transparent !important;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: -4px 0 16px;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 8px;
  letter-spacing: 0.5px;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
  a { color: #FFB000; font-weight: 500; }
  a:hover { color: #FFC833; }
}

/* Features */
.features {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
  margin-top: 24px;
}
.feature-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: rgba(255,176,0,0.08);
  border: 1px solid rgba(255,176,0,0.2);
  border-radius: 100px;
  font-size: 12px;
  color: #A0A0A0;
}
</style>
