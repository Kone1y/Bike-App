# 更新日志 — 20260519 阶段二：管理后台开发

## 本次完成内容

### 1. 后端 — 新增业务模块（6 个 Controller + 6 个 Mapper + 7 个实体）

#### 1.1 AOP 操作日志

- 新增 `common/annotation/Log.java` — 自定义 `@Log("操作描述")` 注解
- 新增 `common/aspect/OperationLogAspect.java` — AOP 切面，拦截带 `@Log` 的方法，自动记录操作人、操作描述、请求方法、请求参数、IP、耗时到 `operation_log` 表
- 新增 `module/system/entity/OperationLog.java` — 操作日志实体
- 新增 `mapper/OperationLogMapper.java` + `OperationLogMapper.xml` — 日志查询与插入
- 新增 `module/system/controller/OperationLogController.java` — `GET /api/system/log/list`
- 修改 `pom.xml` — 添加 `spring-boot-starter-aop` 依赖
- 修改 `UserController`、`RoleController`、`MenuController` — 所有增删改方法添加 `@Log` 注解

#### 1.2 车辆管理模块

- 新增 `module/bike/entity/Bike.java` — 车辆实体（含 areaName 非持久化字段）
- 新增 `mapper/BikeMapper.java` + `BikeMapper.xml`
  - `findList` — 支持按 bikeCode/status/areaId 筛选，LEFT JOIN parking_area 获取区域名称
  - `insert`/`update`/`deleteById` — 标准 CRUD
  - `updateStatus` — 单独的状态变更方法
  - `countByStatus`/`countAll` — 统计查询（供仪表盘使用）
- 新增 `module/bike/controller/BikeController.java`
  - `GET /api/bike/list` — 车辆分页列表
  - `GET /api/bike/{id}` — 车辆详情
  - `POST /api/bike` — 新增车辆
  - `PUT /api/bike` — 编辑车辆
  - `DELETE /api/bike/{id}` — 删除车辆（使用中不可删除）
  - `PUT /api/bike/{id}/status` — 变更车辆状态（使用中仅可变更为报修）
  - `GET /api/bike/{id}/qrcode` — 生成 ZXing 二维码，返回 base64 PNG 图片

#### 1.3 景点管理模块

- 新增 `module/scenic/entity/ScenicSpot.java` — 景点实体
- 新增 `mapper/ScenicSpotMapper.java` + `ScenicSpotMapper.xml`
  - `findList` — 支持按名称模糊搜索
  - `insert`/`update`/`deleteById` — 标准 CRUD
- 新增 `module/scenic/controller/ScenicSpotController.java`
  - `GET /api/scenic/spot/list` — 景点列表
  - `GET /api/scenic/spot/{id}` — 景点详情
  - `POST /api/scenic/spot` — 新增景点
  - `PUT /api/scenic/spot` — 编辑景点
  - `DELETE /api/scenic/spot/{id}` — 删除景点

#### 1.4 停车区域管理模块

- 新增 `module/area/entity/ParkingArea.java` — 停车区域实体
- 新增 `mapper/ParkingAreaMapper.java` + `ParkingAreaMapper.xml`
  - `findList` — 支持按名称模糊搜索
  - `insert`/`update`/`deleteById` — 标准 CRUD
- 新增 `module/area/controller/ParkingAreaController.java`
  - `GET /api/scenic/area/list` — 停车区域列表
  - `POST /api/scenic/area` — 新增区域（默认半径 200 米）
  - `PUT /api/scenic/area` — 编辑区域
  - `DELETE /api/scenic/area/{id}` — 删除区域

#### 1.5 报修管理模块

- 新增 `module/repair/entity/BikeRepair.java` — 报修实体（含 bikeCode、username 非持久化字段）
- 新增 `mapper/BikeRepairMapper.java` + `BikeRepairMapper.xml`
  - `findList` — LEFT JOIN bike + sys_user 获取车辆编码和用户名，支持按 status/bikeCode 筛选
  - `handle` — 更新报修状态、管理员备注、处理时间
  - `countByStatus` — 统计查询（供仪表盘使用）
- 新增 `module/repair/controller/BikeRepairController.java`
  - `GET /api/repair/list` — 报修列表
  - `GET /api/repair/{id}` — 报修详情
  - `PUT /api/repair/{id}/handle` — 处理报修（状态完成时自动恢复车辆为待租）

#### 1.6 仪表盘统计接口

