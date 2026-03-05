# 吉他教学平台全栈开发设计方案

## Context（项目背景）

本项目旨在开发一个专业的吉他在线教学平台，解决传统吉他教学中的以下痛点：
- 学员难以获得专业讲师的即时反馈
- 讲师缺乏有效的收益管理和学员进度追踪工具
- 平台管理方需要高效的资质审核和财务结算系统

目标是构建一个集课程管理、练习提交、智能评分、收益统计于一体的全栈应用，采用现代化技术栈（Spring Boot 3 + Vue 3 + Element Plus），提供专业录音棚质感的用户体验。

---

## 技术架构

### 前端技术栈
- **框架**: Vue 3 (Composition API)
- **UI组件库**: Element Plus（深度定制）
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP客户端**: Axios
- **数据可视化**: ECharts 5
- **视频播放**: Video.js

### 后端技术栈
- **框架**: Spring Boot 3.2
- **ORM**: MyBatis Plus 3.5
- **安全**: Spring Security + JWT
- **API文档**: Knife4j (Swagger 3)
- **数据库**: MySQL 8.0
- **文件存储**: 阿里云OSS/本地存储

### 设计规范
- **配色方案**:
  - 背景色: #121212 (深色模式)
  - 主色调: #FFB000 (琥珀金)
  - 辅助色: #1E1E1E (卡片背景)
- **动画效果**: Card悬浮位移、按钮渐变、页面过渡
- **圆角规范**: 按钮 8px，卡片 12px

---

## 系统功能概览

### 一、学员端功能模块

#### 1. 用户管理
- ✅ 用户注册（邮箱/用户名注册）
- ✅ 用户登录（JWT Token认证）
- ✅ 个人资料管理（头像、昵称、个人简介）
- ✅ 账户余额查看（查看余额、消费记录）
- ✅ 登出功能

#### 2. 课程浏览与购买
- ✅ 课程列表展示（分页、分类过滤、关键词搜索）
- ✅ 课程详情查看（讲师信息、课程描述、时长、价格）
- ✅ 课程分类（民谣吉他、古典吉他、电吉他）
- ✅ 课程购买（支付、订单生成、余额扣费）
- ✅ 已购课程列表

#### 3. 视频学习
- ✅ 视频播放器（支持暂停、快进、倍速播放）
- ✅ 学习进度自动保存（记录播放位置、学习进度百分比）
- ✅ 学习记录查询（查看已学课程、学习进度、最后播放位置）
- ✅ 继续学习功能（从上次位置继续播放）
- ✅ 课程章节导航

#### 4. 练习提交与反馈
- ✅ 练习视频上传（支持mp4/mov格式，最大500MB）
- ✅ 练习列表查看（待评分、已评分状态）
- ✅ 练习评分报告查看
  - 总分评分（0-100分）
  - 讲师评语反馈
- ✅ 练习历史记录

#### 5. 学习数据分析
- ✅ 个人学习统计（已学课程数、总学习时长、平均分数）
- ✅ 练习成绩趋势（显示历史评分变化）
- ✅ 学习成就展示

---

### 二、讲师端功能模块

#### 1. 讲师认证与管理
- ✅ 讲师注册（需上传资质证明）
- ✅ 资质审核等待（显示审核状态）
- ✅ 讲师信息管理（头像、昵称、个人简介、教学经验）
- ✅ 资质证明更新

#### 2. 课程管理
- ✅ 课程创建（标题、分类、价格、描述、视频上传）
- ✅ 课程编辑（修改课程信息、价格、状态）
- ✅ 课程发布（草稿→发布→下线）
- ✅ 课程列表管理（查看所有课程、统计学员数、收益）
- ✅ 课程删除（仅草稿状态可删除）
- ✅ 视频元数据管理（时长、大小、格式）

#### 3. 练习评分与反馈
- ✅ 待评分练习列表（按课程过滤、按时间排序）
- ✅ 练习视频播放与查看
- ✅ 总分评分（0-100分）
- ✅ 评语反馈编写
- ✅ 批量评分功能
- ✅ 已评分练习查看

#### 4. 收益管理与统计
- ✅ 收益统计看板
  - 总收益展示
  - 月度收益曲线图（ECharts）
  - 学员数统计
  - 待评分数统计
- ✅ 收益详情查询（按月份、按课程）
- ✅ 收益流水记录（课程购买、结算记录）
- ✅ 月度结算单查看

#### 5. 学员管理
- ✅ 学员列表（按课程查看购买学员）
- ✅ 学员学习进度追踪
- ✅ 学员练习提交情况统计

---

