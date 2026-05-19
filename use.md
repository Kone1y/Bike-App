# 景区共享单车租赁系统 — 启动指南

## 环境要求

| 工具 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 17+ | 后端 Spring Boot |
| Maven | 3.8+ | 后端构建 |
| Node.js | 20+ | 前端构建（Vite 5） |
| Docker | 任意 | MySQL 数据库容器 |
| npm | 10+ | 包管理 |

---

## 一、数据库（MySQL Docker 容器）

### 启动

```bash
docker start bike-mysql
```

### 停止

```bash
docker stop bike-mysql
```

### 验证

```bash
docker exec bike-mysql mysql -uroot -p123456 --default-character-set=utf8mb4 -e "SELECT 1"
```

### 连接信息

- 地址：`localhost:3306`
- 数据库：`bike_app`
- 用户名：`root`
- 密码：`123456`

> 首次使用需执行建表脚本：
> ```bash
> docker exec -i bike-mysql mysql -uroot -p123456 --default-character-set=utf8mb4 bike_app < sql/schema.sql
> docker exec -i bike-mysql mysql -uroot -p123456 --default-character-set=utf8mb4 bike_app < sql/init-data.sql
> ```

---

## 二、后端（Spring Boot）

### 启动

```bash
cd server
mvn spring-boot:run
```

启动成功标志：控制台输出 `Started BikeApplication` 且无异常。

### 停止

```bash
# 方法一：在运行 mvn spring-boot:run 的终端中按 Ctrl + C

# 方法二：通过端口查找进程并终止
netstat -ano | findstr :8080
taskkill /PID <进程号> /F
```

### 验证

```bash
curl http://localhost:8080/api/app/scenic/list
# 应返回 {"code":200,"message":"success","data":[...]}
```

> **注意**：后端启动依赖数据库，请确保 MySQL 容器已先启动。如果代码有变更，先执行 `mvn compile` 再启动。

---

## 三、管理后台（Vue3 + Element Plus）

### 首次安装依赖

```bash
cd admin
npm install
```

### 启动

```bash
cd admin
npm run dev
```

### 停止

```bash
# 方法一：在运行 npm run dev 的终端中按 Ctrl + C

# 方法二：通过端口查找进程并终止
netstat -ano | findstr :5173
taskkill /PID <进程号> /F
```

### 验证

浏览器访问 http://localhost:5173

登录账号：`admin` / `123456`

> **注意**：管理后台依赖后端 API，请确保后端（8080）已先启动。

---

## 四、小程序 H5 模式

### 首次安装依赖

```bash
cd miniapp
npm install
```

### 启动

```bash
cd miniapp
npm run dev:h5
```

### 停止

```bash
# 方法一：在运行 npm run dev:h5 的终端中按 Ctrl + C

# 方法二：通过端口查找进程并终止
netstat -ano | findstr :5174
taskkill /PID <进程号> /F
```

### 验证

浏览器访问 http://localhost:5174

登录方式：点击「开发模式登录」按钮（无需微信环境，任意 code 即可登录）

> **注意**：小程序 H5 依赖后端 API，请确保后端（8080）已先启动。H5 模式下地图组件使用浏览器原生定位，需允许浏览器获取位置权限。

---

## 五、快速启动（完整流程）

按以下顺序依次启动，每个服务在新的终端窗口中运行：

```bash
# 终端 1：启动数据库
docker start bike-mysql

# 终端 2：启动后端
cd server
mvn spring-boot:run

# 终端 3：启动管理后台（等后端启动完成后）
cd admin
npm run dev

# 终端 4：启动小程序 H5（等后端启动完成后）
cd miniapp
npm run dev:h5
```

### 快速停止所有服务

```bash
# 停止后端（8080）
for /f "tokens=5" %a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do taskkill /PID %a /F

# 停止管理后台（5173）
for /f "tokens=5" %a in ('netstat -ano ^| findstr :5173 ^| findstr LISTENING') do taskkill /PID %a /F

# 停止小程序 H5（5174）
for /f "tokens=5" %a in ('netstat -ano ^| findstr :5174 ^| findstr LISTENING') do taskkill /PID %a /F

# 停止数据库
docker stop bike-mysql
```

---

## 六、生产构建

### 管理后台

```bash
cd admin
npm run build
# 产物在 admin/dist/ 目录，部署到 Nginx
```

### 小程序 H5

```bash
cd miniapp
npm run build:h5
# 产物在 miniapp/dist/build/h5/ 目录
```

### 后端

```bash
cd server
mvn clean package -DskipTests
java -jar target/bike-server.jar --spring.profiles.active=prod
```

---

## 七、服务端口汇总

| 服务 | 端口 | 地址 | 启动依赖 |
|------|------|------|----------|
| MySQL | 3306 | `localhost:3306` | 无 |
| 后端 API | 8080 | http://localhost:8080 | MySQL |
| 管理后台 | 5173 | http://localhost:5173 | 后端 API |
| 小程序 H5 | 5174 | http://localhost:5174 | 后端 API |

> 管理后台和小程序 H5 不能同时使用同一端口，如需修改端口，编辑各自的 `vite.config.js` 或启动命令中指定 `--port` 参数。

---

## 八、常见问题

### Q: 后端启动报 "Port 8080 was already in use"

后端没有正常关闭，旧进程仍占用端口：

```bash
netstat -ano | findstr :8080
taskkill /PID <进程号> /F
```

### Q: 管理后台启动后页面空白 / 接口 404

检查后端是否已启动（端口 8080），管理后台 API 代理指向后端。

### Q: 管理后台中文显示乱码

数据库字符集问题，确保 Docker MySQL 使用 utf8mb4：

```bash
# 检查字符集
docker exec bike-mysql mysql -uroot -p123456 -e "SHOW VARIABLES LIKE 'character_set%%';"

# 如果不是 utf8mb4，需在容器中添加配置后重启：
# docker exec bike-mysql bash -c 'echo "[client]\ndefault-character-set=utf8mb4\n[mysql]\ndefault-character-set=utf8mb4\n[mysqld]\ncharacter-set-server=utf8mb4\ncollation-server=utf8mb4_unicode_ci" > /etc/mysql/conf.d/charset.cnf'
# docker restart bike-mysql

# 然后重建数据：
# docker exec -i bike-mysql mysql -uroot -p123456 --default-character-set=utf8mb4 bike_app < sql/schema.sql
# docker exec -i bike-mysql mysql -uroot -p123456 --default-character-set=utf8mb4 bike_app < sql/init-data.sql
```

### Q: 管理后台提示"无操作权限"

数据库 `sys_menu` 表中缺少菜单权限数据。重新执行 `init-data.sql` 即可修复（该脚本已包含完整的权限配置）。

### Q: 小程序 H5 页面空白

检查终端是否有编译错误。如出现 `Failed to load url /main.js` 错误，确认 `miniapp/index.html` 中没有手写 `<script>` 标签（uni-app 编译器会自动注入入口）。

### Q: 小程序 H5 地图不显示

H5 模式下 `<map>` 组件使用浏览器原生定位，需允许浏览器获取位置权限。部分浏览器要求 HTTPS 环境。

### Q: npm install 报错

确保 Node.js 版本 >= 20，删除 `node_modules` 和 `package-lock.json` 后重新安装：

```bash
rm -rf node_modules package-lock.json
npm install
```
