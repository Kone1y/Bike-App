# 更新日志 — 20260519 阶段一：基础搭建

## 本次完成内容

### 1. 数据库（Docker MySQL 8.0）

- 容器名：`bike-mysql`，端口 `3306`，用户 `root`，密码 `123456`，数据库 `bike_app`
- `sql/schema.sql` — 11 张表建表脚本
  - 业务表：`bike`、`scenic_spot`、`parking_area`、`bike_order`、`bike_repair`
  - 系统表：`sys_user`、`sys_role`、`sys_menu`、`sys_user_role`、`sys_role_menu`、`operation_log`
- `sql/init-data.sql` — 初始数据
  - 管理员账户：`admin / 123456`（BCrypt 加密，用户类型为后台管理员）
  - 3 个预设角色：超级管理员(SUPER_ADMIN)、运营管理员(OPERATOR)、维修人员(REPAIRER)
  - 26 条菜单权限数据（首页、系统管理、车辆管理、景点与区域、报修管理，含按钮级权限）

### 2. 后端服务（Spring Boot 3.2.5 + MyBatis + JDK 21）

**项目路径：** `server/`

**依赖：** spring-boot-starter-web、mybatis、mysql-connector-j、jjwt、spring-security-crypto、zxing、lombok

**已实现的公共模块：**
- `common/result/Result.java` — 统一响应封装 `{code, message, data}`
- `common/exception/BusinessException.java` — 自定义业务异常
- `common/exception/GlobalExceptionHandler.java` — 全局异常处理
- `common/utils/JwtUtils.java` — JWT token 生成与解析
- `common/utils/DistanceUtils.java` — Haversine 公式距离计算
- `common/config/CorsConfig.java` — 跨域配置
- `common/config/WebMvcConfig.java` — 拦截器注册（白名单：login、小程序公开接口）

**已实现的安全模块：**
- `security/annotation/RequirePermission.java` — 权限注解
- `security/annotation/RequireRole.java` — 角色注解
- `security/interceptor/AuthInterceptor.java` — 登录认证拦截器
- `security/interceptor/PermissionInterceptor.java` — 权限校验拦截器

**已实现的业务接口：**
- `POST /api/auth/login` — 管理员登录（返回 JWT token）
- `POST /api/auth/logout` — 登出
- `GET /api/system/user/list` — 用户列表
- `GET /api/system/user/{id}` — 用户详情
- `POST /api/system/user` — 新增用户
- `PUT /api/system/user` — 编辑用户
- `DELETE /api/system/user/{id}` — 删除用户
- `GET /api/system/role/list` — 角色列表
- `POST /api/system/role` — 新增角色
- `PUT /api/system/role` — 编辑角色
- `DELETE /api/system/role/{id}` — 删除角色
- `GET /api/system/menu/tree` — 菜单树（全部）
- `GET /api/system/menu/user-menus` — 当前用户菜单树
- `POST /api/system/menu` — 新增菜单
- `PUT /api/system/menu` — 编辑菜单
- `DELETE /api/system/menu/{id}` — 删除菜单

**MyBatis XML 映射文件：** `resources/mapper/SysUserMapper.xml`、`SysRoleMapper.xml`、`SysMenuMapper.xml`

### 3. 管理后台前端（Vue 3 + Vite + Element Plus）

**项目路径：** `admin/`

**依赖：** element-plus、@element-plus/icons-vue、vue-router@4、pinia、axios

**已实现：**
- `src/utils/request.js` — Axios 封装（baseURL、token 拦截器、错误处理）
- `src/store/user.js` — Pinia 用户状态管理（token/userInfo 持久化到 localStorage）
- `src/router/index.js` — 路由配置 + 路由守卫（未登录跳转登录页）
- `src/layout/index.vue` — 后台布局（Element Plus Container + 可折叠侧边栏 + 顶栏 + 退出登录）
- `src/views/login/index.vue` — 登录页面（用户名+密码表单，对接后端登录接口）
- `src/views/dashboard/index.vue` — 首页仪表盘（统计卡片占位）
- 占位页面：用户管理、角色管理、菜单管理、操作日志、车辆管理、景点管理、停车区域、报修管理

### 4. 小程序（uni-app）

**项目路径：** `miniapp/`

**已实现：**
- `utils/request.js` — 网络请求封装（token、错误处理）
- `utils/location.js` — 定位和距离计算工具
- `store/index.js` — 简易状态管理
- `pages.json` — 8 个页面路由 + tabBar 配置（首页、订单、我的）
- 页面骨架：首页（地图+扫码）、登录、扫码租车、骑行中、订单列表、订单详情、故障上报、个人中心
- 注意：tabBar 图标文件为空占位，需要替换为实际图标

---

## 未完成 / 下一步

### 阶段二：管理后台开发
- [ ] 系统管理页面：用户管理 CRUD、角色管理 CRUD（含分配菜单权限）、菜单管理 CRUD
- [ ] 操作日志记录（AOP）与查询页面
- [ ] 车辆管理页面（CRUD + 状态管理 + 二维码生成/导出）
- [ ] 景点管理页面（CRUD + 地图选点）
- [ ] 停车区域管理页面（CRUD + 地图可视化）
- [ ] 报修管理页面（列表 + 处理流程）
- [ ] 首页仪表盘（车辆统计、订单统计、报修统计图表）

### 阶段三：小程序端开发
- [ ] 微信授权登录对接
- [ ] 高德地图 SDK 集成（车辆标记、景点展示、停车区域）
- [ ] 扫码租车完整流程
- [ ] 骑行中实时计时 + 地图轨迹
- [ ] 结束行程 + 计费结算
- [ ] 订单列表与详情
- [ ] 故障上报（图片上传）
- [ ] 个人中心完善

### 阶段四：联调与测试

---

## 关键信息速查

| 项目 | 值 |
|------|-----|
| MySQL 容器 | `docker start bike-mysql` |
| 数据库 | `bike_app`，`root / 123456`，`localhost:3306` |
| 后端启动 | `cd server && mvn spring-boot:run`，端口 `8080` |
| 管理后台启动 | `cd admin && npm run dev`，端口 `5173` |
| 登录账号 | `admin / 123456` |
| 小程序 | 用 HBuilderX 打开 `miniapp/` 目录 |