### 三、管理端功能模块

#### 1. 讲师审核管理
- ✅ 待审核讲师列表（显示资质证明）
- ✅ 讲师资质审核（批准/拒绝）
- ✅ 审核意见反馈
- ✅ 已审核讲师列表
- ✅ 讲师禁用/启用功能

#### 2. 课程管理
- ✅ 全平台课程列表
- ✅ 课程审核（确保内容合规）
- ✅ 课程下线处理
- ✅ 课程统计（总数、分类分布）

#### 3. 财务管理
- ✅ 月度结算报表生成
  - 讲师ID、讲师名称
  - 课程数、学员数
  - 总收益、平台分成
  - 结算状态
- ✅ 收益流水查询
- ✅ 平台收益统计

#### 4. 用户管理
- ✅ 全平台用户列表
- ✅ 用户角色管理（学员/讲师/管理员）
- ✅ 用户禁用/启用
- ✅ 用户数据统计
- ✅ 学员余额充值（管理员手动充值）

#### 5. 系统监控
- ✅ 平台数据统计
  - 总用户数、讲师数、学员数
  - 总课程数、总收益
  - 日活跃用户数
- ✅ 异常数据预警

---

### 四、通用功能模块

#### 1. 文件管理
- ✅ 视频上传（课程视频、练习视频）
- ✅ 图片上传（头像、课程封面、资质证明）
- ✅ 文件格式验证（mp4/mov、jpg/png）
- ✅ 文件大小限制（视频500MB、图片5MB）
- ✅ 文件URL生成与访问

#### 2. 认证与授权
- ✅ JWT Token认证
- ✅ Token自动刷新
- ✅ 基于角色的权限控制（RBAC）
- ✅ 接口权限验证

#### 3. 数据统计与报表
- ✅ 用户行为数据收集
- ✅ 学习数据统计
- ✅ 收益数据统计
- ✅ 报表导出功能

#### 4. 消息与通知
- ✅ 课程购买成功通知
- ✅ 练习评分完成通知
- ✅ 讲师审核结果通知
- ✅ 系统消息提示

---

## 核心业务流程

### 学员学习流程
```
注册登录 → 浏览课程 → 购买课程 → 观看视频 → 提交练习 → 查看评分 → 继续学习
```

### 讲师教学流程
```
注册认证 → 等待审核 → 创建课程 → 发布课程 → 评分练习 → 查看收益 → 月度结算
```

### 管理员管理流程
```
审核讲师 → 监控课程 → 查看数据 → 生成报表 → 财务结算 → 系统维护
```

---

## 详细功能说明

### 学员端详细功能

#### 1. 用户认证与管理
**注册功能**
- 支持用户名/邮箱注册
- 密码使用BCrypt加密存储
- 注册时自动创建学员账户（role=STUDENT）
- 初始余额为0，可通过充值增加
- 返回用户ID和基本信息

**登录功能**
- 用户名/邮箱 + 密码登录
- 验证成功后生成JWT Token（有效期24小时）
- 返回Token和用户信息（ID、昵称、头像、角色）
- Token用于后续所有需要认证的请求

**个人资料管理**
- 修改昵称、头像、个人简介
- 查看账户余额、消费记录
- 查看已购课程数、学习时长统计
- 支持头像上传（jpg/png，最大5MB）
- 余额由管理员充值，学员无法自行充值

#### 2. 课程浏览与购买
**课程列表**
- 分页展示所有已发布课程（每页10条）
- 支持按分类过滤（民谣/古典/电吉他）
- 支持关键词搜索（课程标题、讲师名称）
- 显示课程封面、标题、讲师名称、价格、时长
- 显示是否已购买标记

**课程详情**
- 完整课程信息（标题、分类、价格、时长、描述）
- 讲师信息（头像、昵称、教学经验）
- 学员评价和评分（如有）
- 是否已购买、学习进度（如已购买）
- 购买按钮或继续学习按钮

**课程购买**
- 点击购买后扣除账户余额
- 生成订单记录（订单号、金额、时间）
- 购买成功后自动创建学习记录
- 发送购买成功通知
- 返回订单信息和剩余余额

#### 3. 视频学习
**视频播放**
- 使用Video.js播放器
- 支持暂停、播放、快进、快退
- 支持倍速播放（0.5x、1x、1.5x、2x）
- 支持全屏播放
- 显示当前播放时间和总时长

**进度保存**
- 每5秒自动保存一次播放位置
- 记录学习进度百分比（已播放时长/总时长）
- 用户离开页面时保存最后位置
- 下次进入课程时自动从上次位置继续播放

