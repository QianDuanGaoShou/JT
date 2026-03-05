# 快速参考指南

## 🚀 30秒快速启动

### 方式1: Docker (推荐)
```bash
docker-compose up -d
# 等待2-3分钟，然后访问:
# 前端: http://localhost:5173
# 后端API: http://localhost:8080
# API文档: http://localhost:8080/doc.html
```

### 方式2: 本地开发
```bash
# 终端1: 启动后端
cd guitar-platform-backend
mvn spring-boot:run

# 终端2: 启动前端
cd guitar-platform-frontend
npm install
npm run dev
```

---

## 📋 默认账户

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 可直接登录 |
| 学员 | - | - | 自行注册 |
| 讲师 | - | - | 自行注册（需审核） |

---

## 🔗 重要链接

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端应用 | http://localhost:5173 | Vue 3应用 |
| 后端API | http://localhost:8080 | Spring Boot服务 |
| API文档 | http://localhost:8080/doc.html | Knife4j文档 |
| Swagger | http://localhost:8080/swagger-ui.html | Swagger UI |
| MySQL | localhost:3306 | 数据库 |

---

## 📁 项目结构速览

```
JT/
├── guitar-platform-backend/
│   ├── src/main/java/com/guitar/
│   │   ├── controller/          # 7个REST控制器
│   │   ├── service/             # 7个业务服务
│   │   ├── mapper/              # 9个数据访问层
│   │   ├── entity/              # 9个数据模型
│   │   ├── dto/                 # 22个数据传输对象
│   │   ├── config/              # 5个配置类
│   │   ├── security/            # 3个安全类
│   │   └── common/              # 工具和异常处理
│   ├── src/main/resources/
│   │   ├── application.yml      # 应用配置
│   │   ├── init.sql             # 数据库初始化
│   │   └── mapper/              # MyBatis XML映射
│   ├── pom.xml
│   └── Dockerfile
│
├── guitar-platform-frontend/
│   ├── src/
│   │   ├── views/               # 15个页面
│   │   ├── components/          # 3个可复用组件
│   │   ├── layouts/             # 3个布局
│   │   ├── api/                 # 7个API模块
│   │   ├── store/               # Pinia状态管理
│   │   ├── router/              # Vue Router路由
│   │   ├── styles/              # 全局样式
│   │   └── main.js              # 应用入口
│   ├── package.json
│   ├── vite.config.js
│   ├── Dockerfile
│   ├── nginx.conf
│   └── index.html
│
├── docker-compose.yml           # 容器编排
├── README.md                    # 项目文档
├── DEPLOYMENT.md                # 部署指南
└── PROJECT_SUMMARY.md           # 项目总结
```

---

## 🎯 核心功能速览

### 学员功能
- ✅ 注册/登录
- ✅ 浏览课程（分类、搜索、分页）
- ✅ 购买课程
- ✅ 观看视频（自动保存进度）
- ✅ 提交练习
- ✅ 查看评分

### 讲师功能
- ✅ 注册/认证
- ✅ 创建课程
- ✅ 管理章节和视频
- ✅ 评分学员练习
- ✅ 查看收益统计
- ✅ 月度结算

### 管理员功能
- ✅ 审核讲师
- ✅ 管理用户
- ✅ 充值学员余额
- ✅ 查看财务报表
- ✅ 平台数据统计

---

## 🔑 关键配置

### 后端配置 (application.yml)
```yaml
# 数据库
spring.datasource.url: jdbc:mysql://localhost:3306/guitar_platform
spring.datasource.username: root
spring.datasource.password: 123456

# JWT
jwt.secret: guitar-platform-jwt-secret-key-2024-very-long-and-secure
jwt.access-token-expiration: 7200000    # 2小时
jwt.refresh-token-expiration: 604800000 # 7天

# 文件上传
file.upload.base-path: /data/guitar-platform/uploads
file.upload.base-url: http://localhost:8080/files

# 服务器
server.port: 8080
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

---

## 📊 数据库表

| 表名 | 说明 | 记录数 |
|------|------|--------|
| sys_user | 用户表 | 用户数 |
| course | 课程表 | 课程数 |
| course_chapter | 章节表 | 章节数 |
| course_video | 视频表 | 视频数 |
| course_order | 订单表 | 订单数 |
| study_record | 学习记录 | 学习记录数 |
| practice_submission | 练习提交 | 练习数 |
| income_record | 收益流水 | 收益记录数 |
| user_refresh_token | 刷新令牌 | 活跃令牌数 |

---

## 🛠️ 常用命令

### 后端
```bash
# 编译
mvn clean install

# 运行
mvn spring-boot:run

# 打包
mvn clean package

# 查看依赖
mvn dependency:tree
```

### 前端
```bash
# 安装依赖
npm install

# 开发模式
npm run dev

