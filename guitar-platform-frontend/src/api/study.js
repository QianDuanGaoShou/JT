import request from './axios'

export const studyApi = {
  records:        (params) => request.get('/study/records', { params }),
  updateProgress: (data)   => request.put('/study/progress', data),
  stats:          ()       => request.get('/study/stats'),
  courseProgress: (courseId) => request.get(`/study/progress/${courseId}`),
  recentVideos:   ()       => request.get('/study/recent')
}