**学习记录**
- 显示已购课程列表（按最近学习时间排序）
- 显示每门课程的学习进度、最后学习时间
- 支持按课程名称搜索
- 点击课程可继续学习

#### 4. 练习提交与反馈
**练习上传**
- 选择课程后上传练习视频
- 支持mp4/mov格式，最大500MB
- 上传前验证文件格式和大小
- 上传成功后显示视频URL和上传时间
- 支持重新上传（覆盖之前的练习）

**练习列表**
- 显示所有已提交的练习
- 按状态过滤（待评分/已评分）
- 显示练习提交时间、讲师评分状态
- 支持按课程名称搜索

**评分报告**
- 显示讲师给出的总分评分（0-100分）
- 显示讲师评语反馈
- 显示评分时间

#### 5. 学习数据分析
**个人统计**
- 已学课程数
- 总学习时长（小时）
- 平均练习分数
- 最高分数的课程

**成绩趋势**
- 折线图展示历史评分变化
- 显示最近10次练习的评分趋势
- 对比不同课程的评分差异

---

### 讲师端详细功能

#### 1. 讲师认证
**讲师注册**
- 注册时选择讲师角色（role=TEACHER）
- 需上传资质证明（pdf/jpg/png，最大10MB）
- 资质证明包括：教师证、音乐学位证、获奖证书等
- 注册后进入待审核状态（audit_status=PENDING）
- 审核通过前无法发布课程

**审核状态**
- PENDING：等待管理员审核
- APPROVED：审核通过，可发布课程
- REJECTED：审核未通过，显示拒绝原因

#### 2. 课程管理
**课程创建**
- 输入课程信息：标题、分类、价格、描述
- 上传课程封面（jpg/png，最大5MB）
- 上传课程视频（mp4/mov，最大500MB）
- 系统自动获取视频时长
- 初始状态为DRAFT（草稿）

**课程编辑**
- 修改课程信息（标题、分类、价格、描述）
- 修改课程封面和视频
- 修改课程状态（DRAFT→PUBLISHED→OFFLINE）
- 仅DRAFT状态可删除课程

**课程发布**
- 草稿状态下点击发布
- 发布后状态变为PUBLISHED
- 已发布课程显示在学员课程列表中
- 可随时下线课程（状态变为OFFLINE）

**课程列表**
- 显示讲师的所有课程
- 显示课程状态、学员数、总收益
- 支持按状态过滤（DRAFT/PUBLISHED/OFFLINE）
- 支持按课程名称搜索

#### 3. 练习评分
**待评分列表**
- 显示所有待评分的学员练习
- 显示学员名称、头像、课程名称、提交时间
- 支持按课程过滤
- 按提交时间倒序排列

**评分界面**
- 显示学员信息和练习视频
- 可播放学员上传的练习视频
- 输入总分评分（0-100分）
- 编写评语反馈（最多500字）
- 提交评分

**已评分查看**
- 显示已评分的练习列表
- 显示评分时间和讲师评语
- 支持修改评分和评语

#### 4. 收益管理
**收益看板**
- 显示总收益（所有课程的累计收益）
- 显示本月收益
- 显示学员总数
- 显示待评分数量

**收益曲线**
- ECharts折线图展示月度收益趋势
- X轴：月份（YYYY-MM格式）
- Y轴：收益金额（元）
- 支持选择时间范围查看

**收益详情**
- 按月份查看收益明细
- 显示每月的学员数、课程销售数、总收益
- 支持按课程查看收益分布
- 显示收益流水记录

**月度结算**
- 显示每月的结算单
- 包含课程数、学员数、总收益、平台分成、讲师收益
- 支持查看结算历史

#### 5. 学员管理
**学员列表**
- 显示购买讲师课程的所有学员
- 显示学员名称、头像、购买课程数
- 支持按课程过滤学员
- 支持按学员名称搜索

**学员进度追踪**
- 显示每个学员的学习进度
- 显示学员已学课程、学习时长
- 显示学员提交的练习数和平均分数

---

### 管理端详细功能

#### 1. 讲师审核
**待审核列表**
- 显示所有待审核的讲师申请
- 显示讲师用户名、昵称、申请时间
- 显示资质证明文件（可下载查看）
- 按申请时间倒序排列

**审核操作**
- 查看讲师资质证明
- 批准讲师（状态变为APPROVED）
- 拒绝讲师（状态变为REJECTED，需填写拒绝原因）
- 审核通过后讲师可发布课程

**已审核列表**
- 显示所有已审核的讲师
- 显示审核状态、审核时间
- 支持禁用/启用讲师账户

