import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    accessToken:  localStorage.getItem('accessToken') || null,
    refreshToken: localStorage.getItem('refreshToken') || null,
    userInfo:     JSON.parse(localStorage.getItem('userInfo') || 'null')
  }),

  getters: {
    isLoggedIn: (state) => !!state.accessToken,
    isTeacher:  (state) => state.userInfo?.role === 'TEACHER',
    isAdmin:    (state) => state.userInfo?.role === 'ADMIN',
    isStudent:  (state) => state.userInfo?.role === 'STUDENT',
    fullName:   (state) => state.userInfo?.nickname || state.userInfo?.username || '用户',
    avatar:     (state) => state.userInfo?.avatar || null
  },

  actions: {
    setTokens(accessToken, refreshToken) {
      this.accessToken  = accessToken
      this.refreshToken = refreshToken
      localStorage.setItem('accessToken', accessToken)
      if (refreshToken) {
        localStorage.setItem('refreshToken', refreshToken)
      }
    },

    setUserInfo(userInfo) {
      this.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },

    updateUserInfo(partial) {
      this.userInfo = { ...this.userInfo, ...partial }
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
    },

    logout() {
      this.accessToken  = null
      this.refreshToken = null
      this.userInfo     = null
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('userInfo')
    }
  }
})
