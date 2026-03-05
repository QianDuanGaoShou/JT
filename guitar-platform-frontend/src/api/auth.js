import request from './axios'

export const authApi = {
  register:      (data)          => request.post('/auth/register', data),
  login:         (data)          => request.post('/auth/login', data),
  logout:        ()              => request.post('/auth/logout'),
  refresh:       (refreshToken)  => request.post('/auth/refresh', { refreshToken }),
  getProfile:    ()              => request.get('/auth/me'),
  updateProfile: (data)          => request.put('/auth/profile', data),
  uploadAvatar:  (formData)      => request.post('/auth/avatar', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  changePassword: (data)         => request.put('/auth/password', data)
}