#### 2. 课程管理
**全平台课程列表**
- 显示所有课程（包括草稿、已发布、已下线）
- 显示课程标题、讲师名称、分类、价格、学员数
- 支持按分类过滤
- 支持按讲师名称搜索

**课程审核**
- 检查课程内容是否合规
- 可下线不合规课程
- 显示课程详细信息和学员评价

#### 3. 财务管理
**月度结算报表**
- 生成指定月份的结算报表
- 包含所有讲师的结算数据
- 显示讲师ID、讲师名称、课程数、学员数、总收益、平台分成、讲师收益
- 在线查看报表数据

**收益流水**
- 显示所有收益记录
- 按时间倒序排列
- 显示交易类型（课程购买/结算）
- 支持按讲师、按月份过滤

**平台收益统计**
- 显示平台总收益
- 显示本月收益
- 显示讲师总数、学员总数、课程总数
- 显示平台分成比例

#### 4. 用户管理
**用户列表**
- 显示所有用户（学员、讲师、管理员）
- 显示用户名、昵称、角色、注册时间
- 支持按角色过滤
- 支持按用户名搜索

**用户操作**
- 修改用户角色
- 禁用/启用用户账户
- 查看用户详细信息

**学员余额充值**
- 选择学员账户
- 输入充值金额
- 添加充值备注
- 记录充值操作日志

**数据统计**
- 用户总数、学员数、讲师数、管理员数
- 新增用户数（按日/周/月）
- 活跃用户数

#### 5. 系统监控
**平台数据**
- 总用户数、讲师数、学员数
- 总课程数、已发布课程数
- 总收益、本月收益
- 日活跃用户数

**异常预警**
- 异常登录提醒
- 大额交易提醒
- 讲师资质过期提醒
- 系统性能告警

---

## 数据库设计

### 核心表结构

#### 1. 用户表 (sys_user)
```sql
CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL COMMENT 'BCrypt加密',
  nickname VARCHAR(50),
  avatar VARCHAR(255),
  role ENUM('STUDENT', 'TEACHER', 'ADMIN') DEFAULT 'STUDENT',
  balance DECIMAL(10,2) DEFAULT 0.00,
  qualification_url VARCHAR(255) COMMENT '讲师资质证明',
  audit_status ENUM('PENDING', 'APPROVED', 'REJECTED') COMMENT '审核状态',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_role (role),
  INDEX idx_audit (audit_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 2. 课程表 (course)
```sql
CREATE TABLE course (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  teacher_id BIGINT NOT NULL,
  title VARCHAR(100) NOT NULL,
  category VARCHAR(50) COMMENT '分类：民谣/古典/电吉他',
  price DECIMAL(10,2) NOT NULL,
  cover_url VARCHAR(255),
  video_url VARCHAR(255),
  duration INT COMMENT '时长(秒)',
  description TEXT,
  status ENUM('DRAFT', 'PUBLISHED', 'OFFLINE') DEFAULT 'DRAFT',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (teacher_id) REFERENCES sys_user(id),
  INDEX idx_teacher (teacher_id),
  INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 3. 学习记录表 (study_record)
```sql
CREATE TABLE study_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  progress INT DEFAULT 0 COMMENT '进度百分比',
  last_position INT DEFAULT 0 COMMENT '上次播放位置(秒)',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (student_id) REFERENCES sys_user(id),
  FOREIGN KEY (course_id) REFERENCES course(id),
  UNIQUE KEY uk_student_course (student_id, course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 4. 练习提交表 (practice_submission)
```sql
CREATE TABLE practice_submission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  video_url VARCHAR(255) NOT NULL COMMENT '学员练习视频',
  total_score DECIMAL(5,2) COMMENT '总分(0-100)',
  teacher_feedback TEXT COMMENT '讲师评语',
  status ENUM('PENDING', 'GRADED') DEFAULT 'PENDING',
  graded_at TIMESTAMP NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (student_id) REFERENCES sys_user(id),
  FOREIGN KEY (course_id) REFERENCES course(id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 5. 收益流水表 (income_record)
```sql
CREATE TABLE income_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  teacher_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  student_id BIGINT NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  type ENUM('COURSE_PURCHASE', 'SETTLEMENT') COMMENT '类型',
  settle_month VARCHAR(7) COMMENT '结算月份 YYYY-MM',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (teacher_id) REFERENCES sys_user(id),
  INDEX idx_teacher_month (teacher_id, settle_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 后端API接口设计

### 统一响应格式
```java
public class Result<T> {
    private Integer code;  // 200成功, 400参数错误, 401未授权, 500服务器错误
    private String message;
    private T data;

    // 成功响应
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    // 失败响应
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
```

---

## 完整API接口文档

### 一、学员端API

#### 1.1 用户注册
**接口**: `POST /api/auth/register`

**请求头**: 无需认证

**请求体**:
```json
{
  "username": "student01",
  "password": "123456",
  "nickname": "吉他小白",
  "role": "STUDENT"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "student01",
    "nickname": "吉他小白",
    "role": "STUDENT",
    "createdAt": "2024-03-05T10:30:00"
  }
}
```

---

#### 1.2 用户登录
**接口**: `POST /api/auth/login`

**请求头**: 无需认证

**请求体**:
```json
{
  "username": "student01",
  "password": "123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "username": "student01",
      "nickname": "吉他小白",
      "role": "STUDENT",
      "avatar": "https://example.com/avatar.jpg"
    }
  }
}
```

---

#### 1.3 课程列表（分页+过滤）
**接口**: `GET /api/courses`

**请求头**: `Authorization: Bearer {token}`

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `category`: 分类过滤（可选：民谣/古典/电吉他）
- `keyword`: 关键词搜索（可选）

**请求示例**: `GET /api/courses?page=1&size=10&category=民谣`

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 25,
    "list": [
      {
        "id": 1,
        "title": "民谣吉他入门教程",
        "category": "民谣",
        "price": 99.00,
        "coverUrl": "https://example.com/cover1.jpg",
        "duration": 3600,
        "teacherName": "张老师",
        "teacherId": 10,
        "status": "PUBLISHED",
        "createdAt": "2024-03-01T10:00:00"
      }
    ]
  }
}
```

