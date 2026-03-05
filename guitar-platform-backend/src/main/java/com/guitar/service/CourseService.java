package com.guitar.service;

import com.guitar.common.PageResult;
import com.guitar.dto.response.CourseDetailResponse;
import com.guitar.dto.response.CourseListItemResponse;
import com.guitar.dto.response.OrderResponse;

import java.util.Map;

public interface CourseService {

    PageResult<CourseListItemResponse> getCourseList(int page, int size, String category, String keyword, Long currentUserId);

    CourseDetailResponse getCourseDetail(Long id, Long currentUserId);

    Map<String, Object> purchaseCourse(Long courseId, Long studentId);

    PageResult<OrderResponse> getMyOrders(Long studentId, int page, int size);
}
