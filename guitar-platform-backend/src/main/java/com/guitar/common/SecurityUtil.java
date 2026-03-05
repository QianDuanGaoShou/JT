package com.guitar.common;

import com.guitar.common.exception.BusinessException;
import com.guitar.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getUserId();
        }
        throw new BusinessException(401, "未登录");
    }

    public static String getCurrentRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getRole();
        }
        throw new BusinessException(401, "未登录");
    }

    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getUsername();
        }
        throw new BusinessException(401, "未登录");
    }
}