---

#### 1.4 课程详情
**接口**: `GET /api/courses/{id}`

**请求头**: `Authorization: Bearer {token}`

**路径参数**: `id` - 课程ID

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 1,
    "title": "民谣吉他入门教程",
    "category": "民谣",
    "price": 99.00,
    "coverUrl": "https://example.com/cover1.jpg",
    "videoUrl": "https://example.com/video1.mp4",
    "duration": 3600,
    "description": "适合零基础学员的民谣吉他入门课程",
    "teacherInfo": {
      "id": 10,
      "nickname": "张老师",
      "avatar": "https://example.com/teacher.jpg"
    },
    "isPurchased": false,
    "studyProgress": 0
  }
}
```

---

#### 1.5 购买课程
**接口**: `POST /api/courses/{id}/purchase`

**请求头**: `Authorization: Bearer {token}`

**路径参数**: `id` - 课程ID

**请求体**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "购买成功",
  "data": {
    "orderId": "ORD20240305001",
    "courseId": 1,
    "amount": 99.00,
    "balance": 901.00
  }
}
```

---

#### 1.6 我的学习记录
**接口**: `GET /api/study/records`

**请求头**: `Authorization: Bearer {token}`

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 5,
    "list": [
      {
        "id": 1,
        "courseId": 1,
        "courseTitle": "民谣吉他入门教程",
        "coverUrl": "https://example.com/cover1.jpg",
        "progress": 65,
        "lastPosition": 2340,
        "updatedAt": "2024-03-05T09:30:00"
      }
    ]
  }
}
```

---

#### 1.7 更新学习进度
**接口**: `PUT /api/study/progress`

**请求头**: `Authorization: Bearer {token}`

**请求体**:
```json
{
  "courseId": 1,
  "progress": 70,
  "lastPosition": 2520
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "进度已保存",
  "data": null
}
```

---

#### 1.8 提交练习视频
**接口**: `POST /api/practice/submit`

**请求头**:
- `Authorization: Bearer {token}`
- `Content-Type: multipart/form-data`

**请求体**（FormData）:
- `courseId`: 课程ID
- `video`: 视频文件（File）

**响应示例**:
```json
{
  "code": 200,
  "message": "提交成功，等待讲师评分",
  "data": {
    "id": 100,
    "videoUrl": "https://example.com/practice/student1_20240305.mp4",
    "status": "PENDING",
    "createdAt": "2024-03-05T10:45:00"
  }
}
```

---

#### 1.9 我的练习列表
**接口**: `GET /api/practice/list`

**请求头**: `Authorization: Bearer {token}`

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `status`: 状态过滤（可选：PENDING/GRADED）

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 8,
    "list": [
      {
        "id": 100,
        "courseTitle": "民谣吉他入门教程",
        "videoUrl": "https://example.com/practice/student1.mp4",
        "status": "GRADED",
        "totalScore": 85.5,
        "createdAt": "2024-03-05T10:45:00",
        "gradedAt": "2024-03-05T14:20:00"
      }
    ]
  }
}
```

