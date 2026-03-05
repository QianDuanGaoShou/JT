import axios from 'axios'

const instance = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' }
})

// Token refresh state
let isRefreshing = false
let refreshQueue = []

const processQueue = (error, token = null) => {
  refreshQueue.forEach(({ resolve, reject }) => {
    if (error) {
      reject(error)
    } else {
      resolve(token)
    }
  })
  refreshQueue = []
}

// ─── Request Interceptor ─────────────────────────────────────
instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// ─── Response Interceptor ─────────────────────────────────────
instance.interceptors.response.use(
  (response) => response.data,
  async (error) => {
    const originalRequest = error.config

    if (error.response?.status === 401 && !originalRequest._retry) {
      // If already refreshing, queue this request
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          refreshQueue.push({ resolve, reject })
        })
          .then((token) => {
            originalRequest.headers['Authorization'] = `Bearer ${token}`
            return instance(originalRequest)
          })
          .catch((err) => Promise.reject(err))
      }

      originalRequest._retry = true
      isRefreshing = true

      const refreshToken = localStorage.getItem('refreshToken')

      if (!refreshToken) {
        isRefreshing = false
        // Clear auth and redirect
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        localStorage.removeItem('userInfo')
        window.location.href = '/login'
        return Promise.reject(error)
      }

      try {
        const res = await axios.post('/api/auth/refresh', { refreshToken })
        const { accessToken, refreshToken: newRefreshToken } = res.data.data || res.data

        localStorage.setItem('accessToken', accessToken)
        if (newRefreshToken) {
          localStorage.setItem('refreshToken', newRefreshToken)
        }

        instance.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`
        originalRequest.headers['Authorization'] = `Bearer ${accessToken}`

        processQueue(null, accessToken)
        return instance(originalRequest)
      } catch (refreshError) {
        processQueue(refreshError, null)
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        localStorage.removeItem('userInfo')
        window.location.href = '/login'
        return Promise.reject(refreshError)
      } finally {
        isRefreshing = false
      }
    }

    // Handle other errors
    const message = error.response?.data?.message || error.message || '请求失败'

    if (error.response?.status === 403) {
      window.location.href = '/login'
    }

    return Promise.reject({ message, status: error.response?.status, data: error.response?.data })
  }
)

export default instance