# 生产构建
npm run build

# 预览构建结果
npm run preview
```

### Docker
```bash
# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down

# 重启服务
docker-compose restart

# 查看容器状态
docker-compose ps
```

### 数据库
```bash
# 连接MySQL
mysql -u root -p

# 选择数据库
use guitar_platform;

# 查看表
show tables;

# 查看表结构
describe sys_user;

# 备份数据库
mysqldump -u root -p guitar_platform > backup.sql

# 恢复数据库
mysql -u root -p guitar_platform < backup.sql
```

---

## 🔍 API快速查询

### 认证
```
POST   /api/auth/register          # 注册
POST   /api/auth/login             # 登录
POST   /api/auth/refresh           # 刷新Token
POST   /api/auth/logout            # 登出
GET    /api/auth/me                # 获取用户信息
PUT    /api/auth/profile           # 更新资料
```

### 课程
```
GET    /api/courses                # 课程列表
GET    /api/courses/{id}           # 课程详情
POST   /api/courses/{id}/purchase  # 购买课程
GET    /api/orders                 # 我的订单
```

### 学习
```
GET    /api/study/records          # 学习记录
PUT    /api/study/progress         # 更新进度
GET    /api/study/stats            # 学习统计
```

### 练习
```
POST   /api/practice/submit        # 提交练习
GET    /api/practice/list          # 练习列表
GET    /api/practice/{id}/report   # 练习报告
```

### 讲师
```
POST   /api/teacher/course         # 创建课程
PUT    /api/teacher/course/{id}    # 更新课程
GET    /api/teacher/courses        # 我的课程
GET    /api/teacher/practice/pending  # 待评分
PUT    /api/teacher/practice/{id}/grade  # 评分
GET    /api/teacher/stats/income   # 收益统计
```

### 管理
```
GET    /api/admin/teachers/pending # 待审核讲师
PUT    /api/admin/teacher/{id}/audit  # 审核讲师
GET    /api/admin/users            # 用户列表
POST   /api/admin/user/{id}/recharge  # 充值
GET    /api/admin/report/settle    # 结算报表
```

### 上传
```
POST   /api/upload/video           # 上传视频
POST   /api/upload/image           # 上传图片
```

---

## 🎨 主题颜色

| 用途 | 颜色 | 十六进制 |
|------|------|---------|
| 背景 | 深灰 | #121212 |
| 卡片 | 深灰 | #1E1E1E |
| 主色 | 琥珀金 | #FFB000 |
| 文字 | 白色 | #FFFFFF |
| 次文字 | 浅灰 | #E0E0E0 |

修改位置: `guitar-platform-frontend/src/styles/variables.scss`

---

## 🐛 常见问题

### Q: 后端无法启动
```
检查:
1. Java版本 >= 17
2. MySQL是否运行
3. 数据库连接字符串
4. 端口8080是否被占用
```

### Q: 前端无法访问API
```
检查:
1. 后端是否运行在8080
2. vite.config.js中的代理配置
3. 浏览器控制台的CORS错误
4. 防火墙设置
```

### Q: 文件上传失败
```
检查:
1. 上传目录权限
2. 文件大小限制
3. 磁盘空间
4. 文件格式
```

### Q: 登录后无法访问页面
```
检查:
1. Token是否正确保存
2. Token是否过期
3. 用户角色权限
4. 路由守卫配置
```

---

## 📈 性能优化建议

### 后端
- [ ] 启用Redis缓存
- [ ] 添加数据库索引
- [ ] 使用连接池
- [ ] 异步处理文件上传
- [ ] 启用Gzip压缩

### 前端
- [ ] 路由懒加载
- [ ] 图片压缩
- [ ] CDN加速
- [ ] 启用Gzip
- [ ] 代码分割

### 数据库
- [ ] 定期清理过期数据
- [ ] 优化查询语句
- [ ] 添加适当索引
- [ ] 定期备份

---

## 📞 获取帮助

1. 查看 `README.md` - 项目概览
2. 查看 `DEPLOYMENT.md` - 部署指南
3. 查看 `PROJECT_SUMMARY.md` - 项目总结
4. 访问 API文档 - http://localhost:8080/doc.html
5. 查看源代码注释

---

## ✅ 检查清单

启动前检查:
- [ ] Java 17+ 已安装
- [ ] Node.js 16+ 已安装
- [ ] MySQL 8.0+ 已安装
- [ ] 端口 3306, 8080, 5173 未被占用
- [ ] 数据库已初始化

启动后检查:
- [ ] 后端服务运行在 8080
- [ ] 前端应用运行在 5173
- [ ] 可以访问 API 文档
- [ ] 可以登录管理员账户
- [ ] 可以注册新账户

---

**最后更新**: 2024年3月5日
**版本**: 1.0.0
**状态**: ✅ 完全可用