---

#### 1.10 获取练习评分报告
**接口**: `GET /api/practice/{id}/report`

**请求头**: `Authorization: Bearer {token}`

**路径参数**: `id` - 练习提交ID

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 100,
    "courseTitle": "民谣吉他入门教程",
    "videoUrl": "https://example.com/practice/student1.mp4",
    "totalScore": 85.5,
    "teacherFeedback": "节奏感很好，指法需要加强练习，整体表现不错！",
    "gradedAt": "2024-03-05T14:20:00"
  }
}
```

---

### 二、讲师端API

#### 2.1 创建课程
**接口**: `POST /api/teacher/course`

**请求头**: `Authorization: Bearer {token}`

**请求体**:
```json
{
  "title": "古典吉他进阶教程",
  "category": "古典",
  "price": 199.00,
  "coverUrl": "https://example.com/cover2.jpg",
  "videoUrl": "https://example.com/video2.mp4",
  "duration": 5400,
  "description": "适合有基础的学员进阶学习"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "课程创建成功",
  "data": {
    "id": 2,
    "title": "古典吉他进阶教程",
    "status": "DRAFT",
    "createdAt": "2024-03-05T11:00:00"
  }
}
```

---

#### 2.2 更新课程
**接口**: `PUT /api/teacher/course/{id}`

**请求头**: `Authorization: Bearer {token}`

**路径参数**: `id` - 课程ID

**请求体**:
```json
{
  "title": "古典吉他进阶教程（更新版）",
  "price": 179.00,
  "status": "PUBLISHED"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

#### 2.3 我的课程列表
**接口**: `GET /api/teacher/courses`

**请求头**: `Authorization: Bearer {token}`

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `status`: 状态过滤（可选：DRAFT/PUBLISHED/OFFLINE）

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 3,
    "list": [
      {
        "id": 1,
        "title": "民谣吉他入门教程",
        "category": "民谣",
        "price": 99.00,
        "status": "PUBLISHED",
        "studentCount": 120,
        "totalIncome": 11880.00,
        "createdAt": "2024-03-01T10:00:00"
      }
    ]
  }
}
```

---

#### 2.4 待评分练习列表
**接口**: `GET /api/teacher/practice/pending`

**请求头**: `Authorization: Bearer {token}`

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `courseId`: 课程ID过滤（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 15,
    "list": [
      {
        "id": 101,
        "studentName": "吉他小白",
        "studentAvatar": "https://example.com/avatar1.jpg",
        "courseTitle": "民谣吉他入门教程",
        "videoUrl": "https://example.com/practice/student2.mp4",
        "createdAt": "2024-03-05T11:30:00"
      }
    ]
  }
}
```

---

#### 2.5 练习评分
**接口**: `PUT /api/teacher/practice/{id}/grade`

**请求头**: `Authorization: Bearer {token}`

**路径参数**: `id` - 练习提交ID

**请求体**:
```json
{
  "totalScore": 85.5,
  "feedback": "节奏感很好，指法需要加强练习，整体表现不错！"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "评分成功",
  "data": {
    "totalScore": 85.5,
    "gradedAt": "2024-03-05T14:20:00"
  }
}
```

---

#### 2.6 收益统计
**接口**: `GET /api/teacher/stats/income`

**请求头**: `Authorization: Bearer {token}`

**查询参数**:
- `startMonth`: 开始月份（格式：YYYY-MM）
- `endMonth`: 结束月份（格式：YYYY-MM）

**请求示例**: `GET /api/teacher/stats/income?startMonth=2024-01&endMonth=2024-03`

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "totalIncome": 35600.00,
    "monthlyData": [
      {
        "month": "2024-01",
        "income": 10200.00,
        "studentCount": 45
      },
      {
        "month": "2024-02",
        "income": 12500.00,
        "studentCount": 52
      },
      {
        "month": "2024-03",
        "income": 12900.00,
        "studentCount": 58
      }
    ],
    "chartData": {
      "months": ["2024-01", "2024-02", "2024-03"],
      "incomes": [10200, 12500, 12900]
    }
  }
}
```

---

### 三、管理端API

#### 3.1 待审核讲师列表
**接口**: `GET /api/admin/teachers/pending`

**请求头**: `Authorization: Bearer {token}` (需要ADMIN角色)

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 8,
    "list": [
      {
        "id": 15,
        "username": "teacher_zhang",
        "nickname": "张老师",
        "qualificationUrl": "https://example.com/cert/zhang.pdf",
        "auditStatus": "PENDING",
        "createdAt": "2024-03-04T16:30:00"
      }
    ]
  }
}
```