- 新增 `mapper/BikeOrderMapper.java` + `BikeOrderMapper.xml` — 仅含 `countToday`、`countByStatus` 两个统计方法
- 新增 `module/system/controller/DashboardController.java`
  - `GET /api/dashboard/stats` — 返回待租车辆数、今日订单数、使用中车辆数、维修中车辆数、待处理报修数、车辆总数

#### 1.7 角色-菜单权限分配

- 修改 `mapper/SysRoleMapper.java` — 新增 `getMenuIdsByRoleId`、`deleteRoleMenus`、`insertRoleMenus` 三个方法
- 修改 `mapper/SysRoleMapper.xml` — 新增对应 SQL（含 `<foreach>` 批量插入）
- 修改 `RoleController.java` — 新增 `GET /{id}/menus` 和 `POST /{id}/menus` 端点

#### 1.8 其他修改

- 修改 `AuthInterceptor.java` — 解析 JWT 中的 username 并设置到 request attribute，供 AOP 日志使用
- 修改 `application.yml` — 扩展 `type-aliases-package`，覆盖新增的 bike/scenic/area/repair 实体包

---

### 2. 管理后台前端（Vue3 + Element Plus）

#### 2.1 API 接口层（9 个新模块）

| 文件 | 接口 |
|------|------|
| `api/user.js` | getUserList, getUserById, addUser, editUser, deleteUser |
| `api/role.js` | getRoleList, getRoleById, addRole, editRole, deleteRole, getRoleMenuIds, assignRoleMenus |
| `api/menu.js` | getMenuTree, getUserMenus, addMenu, editMenu, deleteMenu |
| `api/log.js` | getLogList |
| `api/bike.js` | getBikeList, getBikeById, addBike, editBike, deleteBike, updateBikeStatus, getBikeQrCode |
| `api/scenic.js` | getScenicList, getScenicById, addScenic, editScenic, deleteScenic |
| `api/area.js` | getAreaList, getAreaById, addArea, editArea, deleteArea |
| `api/repair.js` | getRepairList, getRepairById, handleRepair |
| `api/dashboard.js` | getDashboardStats |

#### 2.2 页面视图（9 个页面替换占位符）

| 页面 | 文件 | 功能 |
|------|------|------|
| 仪表盘 | `views/dashboard/index.vue` | 调用 API 获取真实统计数据（待租/今日订单/使用中/待处理报修/总数/维修中），使用 el-statistic 展示 |
| 用户管理 | `views/system/user/index.vue` | 完整 CRUD：按用户名/状态/类型搜索，新增/编辑弹窗（密码可选），删除确认 |
| 角色管理 | `views/system/role/index.vue` | 完整 CRUD + 菜单权限分配：编辑弹窗中的 el-tree 勾选菜单，支持半选节点保存 |
| 菜单管理 | `views/system/menu/index.vue` | 树形 CRUD：el-table 行树展示，新增子菜单，el-tree-select 选择上级菜单，支持目录/菜单/按钮三种类型 |
| 操作日志 | `views/system/log/index.vue` | 只读列表：按操作人/描述搜索，耗时标签（>1s 红色、>500ms 黄色、其余绿色） |
| 车辆管理 | `views/bike/index.vue` | CRUD + 状态下拉变更（待租/报修/报废）+ ZXing 二维码弹窗展示 + 区域下拉筛选 + 编码自动生成 |
| 景点管理 | `views/scenic/spot/index.vue` | CRUD + 经纬度输入 + 封面图 URL + 图片预览 |
| 停车区域 | `views/scenic/area/index.vue` | CRUD + 中心坐标输入 + 半径输入（默认 200 米） |
| 报修管理 | `views/repair/index.vue` | 按状态/车辆编码筛选 + 详情弹窗（el-descriptions + 图片画廊）+ 处理弹窗（状态+备注，完成后自动恢复车辆） |

---

### 3. 新增/修改文件清单

#### 后端新建文件（20 个）

