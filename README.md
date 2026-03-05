# 吉他教学平台 - Guitar Teaching Platform

一个专业的全栈吉他在线教学平台，集课程管理、练习提交、智能评分、收益统计于一体。

## 项目概览

### 核心功能
- **学员端**: 课程浏览、购买、视频学习、练习提交、成绩查看
- **讲师端**: 课程创建、章节管理、练习评分、收益统计
- **管理端**: 讲师审核、用户管理、财务结算、平台监控

### 技术栈

**后端**
- Spring Boot 3.2
- MyBatis Plus 3.5
- Spring Security + JWT
- MySQL 8.0
- Knife4j (Swagger 3)

**前端**
- Vue 3 (Composition API)
- Element Plus
- Pinia (状态管理)
- Vue Router 4
- Axios
- ECharts 5
- Video.js

### 设计特色
- 深色主题 (#121212) + 琥珀金色 (#FFB000)
- 录音棚质感的专业UI
- 响应式布局
- 完整的权限控制

## 项目结构

```
JT/
├── guitar-platform-backend/          # Spring Boot后端
│   ├── src/main/java/com/guitar/
│   │   ├── controller/               # REST控制器
│   │   ├── service/                  # 业务逻辑
│   │   ├── mapper/                   # 数据访问层
│   │   ├── entity/                   # 数据模型
│   │   ├── dto/                      # 数据传输对象
│   │   ├── config/                   # 配置类
│   │   ├── security/                 # 安全认证
│   │   └── common/                   # 公共工具
│   ├── src/main/resources/
│   │   ├── application.yml           # 应用配置
│   │   ├── init.sql                  # 数据库初始化
│   │   └── mapper/                   # MyBatis XML映射
│   └── pom.xml                       # Maven配置
│
└── guitar-platform-frontend/         # Vue 3前端
    ├── src/
    │   ├── views/                    # 页面组件
    │   │   ├── student/              # 学员页面
    │   │   ├── teacher/              # 讲师页面
    │   │   └── admin/                # 管理页面
    │   ├── components/               # 可复用组件
    │   ├── api/                      # API接口层
    │   ├── store/                    # Pinia状态管理
    │   ├── router/                   # 路由配置
    │   ├── styles/                   # 全局样式
    │   ├── layouts/                  # 布局组件
    │   ├── main.js                   # 应用入口
    │   └── App.vue                   # 根组件
    ├── package.json                  # 依赖配置
    ├── vite.config.js                # Vite配置
    └── index.html                    # HTML入口
```

## 快速开始

### 前置要求
- Java 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 后端部署

1. **创建数据库**
```bash
mysql -u root -p < guitar-platform-backend/src/main/resources/init.sql
```

2. **配置数据库连接**
编辑 `guitar-platform-backend/src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/guitar_platform
    username: root
    password: your_password
```

3. **启动后端服务**
```bash
cd guitar-platform-backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 前端部署

1. **安装依赖**
```bash
cd guitar-platform-frontend
npm install
```

2. **开发模式**
```bash
npm run dev
```

前端应用将在 `http://localhost:5173` 启动

3. **生产构建**
```bash
npm run build
npm run preview
```

## API文档

启动后端后，访问 Knife4j 文档：
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Knife4j**: http://localhost:8080/doc.html

## 默认账户

### 管理员
- 用户名: `admin`
- 密码: `admin123`

## 核心功能说明

### 学员流程
1. 注册/登录账户
2. 浏览课程列表（支持分类、搜索、分页）
3. 购买课程（扣除账户余额）
4. 观看视频（自动保存进度）
5. 提交练习视频
6. 查看讲师评分和反馈

### 讲师流程
1. 注册讲师账户并上传资质证明
2. 等待管理员审核
3. 审核通过后创建课程
4. 添加章节和视频
5. 发布课程
6. 评分学员练习
7. 查看收益统计和月度结算

### 管理员流程
1. 审核讲师资质
2. 监控平台课程
3. 管理用户账户
4. 为学员充值余额
5. 生成财务报表
6. 查看平台数据统计

## 关键特性

### 认证与授权
- JWT双Token机制（Access Token + Refresh Token）
- Access Token有效期：2小时
- Refresh Token有效期：7天
- 基于角色的权限控制（RBAC）

### 文件管理
- 视频上传：支持mp4/mov，最大500MB
- 图片上传：支持jpg/png，最大5MB
- 本地文件存储或阿里云OSS集成

### 学习进度
- 每5秒自动保存播放位置
- 支持从上次位置继续学习
- 记录视频播放进度百分比

### 数据可视化
- 讲师收益趋势图（ECharts）
- 学员成绩趋势分析
- 平台数据统计仪表板

## 数据库表结构

### 核心表
- `sys_user` - 用户表
- `course` - 课程表
- `course_chapter` - 课程章节表
- `course_video` - 课程视频表
- `course_order` - 课程订单表
- `study_record` - 学习记录表
- `practice_submission` - 练习提交表
- `income_record` - 收益流水表
- `user_refresh_token` - 刷新令牌表

详见 `guitar-platform-backend/src/main/resources/init.sql`

## 配置说明

### 后端配置 (application.yml)
```yaml
jwt:
  secret: guitar-platform-jwt-secret-key-2024-very-long-and-secure
  access-token-expiration: 7200000    # 2小时
  refresh-token-expiration: 604800000 # 7天

file:
  upload:
    base-path: /data/guitar-platform/uploads
    base-url: http://localhost:8080/files
```

### 前端配置 (vite.config.js)
```javascript
server: {
  port: 5173,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 开发指南

### 添加新的API端点

1. **创建Entity**
```java
@Data
@TableName("table_name")
public class MyEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    // ... fields
}
```

2. **创建Mapper**
```java
@Mapper
public interface MyMapper extends BaseMapper<MyEntity> {
    // custom queries
}
```

3. **创建Service**
```java
@Service
public class MyService {
    @Autowired
    private MyMapper mapper;