---

#### 3.2 审核讲师
**接口**: `PUT /api/admin/teacher/{id}/audit`

**请求头**: `Authorization: Bearer {token}` (需要ADMIN角色)

**路径参数**: `id` - 讲师用户ID

**请求体**:
```json
{
  "status": "APPROVED",
  "reason": "资质审核通过"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "审核完成",
  "data": null
}
```

---

#### 3.3 月度结算报表查询
**接口**: `GET /api/admin/report/settle`

**请求头**: `Authorization: Bearer {token}` (需要ADMIN角色)

**查询参数**:
- `month`: 结算月份（格式：YYYY-MM）
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）

**请求示例**: `GET /api/admin/report/settle?month=2024-03&page=1&size=10`

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 25,
    "list": [
      {
        "teacherId": 10,
        "teacherName": "张老师",
        "courseCount": 3,
        "studentCount": 120,
        "totalIncome": 11880.00,
        "settleStatus": "已结算"
      }
    ]
  }
}
```

---

#### 3.4 学员余额充值
**接口**: `POST /api/admin/user/{id}/recharge`

**请求头**: `Authorization: Bearer {token}` (需要ADMIN角色)

**路径参数**: `id` - 学员用户ID

**请求体**:
```json
{
  "amount": 1000.00,
  "remark": "管理员充值"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "充值成功",
  "data": {
    "userId": 1,
    "balance": 2000.00,
    "rechargeAmount": 1000.00
  }
}
```

---

### 四、文件上传API

#### 4.1 上传视频
**接口**: `POST /api/upload/video`

**请求头**:
- `Authorization: Bearer {token}`
- `Content-Type: multipart/form-data`

**请求体**（FormData）:
- `file`: 视频文件（支持mp4/mov，最大500MB）
- `type`: 上传类型（course/practice）

**响应示例**:
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "https://example.com/videos/20240305_abc123.mp4",
    "duration": 3600,
    "size": 104857600
  }
}
```

---

#### 4.2 上传图片
**接口**: `POST /api/upload/image`

**请求头**:
- `Authorization: Bearer {token}`
- `Content-Type: multipart/form-data`

**请求体**（FormData）:
- `file`: 图片文件（支持jpg/png，最大5MB）
- `type`: 上传类型（avatar/cover/qualification）

**响应示例**:
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "https://example.com/images/20240305_xyz789.jpg"
  }
}
```

---

### 五、错误码说明

| 错误码 | 说明 | 示例场景 |
|--------|------|----------|
| 200 | 成功 | 正常响应 |
| 400 | 参数错误 | 缺少必填字段、格式不正确 |
| 401 | 未授权 | Token无效或过期 |
| 403 | 权限不足 | 学员访问讲师接口 |
| 404 | 资源不存在 | 课程ID不存在 |
| 409 | 冲突 | 用户名已存在、重复购买 |
| 500 | 服务器错误 | 数据库异常、文件上传失败 |

**错误响应示例**:
```json
{
  "code": 401,
  "message": "Token已过期，请重新登录",
  "data": null
}
```

---

## 前端页面设计

### 核心页面结构

#### 1. 学员端 - 视频学习页 (/course/:id)
**布局**: 左右分栏
- **左侧 (70%)**: Video.js播放器
  - 自动记录播放进度
  - 支持倍速播放
- **右侧 (30%)**: el-tabs切换
  - Tab1: 章节列表（el-timeline）
  - Tab2: 练习反馈（显示总分+评语）

**关键交互**:
```javascript
// 提交练习
const submitPractice = async (videoFile) => {
  const formData = new FormData();
  formData.append('video', videoFile);
  formData.append('courseId', courseId);
  await axios.post('/api/practice/submit', formData);
};
```

#### 2. 讲师端 - 收益看板 (/teacher/dashboard)
**组件**:
- ECharts折线图：月度收益趋势
- 统计卡片：总收益、待评分数、学员数

**数据可视化**:
```javascript
const incomeChart = {
  xAxis: { data: ['2024-01', '2024-02', ...] },
  yAxis: {},
  series: [{ type: 'line', data: [1200, 1500, ...] }]
};
```

#### 3. 学员端 - 练习报告页 (/practice/:id/report)
**页面布局**:
- 显示练习视频
- 显示总分评分（0-100分）
- 显示讲师评语反馈
- 显示评分时间

### Element Plus定制主题
```scss
// variables.scss
$--color-primary: #FFB000;
$--background-color-base: #121212;
$--border-radius-base: 8px;

