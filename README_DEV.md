# 景区共享单车租赁系统 — 启动指南

## 前置条件

| 工具 | 版本 | 当前状态 |
|------|------|----------|
| Docker Desktop | 28.x | 需要运行中 |
| JDK | 21 | 已安装 |
| Maven | 3.9+ | 已安装 |
| Node.js | 20+ | 已安装 |
| HBuilderX | 最新版 | 小程序开发需要 |

## 第一步：启动数据库

```bash
# 启动 MySQL 容器（如果之前已停止）
docker start bike-mysql

# 验证容器是否运行
docker ps

# 查看数据库
docker exec -it bike-mysql mysql -uroot -p123456 bike_app -e "SHOW TABLES;"
```

# 查看数据库
数据库内容正常。你可以用以下常用命令查看数据：

  ┌──────────────┬─────────────────────────────────────────────────────────────────────────────────────┐
  │     操作     │                                        命令                                         │  ├──────────────┼─────────────────────────────────────────────────────────────────────────────────────┤
  │ 查看所有表   │ docker exec bike-mysql mysql -uroot -p123456 bike_app -e "SHOW TABLES;"             │  ├──────────────┼─────────────────────────────────────────────────────────────────────────────────────┤
  │ 查看表结构   │ docker exec bike-mysql mysql -uroot -p123456 bike_app -e "DESC bike;"               │
  ├──────────────┼─────────────────────────────────────────────────────────────────────────────────────┤
  │ 查询用户数据 │ docker exec bike-mysql mysql -uroot -p123456 bike_app -e "SELECT * FROM sys_user\G" │
  ├──────────────┼─────────────────────────────────────────────────────────────────────────────────────┤
  │ 进入交互模式 │ winpty docker exec -it bike-mysql mysql -uroot -p123456 bike_app                    │
  └──────────────┴─────────────────────────────────────────────────────────────────────────────────────┘

  ▎ 注意：Windows 下进入交互模式需要加 winpty 前缀，退出输入 exit。

> 数据库连接信息：`localhost:3306`、用户名 `root`、密码 `123456`、数据库 `bike_app`

如果容器丢失，重新创建：
```bash
docker run -d --name bike-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 -e MYSQL_DATABASE=bike_app mysql:8.0 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

# 导入数据
# 需要使用CMD命令
docker exec -i bike-mysql mysql -uroot -p123456 < sql/schema.sql
docker exec -i bike-mysql mysql -uroot -p123456 < sql/init-data.sql
```

## 第二步：启动后端服务

```bash
cd server
mvn spring-boot:run
```

启动成功后访问：`http://localhost:8080`

测试登录接口（确认后端正常）：
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

应该返回包含 `token` 的 JSON 数据。

## 第三步：启动管理后台

```bash
cd admin
npm install    # 首次运行时需要
npm run dev
```

启动成功后访问：`http://localhost:5173`

- 账号：`admin`
- 密码：`123456`
- 登录后可以看到左侧菜单和首页仪表盘

## 第四步：查看小程序（可选）

用 HBuilderX 打开 `miniapp/` 目录，运行到微信开发者工具即可预览。

## 查看项目进度

当前处于 **阶段一完成** 状态，详情参见 `20260519_update_log.md`。

快速查看各模块完成情况：

| 模块 | 后端接口 | 管理后台页面 | 小程序页面 |
|------|---------|-------------|-----------|
| 认证登录 | ✅ 已完成 | ✅ 已完成 | 占位 |
| 系统管理（用户/角色/菜单） | ✅ 已完成 | 占位页面 | — |
| 车辆管理 | ❌ 未开始 | 占位页面 | 占位 |
| 景点管理 | ❌ 未开始 | 占位页面 | 占位 |
| 停车区域 | ❌ 未开始 | 占位页面 | 占位 |
| 订单/租赁 | ❌ 未开始 | ❌ 未开始 | 占位 |
| 报修管理 | ❌ 未开始 | 占位页面 | 占位 |
| 操作日志 | ❌ 未开始 | 占位页面 | — |

## 下一步开发

继续 **阶段二：管理后台开发**，将占位页面替换为完整的 CRUD 页面，并补充后端业务接口。