```
server/src/main/java/com/bike/common/annotation/Log.java
server/src/main/java/com/bike/common/aspect/OperationLogAspect.java
server/src/main/java/com/bike/module/bike/entity/Bike.java
server/src/main/java/com/bike/module/bike/controller/BikeController.java
server/src/main/java/com/bike/module/scenic/entity/ScenicSpot.java
server/src/main/java/com/bike/module/scenic/controller/ScenicSpotController.java
server/src/main/java/com/bike/module/area/entity/ParkingArea.java
server/src/main/java/com/bike/module/area/controller/ParkingAreaController.java
server/src/main/java/com/bike/module/repair/entity/BikeRepair.java
server/src/main/java/com/bike/module/repair/controller/BikeRepairController.java
server/src/main/java/com/bike/module/system/entity/OperationLog.java
server/src/main/java/com/bike/module/system/controller/DashboardController.java
server/src/main/java/com/bike/module/system/controller/OperationLogController.java
server/src/main/java/com/bike/mapper/BikeMapper.java
server/src/main/java/com/bike/mapper/ScenicSpotMapper.java
server/src/main/java/com/bike/mapper/ParkingAreaMapper.java
server/src/main/java/com/bike/mapper/BikeRepairMapper.java
server/src/main/java/com/bike/mapper/OperationLogMapper.java
server/src/main/java/com/bike/mapper/BikeOrderMapper.java
server/src/main/resources/mapper/BikeMapper.xml
server/src/main/resources/mapper/ScenicSpotMapper.xml
server/src/main/resources/mapper/ParkingAreaMapper.xml
server/src/main/resources/mapper/BikeRepairMapper.xml
server/src/main/resources/mapper/OperationLogMapper.xml
server/src/main/resources/mapper/BikeOrderMapper.xml
```

#### 后端修改文件（7 个）

```
server/pom.xml                                          — 添加 spring-boot-starter-aop
server/src/main/resources/application.yml              — 扩展 type-aliases-package
server/.../security/interceptor/AuthInterceptor.java   — 设置 username 到 request attribute
server/.../mapper/SysRoleMapper.java                    — 新增角色菜单权限方法
server/src/main/resources/mapper/SysRoleMapper.xml      — 新增角色菜单权限 SQL
server/.../controller/UserController.java               — 添加 @Log 注解
server/.../controller/RoleController.java               — 添加 @Log 注解 + 菜单权限端点
server/.../controller/MenuController.java               — 添加 @Log 注解
```

#### 前端新建文件（9 个）

```
admin/src/api/user.js
admin/src/api/role.js
admin/src/api/menu.js
admin/src/api/log.js
admin/src/api/bike.js
admin/src/api/scenic.js
admin/src/api/area.js
admin/src/api/repair.js
admin/src/api/dashboard.js
```

#### 前端修改文件（9 个）

```
admin/src/views/dashboard/index.vue          — 真实统计数据替换硬编码
admin/src/views/system/user/index.vue       — 完整 CRUD 页面
admin/src/views/system/role/index.vue       — CRUD + 菜单权限分配
admin/src/views/system/menu/index.vue       — 树形 CRUD 页面
admin/src/views/system/log/index.vue        — 只读日志列表
admin/src/views/bike/index.vue              — CRUD + 状态变更 + 二维码
admin/src/views/scenic/spot/index.vue       — 景点 CRUD
admin/src/views/scenic/area/index.vue       — 停车区域 CRUD
admin/src/views/repair/index.vue            — 报修列表 + 处理
```

---

## 未完成 / 下一步

### 阶段三：小程序端开发
- [ ] 微信授权登录对接（`/api/app/auth/login`）
- [ ] 高德地图 SDK 集成（车辆标记、景点展示、停车区域可视化）
- [ ] 扫码租车完整流程（`/api/app/bike/rent`）
- [ ] 骑行中实时计时 + 地图轨迹展示
- [ ] 结束行程 + 计费结算（`/api/app/order/finish`）
- [ ] 订单列表与详情（`/api/app/order/list`、`/api/app/order/{id}`）
- [ ] 故障上报（`/api/app/repair/submit`，含图片上传）
- [ ] 个人中心完善

### 阶段四：联调与测试
- [ ] 管理后台前后端接口联调
- [ ] 小程序前后端接口联调
- [ ] 租赁业务完整流程测试（租车 → 骑行 → 结算 → 车辆恢复）
- [ ] 边界场景测试（并发租车、网络异常、未在停车区域还车）
- [ ] Bug 修复与优化

### 已知环境问题
- **Vite 8 与 Node 20 不兼容**：当前 Node 版本为 v20.18.1，Vite 8 需要 Node 22+。运行 `npm run dev` 前需升级 Node 到 v22+，或将 Vite 降级到 v5（`npm install vite@5 @vitejs/plugin-vue@5`）

---

## 关键信息速查

| 项目 | 值 |
|------|-----|
| MySQL 容器 | `docker start bike-mysql` |
| 数据库 | `bike_app`，`root / 123456`，`localhost:3306` |
| 后端启动 | `cd server && mvn spring-boot:run`，端口 `8080` |
| 管理后台启动 | `cd admin && npm run dev`，端口 `5173`（需 Node 22+） |
| 登录账号 | `admin / 123456` |
| 后端编译验证 | `cd server && mvn compile` — 本次已完成，通过 |