.el-card {
  background: #1E1E1E;
  transition: transform 0.3s;
  &:hover {
    transform: translateY(-4px);
  }
}

.el-button--primary {
  background: linear-gradient(135deg, #FFB000 0%, #FF8C00 100%);
}
```

---

## 关键技术实现

### 1. JWT认证拦截器
```java
@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) {
        String token = request.getHeader("Authorization");
        if (token == null || !JwtUtil.verify(token)) {
            throw new UnauthorizedException("Token无效");
        }
        return true;
    }
}
```

### 2. 文件上传服务
```java
@Service
public class FileService {
    public String uploadVideo(MultipartFile file) {
        // 1. 验证文件格式（mp4/mov）
        // 2. 生成唯一文件名
        // 3. 上传到OSS/本地存储
        // 4. 返回访问URL
    }
}
```

---

## 项目结构

### 后端目录结构
```
guitar-platform-backend/
├── src/main/java/com/guitar/
│   ├── controller/      # 控制器层
│   │   ├── AuthController.java
│   │   ├── CourseController.java
│   │   ├── PracticeController.java
│   │   └── TeacherController.java
│   ├── service/         # 业务逻辑层
│   ├── mapper/          # MyBatis映射器
│   ├── entity/          # 实体类
│   ├── dto/             # 数据传输对象
│   ├── config/          # 配置类
│   │   ├── SecurityConfig.java
│   │   └── SwaggerConfig.java
│   └── common/          # 公共类
│       ├── Result.java
│       └── JwtUtil.java
└── src/main/resources/
    ├── application.yml
    └── mapper/          # XML映射文件
```

### 前端目录结构
```
guitar-platform-frontend/
├── src/
│   ├── views/           # 页面组件
│   │   ├── student/
│   │   │   ├── CourseList.vue
│   │   │   ├── CourseDetail.vue
│   │   │   └── PracticeReport.vue
│   │   ├── teacher/
│   │   │   ├── Dashboard.vue
│   │   │   └── GradePractice.vue
│   │   └── admin/
│   ├── components/      # 公共组件
│   │   └── VideoPlayer.vue
│   ├── api/             # API封装
│   │   ├── auth.js
│   │   ├── course.js
│   │   └── practice.js
│   ├── store/           # Pinia状态管理
│   ├── router/          # 路由配置
│   └── styles/          # 样式文件
│       └── variables.scss
```

---

## 验证测试方案

### 端到端测试流程

1. **用户注册登录**
   - 注册学员账号 → 登录获取Token
   - 注册讲师账号 → 上传资质 → 等待审核

2. **课程购买学习**
   - 浏览课程列表 → 购买课程
   - 进入学习页 → 播放视频 → 自动保存进度

3. **练习提交评分**
   - 上传练习视频 → 讲师端查看待评分
   - 讲师打分 → 学员查看评分报告

4. **收益统计**
   - 讲师端查看收益曲线
   - 管理端查看月度结算报表

### 接口测试
- 使用Knife4j在线文档测试所有API
- 验证JWT Token过期处理
- 测试文件上传大小限制

### 前端测试
- 检查深色主题样式一致性
- 验证Card悬浮动画效果
- 测试ECharts图表响应式布局

---

## 开发优先级

### Phase 1: 核心功能（2周）
1. 数据库建表 + 后端基础架构
2. 用户认证模块（注册/登录/JWT）
3. 课程CRUD接口
4. 前端基础布局 + 路由配置

### Phase 2: 业务功能（2周）
1. 视频播放 + 学习进度记录
2. 练习提交 + 文件上传
3. 讲师评分功能
4. 雷达图可视化

### Phase 3: 高级功能（1周）
1. 收益统计 + ECharts图表
2. 管理端审核功能
3. 报表导出
4. UI深度定制优化

---

## 关键文件清单

### 后端核心文件
- `AuthController.java` - 认证接口
- `CourseController.java` - 课程管理
- `PracticeController.java` - 练习提交评分
- `JwtInterceptor.java` - Token拦截器
- `FileService.java` - 文件上传服务

### 前端核心文件
- `CourseDetail.vue` - 视频学习页
- `PracticeReport.vue` - 练习评分报告页
- `Dashboard.vue` - 讲师收益看板
- `api/axios.js` - Axios封装（含Token注入）
- `styles/variables.scss` - 主题定制

### 配置文件
- `application.yml` - Spring Boot配置
- `vite.config.js` - 前端构建配置
- `init.sql` - 数据库初始化脚本
