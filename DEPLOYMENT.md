# 部署指南

## 本地开发环境部署

### 1. 后端部署

#### 1.1 环境要求
- Java 17 或更高版本
- Maven 3.6+
- MySQL 8.0+

#### 1.2 数据库初始化
```bash
# 连接MySQL
mysql -u root -p

# 执行初始化脚本
source guitar-platform-backend/src/main/resources/init.sql;
```

#### 1.3 配置修改
编辑 `guitar-platform-backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/guitar_platform?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456  # 修改为你的MySQL密码
    driver-class-name: com.mysql.cj.jdbc.Driver

  servlet:
    multipart:
      max-file-size: 500MB
      max-request-size: 500MB

jwt:
  secret: guitar-platform-jwt-secret-key-2024-very-long-and-secure
  access-token-expiration: 7200000    # 2小时
  refresh-token-expiration: 604800000 # 7天

file:
  upload:
    base-path: /data/guitar-platform/uploads  # 修改为实际路径
    base-url: http://localhost:8080/files

server:
  port: 8080
```

#### 1.4 启动后端
```bash
cd guitar-platform-backend

# 编译
mvn clean install

# 运行
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/guitar-platform-backend-1.0.0.jar
```

后端服务将在 `http://localhost:8080` 启动

#### 1.5 验证后端
访问 Swagger 文档：
- http://localhost:8080/doc.html (Knife4j)
- http://localhost:8080/swagger-ui.html (Swagger UI)

### 2. 前端部署

#### 2.1 环境要求
- Node.js 16+
- npm 8+ 或 yarn

#### 2.2 安装依赖
```bash
cd guitar-platform-frontend
npm install
```

#### 2.3 开发模式
```bash
npm run dev
```

前端应用将在 `http://localhost:5173` 启动

#### 2.4 生产构建
```bash
npm run build
npm run preview
```

构建输出在 `dist/` 目录

### 3. 测试流程

#### 3.1 学员流程测试
1. 访问 http://localhost:5173
2. 点击"注册"，选择"学员"角色
3. 填写用户名、密码、昵称，点击注册
4. 使用新账户登录
5. 浏览课程列表
6. 点击课程进入详情页
7. 购买课程（需要管理员先充值余额）
8. 观看视频，提交练习

#### 3.2 讲师流程测试
1. 注册讲师账户，上传资质证明
2. 使用管理员账户登录
3. 进入"讲师审核"，批准讲师
4. 使用讲师账户登录
5. 创建课程，添加章节和视频
6. 发布课程
7. 学员购买后，评分学员练习
8. 查看收益统计

#### 3.3 管理员流程测试
1. 使用默认管理员账户登录
   - 用户名: `admin`
   - 密码: `admin123`
2. 进入管理后台
3. 审核待审核讲师
4. 管理用户和课程
5. 查看财务报表

---

## Docker 部署

### 1. 后端 Docker 部署

#### 1.1 创建 Dockerfile
在 `guitar-platform-backend/` 目录创建 `Dockerfile`:

```dockerfile
FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/guitar-platform-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 1.2 创建 docker-compose.yml
在项目根目录创建 `docker-compose.yml`:

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: guitar-mysql
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: guitar_platform
      TZ: Asia/Shanghai
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./guitar-platform-backend/src/main/resources/init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - guitar-network

  backend:
    build:
      context: ./guitar-platform-backend
      dockerfile: Dockerfile
    container_name: guitar-backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/guitar_platform?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: 123456
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    networks:
      - guitar-network

  frontend:
    image: node:18-alpine
    container_name: guitar-frontend
    working_dir: /app
    volumes:
      - ./guitar-platform-frontend:/app
    ports:
      - "5173:5173"
    command: sh -c "npm install && npm run dev"
    networks:
      - guitar-network

volumes:
  mysql_data:

networks:
  guitar-network:
    driver: bridge
```

#### 1.3 启动容器
```bash
# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

---

## 云服务器部署（以阿里云为例）

### 1. 准备工作

#### 1.1 购买云资源
- ECS 实例（推荐 2核4GB）
- RDS MySQL 数据库
- OSS 对象存储（可选）

#### 1.2 安装依赖
```bash
# 更新系统
sudo yum update -y

# 安装 Java
sudo yum install java-17-openjdk java-17-openjdk-devel -y

# 安装 Node.js
curl -fsSL https://rpm.nodesource.com/setup_18.x | sudo bash -
sudo yum install nodejs -y

