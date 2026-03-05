import request from './axios'

export const courseApi = {
  list:        (params) => request.get('/courses', { params }),
  detail:      (id)     => request.get(`/courses/${id}`),
  purchase:    (id)     => request.post(`/courses/${id}/purchase`),
  myOrders:    (params) => request.get('/orders', { params }),
  categories:  ()       => request.get('/courses/categories'),
  reviews:     (id, params) => request.get(`/courses/${id}/reviews`, { params }),
  addReview:   (id, data)   => request.post(`/courses/${id}/reviews`, data)
}
