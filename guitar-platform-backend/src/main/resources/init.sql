-- Guitar Platform Database Initialization Script
-- Charset: utf8mb4

CREATE DATABASE IF NOT EXISTS guitar_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE guitar_platform;

-- 1. User table
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL COMMENT 'BCrypt encrypted',
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    email VARCHAR(100),
    bio TEXT COMMENT 'Personal bio',
    role ENUM('STUDENT', 'TEACHER', 'ADMIN') DEFAULT 'STUDENT',
    balance DECIMAL(10,2) DEFAULT 0.00,
    qualification_url VARCHAR(255) COMMENT 'Teacher qualification proof',
    audit_status ENUM('PENDING', 'APPROVED', 'REJECTED') COMMENT 'Teacher audit status',
    audit_reason VARCHAR(500) COMMENT 'Rejection reason',
    enabled TINYINT(1) DEFAULT 1,
    teaching_experience TEXT COMMENT 'Teacher teaching experience',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_role (role),
    INDEX idx_audit (audit_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System users';

-- 2. Refresh token table
CREATE TABLE IF NOT EXISTS user_refresh_token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    refresh_token VARCHAR(512) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_token (refresh_token(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Refresh tokens';

-- 3. Course table
CREATE TABLE IF NOT EXISTS course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    teacher_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    category VARCHAR(50) COMMENT 'Category: 民谣/古典/电吉他',
    price DECIMAL(10,2) NOT NULL,
    cover_url VARCHAR(255),
    description TEXT,
    status ENUM('DRAFT', 'PUBLISHED', 'OFFLINE') DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES sys_user(id),
    INDEX idx_teacher (teacher_id),
    INDEX idx_category (category),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Courses';

-- 4. Course chapter table
CREATE TABLE IF NOT EXISTS course_chapter (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    INDEX idx_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Course chapters';

-- 5. Course video table
CREATE TABLE IF NOT EXISTS course_video (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    chapter_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    video_url VARCHAR(255) NOT NULL,
    duration INT DEFAULT 0 COMMENT 'Duration in seconds',
    sort_order INT DEFAULT 0,
    file_size BIGINT DEFAULT 0 COMMENT 'File size in bytes',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (chapter_id) REFERENCES course_chapter(id) ON DELETE CASCADE,
    INDEX idx_chapter (chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Course videos';

-- 6. Course order table
CREATE TABLE IF NOT EXISTS course_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) UNIQUE NOT NULL,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES sys_user(id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    INDEX idx_student (student_id),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Course orders';

-- 7. Study record table
CREATE TABLE IF NOT EXISTS study_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    video_id BIGINT NOT NULL COMMENT 'Current video being studied',
    progress INT DEFAULT 0 COMMENT 'Video progress percentage',
    last_position INT DEFAULT 0 COMMENT 'Last playback position in seconds',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES sys_user(id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    FOREIGN KEY (video_id) REFERENCES course_video(id),
    UNIQUE KEY uk_student_video (student_id, course_id, video_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Study records';

-- 8. Practice submission table
CREATE TABLE IF NOT EXISTS practice_submission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    video_url VARCHAR(255) NOT NULL COMMENT 'Student practice video',
    total_score DECIMAL(5,2) COMMENT 'Total score (0-100)',
    teacher_feedback TEXT COMMENT 'Teacher feedback',
    status ENUM('PENDING', 'GRADED') DEFAULT 'PENDING',
    graded_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES sys_user(id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    INDEX idx_status (status),
    INDEX idx_student (student_id),
    INDEX idx_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Practice submissions';

-- 9. Income record table
CREATE TABLE IF NOT EXISTS income_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    teacher_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    order_id BIGINT COMMENT 'Related order ID',
    amount DECIMAL(10,2) NOT NULL,
    type ENUM('COURSE_PURCHASE', 'SETTLEMENT') COMMENT 'Record type',
    settle_month VARCHAR(7) COMMENT 'Settlement month YYYY-MM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES sys_user(id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    INDEX idx_teacher_month (teacher_id, settle_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Income records';

-- Insert default admin user (password: admin123)
INSERT INTO sys_user (username, password, nickname, role, enabled)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE id=id;