    public void doSomething() {
        // business logic
    }
}
```

4. **创建Controller**
```java
@RestController
@RequestMapping("/api/my")
public class MyController {
    @Autowired
    private MyService service;

    @GetMapping
    public Result<Object> get() {
        return Result.success(service.doSomething());
    }
}
```

### 前端开发

1. **创建API接口** (`src/api/`)
```javascript
export const myApi = {
  list: (params) => axios.get('/my', { params }),
  detail: (id) => axios.get(`/my/${id}`),
  create: (data) => axios.post('/my', data)
}
```

2. **创建Vue组件** (`src/views/`)
```vue
<template>
  <div class="my-view">
    <!-- template -->
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { myApi } from '@/api'

const data = ref([])

onMounted(async () => {
  const res = await myApi.list()
  data.value = res.data.list
})
</script>

<style scoped>
/* styles */
</style>
```

## 常见问题

### Q: 如何修改主题颜色？
A: 编辑 `src/styles/variables.scss`，修改CSS变量：
```scss
--color-primary: #FFB000;      // 主色调
--color-background: #121212;   // 背景色
--color-card: #1E1E1E;         // 卡片背景
```

### Q: 如何添加新的用户角色？
A:
1. 修改 `SysUser.java` 中的 `role` 枚举
2. 在 `SecurityConfig.java` 中添加权限规则
3. 在前端路由中添加角色检查

### Q: 如何集成支付功能？
A:
1. 在 `CourseOrder` 表中添加支付状态字段
2. 在 `CourseService.purchaseCourse()` 中集成支付API
3. 添加支付回调处理

### Q: 如何部署到生产环境？
A:
1. 后端：打包为JAR，使用Docker或直接运行
2. 前端：`npm run build` 生成dist文件夹，部署到Nginx或CDN
3. 数据库：使用云数据库服务（RDS）
4. 文件存储：使用阿里云OSS或AWS S3

## 性能优化建议

1. **数据库**
   - 为常用查询字段添加索引
   - 使用分页查询大数据集
   - 定期清理过期的刷新令牌

2. **前端**
   - 使用路由懒加载
   - 图片压缩和CDN加速
   - 启用gzip压缩

3. **后端**
   - 使用Redis缓存热点数据
   - 异步处理文件上传
   - 使用消息队列处理耗时操作

## 安全建议

1. **认证**
   - 定期更新JWT密钥
   - 实现登录失败次数限制
   - 添加验证码防止暴力破解

2. **授权**
   - 验证用户权限
   - 使用HTTPS传输
   - 实现审计日志

3. **数据保护**
   - 敏感数据加密存储
   - 定期备份数据库
   - 实现数据访问控制

## 许可证

MIT License

## 联系方式

如有问题或建议，请提交Issue或Pull Request。

---

**最后更新**: 2024年3月5日