# 安装 Nginx
sudo yum install nginx -y

# 启动 Nginx
sudo systemctl start nginx
sudo systemctl enable nginx
```

### 2. 后端部署

#### 2.1 上传代码
```bash
# 本地执行
scp -r guitar-platform-backend/ root@your_server_ip:/home/app/
```

#### 2.2 配置数据库
编辑 `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://your_rds_endpoint:3306/guitar_platform
    username: root
    password: your_password
```

#### 2.3 编译和运行
```bash
cd /home/app/guitar-platform-backend
mvn clean package -DskipTests

# 后台运行
nohup java -jar target/guitar-platform-backend-1.0.0.jar > app.log 2>&1 &
```

### 3. 前端部署

#### 3.1 上传代码
```bash
scp -r guitar-platform-frontend/ root@your_server_ip:/home/app/
```

#### 3.2 构建
```bash
cd /home/app/guitar-platform-frontend
npm install
npm run build
```

#### 3.3 配置 Nginx
编辑 `/etc/nginx/nginx.conf`:

```nginx
server {
    listen 80;
    server_name your_domain.com;

    # 前端
    location / {
        root /home/app/guitar-platform-frontend/dist;
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 文件服务
    location /files {
        alias /data/guitar-platform/uploads;
        expires 30d;
    }
}
```

#### 3.4 重启 Nginx
```bash
sudo nginx -t
sudo systemctl restart nginx
```

### 4. SSL 证书配置

使用 Let's Encrypt 免费证书：

```bash
# 安装 Certbot
sudo yum install certbot python3-certbot-nginx -y

# 申请证书
sudo certbot certonly --nginx -d your_domain.com

# 配置 Nginx 使用 HTTPS
# 编辑 /etc/nginx/nginx.conf，添加 SSL 配置
```

---

## 监控和维护

### 1. 日志查看
```bash
# 后端日志
tail -f /home/app/guitar-platform-backend/app.log

# Nginx 日志
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log
```

### 2. 性能监控
```bash
# 查看进程
ps aux | grep java

# 查看端口占用
netstat -tlnp | grep 8080

# 查看磁盘使用
df -h

# 查看内存使用
free -h
```

### 3. 数据库备份
```bash
# 备份数据库
mysqldump -u root -p guitar_platform > backup_$(date +%Y%m%d).sql

# 恢复数据库
mysql -u root -p guitar_platform < backup_20240305.sql
```

### 4. 定期维护
- 每周检查日志，清理过期数据
- 每月备份数据库
- 定期更新依赖包
- 监控服务器资源使用

---

## 常见问题排查

### 问题1: 后端无法连接数据库
```
错误: Communications link failure
解决:
1. 检查 MySQL 是否运行: systemctl status mysql
2. 检查数据库连接字符串
3. 检查防火墙规则
```

### 问题2: 前端无法访问后端 API
```
错误: CORS error / 404 Not Found
解决:
1. 检查后端是否运行在 8080 端口
2. 检查 vite.config.js 中的代理配置
3. 检查 SecurityConfig 中的 CORS 配置
```

### 问题3: 文件上传失败
```
错误: 413 Payload Too Large
解决:
1. 检查 application.yml 中的 max-file-size 配置
2. 检查 Nginx 中的 client_max_body_size 配置
3. 检查上传目录权限
```

### 问题4: JWT Token 过期
```
错误: 401 Unauthorized
解决:
1. 检查 Token 是否过期
2. 使用 Refresh Token 获取新的 Access Token
3. 重新登录
```

---

## 性能优化建议

### 后端优化
1. 启用 Redis 缓存
2. 使用数据库连接池
3. 异步处理文件上传
4. 启用 Gzip 压缩

### 前端优化
1. 路由懒加载
2. 图片压缩和 CDN 加速
3. 启用 Gzip 压缩
4. 使用 CDN 加速静态资源

### 数据库优化
1. 添加适当的索引
2. 定期清理过期数据
3. 使用分区表处理大数据集
4. 定期分析和优化查询

---

## 扩展建议

### 功能扩展
- 集成支付网关（支付宝、微信）
- 添加直播功能
- 实现推荐算法
- 添加社区讨论功能

### 技术扩展
- 使用 Redis 缓存
- 使用 Elasticsearch 搜索
- 使用 RabbitMQ 消息队列
- 使用 Kubernetes 容器编排

---

**最后更新**: 2024年3月5日
