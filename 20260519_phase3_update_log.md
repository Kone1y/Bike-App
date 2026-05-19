# 更新日志 — 20260519 阶段三：小程序端开发

## 本次完成内容

### 1. 后端 — 数据库变更 + 配置扩展

#### 1.1 数据库变更

- 需执行的 SQL（未自动执行，需手动运行）：

```sql
ALTER TABLE sys_user ADD COLUMN openid VARCHAR(64) DEFAULT '' COMMENT '微信openid' AFTER avatar;
ALTER TABLE sys_user ADD UNIQUE KEY uk_openid (openid);
```

> `openid` 字段用于微信小程序登录时唯一标识用户，支持"查找或自动创建"逻辑。

#### 1.2 application.yml 新增配置

- 新增 `wechat` 配置块（appid, secret, dev-mode: true）
- 新增 `billing` 配置块（base-fee: 2.00, free-duration: 10, rate-per-minute: 0.50, daily-cap: 30.00）
- `dev-mode: true` 时后端不调用真实微信 API，直接将 code 作为 mock openid，方便开发调试

#### 1.3 静态资源配置

- 新增 `ResourceConfig.java` — 将 `file.upload-path` 目录映射为 `/uploads/**` 静态资源路径，供小程序访问上传的图片

#### 1.4 拦截器调整

- 修改 `WebMvcConfig.java` — PermissionInterceptor 排除 `/api/app/**` 路径（小程序端不需要 RBAC 权限校验）

---

### 2. 后端 — 实体与 DTO 新建

#### 2.1 订单实体

- 新增 `module/order/entity/BikeOrder.java` — 订单实体（id, orderNo, userId, bikeId, startTime, endTime, startLng/Lat, endLng/Lat, duration, amount, status, createTime, bikeCode(LEFT JOIN 字段)）
- 新增 `module/order/entity/RentDTO.java` — 租车请求 DTO（bikeCode, startLng, startLat）
- 新增 `module/order/entity/FinishDTO.java` — 结算请求 DTO（orderId, endLng, endLat）

#### 2.2 SysUser 扩展

- 修改 `SysUser.java` — 添加 `openid` 字段

---

### 3. 后端 — Mapper 扩展（4 个 Mapper 新增/修改）

#### 3.1 BikeOrderMapper（大幅扩展）

- 原有：`countToday()`, `countByStatus()` — 仅供仪表盘统计
- 新增：
  - `insert(BikeOrder)` — 创建订单
  - `update(BikeOrder)` — 动态更新（结束时间、金额、状态等）
  - `findById(id)` — 查询订单详情（LEFT JOIN bike 获取 bikeCode）
  - `findRidingByUserId(userId)` — 查询用户当前骑行中的订单
  - `findListByUserId(userId, status)` — 用户订单列表（支持状态筛选）
  - `countTodayByUserId(userId)` — 用户今日已完成订单数
  - `sumTodayAmountByUserId(userId)` — 用户今日已消费总额（用于每日封顶检查）

#### 3.2 SysUserMapper

- 新增 `findByOpenid(openid)` — 根据微信 openid 查找用户
- 修改 `insert` SQL — 新增 openid 列

#### 3.3 BikeMapper

- 新增 `findByCode(bikeCode)` — 根据车辆编码查找车辆（扫码租车核心查询）

#### 3.4 BikeRepairMapper

- 新增 `insert(BikeRepair)` — 小程序端提交故障上报时使用

---

### 4. 后端 — 计费服务

- 新增 `module/order/service/BillingService.java`
  - `calculate(userId, startTime, endTime)` → `BillingResult(durationMinutes, amount)`
  - 计费逻辑：
    1. 始终收取基础费 ¥2.00
    2. 骑行时长超过免费时长（10 分钟）后，超出部分按 ¥0.50/分钟计费
    3. 检查每日封顶（¥30.00）：查询用户当日已完成订单总额，剩余额度不足时扣减
    4. 金额四舍五入到两位小数
  - 所有参数从 `application.yml` 的 `billing` 配置块读取，支持后台配置修改

---

### 5. 后端 — 小程序端 App Controller（6 个）

#### 5.1 AppAuthController — `/api/app/auth`

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/app/auth/login` | POST | 小程序用户登录 |

- 接收 `{ "code": "..." }` 请求体
- `dev-mode=true` 时：将 code 前缀加 `mock_` 作为 openid，直接查找或创建用户
- `dev-mode=false` 时：调用微信 `code2Session` API 获取真实 openid
- 新用户自动创建（username: `wx_` + 时间戳, password: BCrypt加密空串, userType: 1, nickname: "微信用户"）
- 返回 JWT token + userId + username + nickname

#### 5.2 AppBikeController — `/api/app/bike`

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/app/bike/nearby` | GET | 获取附近可租车辆 |
| `/api/app/bike/rent` | POST | 扫码租车 |

- `nearby`：查询所有 status=0 的车辆，用 `DistanceUtils.calculate()` 在 Java 端过滤距离 ≤ radius 的车辆
- `rent`：校验用户无进行中订单 → 查找车辆 → 创建订单 → 更新车辆状态为"使用中"，返回含 bikeCode 的订单对象

#### 5.3 AppOrderController — `/api/app/order`

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/app/order/riding` | GET | 当前骑行中的订单 |
| `/api/app/order/finish` | POST | 结束行程/结算 |
| `/api/app/order/list` | GET | 我的订单列表 |
| `/api/app/order/{id}` | GET | 订单详情 |

- `finish`：校验订单归属和状态 → 调用 BillingService 计费 → 更新订单（结束时间、时长、金额、状态） → 释放车辆状态为"待租"
- `list`：支持 `?status=0/1/2` 筛选

#### 5.4 AppRepairController — `/api/app/repair`

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/app/repair/submit` | POST | 提交故障上报 |

- 接收 `{ bikeCode, description, images }` JSON 请求体
- 内部通过 `bikeMapper.findByCode()` 将 bikeCode 转换为 bikeId
- 创建报修记录 + 自动更新车辆状态为"报修"

#### 5.5 AppScenicController — `/api/app`（公开接口，无需登录）

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/app/scenic/list` | GET | 景点列表 |
| `/api/app/area/list` | GET | 停车区域列表 |

#### 5.6 AppFileController — `/api/app/file`

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/app/file/upload` | POST | 文件上传 |

- 接收 MultipartFile，保存到 `file.upload-path` 目录
- 返回 `{ "url": "/uploads/uuid.ext" }` 供后续使用

---

### 6. 小程序 — API 层（7 个新文件）

| 文件 | 接口 |
|------|------|
| `api/auth.js` | `login(code)` |
| `api/bike.js` | `getNearbyBikes(lng, lat, radius)`, `rentBike(bikeCode, startLng, startLat)` |
| `api/order.js` | `getRidingOrder()`, `finishOrder(orderId, endLng, endLat)`, `getOrderList(status)`, `getOrderDetail(id)` |
| `api/repair.js` | `submitRepair(bikeCode, description, images)` |
| `api/scenic.js` | `getScenicList()`, `getAreaList()` |
| `api/file.js` | `uploadImage(filePath)` — 使用 `uni.uploadFile` 上传 |
| `utils/auth.js` | `requireAuth()` — 登录守卫，未登录时跳转登录页 |

---

### 7. 小程序 — 页面实现（8 个页面 + App.vue 全部重写）

#### 7.1 App.vue

- `onShow` 生命周期：检查当前用户是否有骑行中的订单，如有则弹窗提醒并提供"查看"按钮跳转到骑行页

#### 7.2 登录页 (`pages/login/index.vue`)

- 微信登录：调用 `uni.login()` → 后端 `/api/app/auth/login` → 存储 token/userInfo → 跳转首页
- 开发模式登录：生成 mock code 直接调用后端（方便 HBuilderX 预览调试）
- UI：Logo + 标语 + 两个按钮（微信登录 / 开发模式登录）

#### 7.3 首页 (`pages/index/index.vue`)

- 全屏 `<map>` 组件，获取用户定位（gcj02 坐标系）
- 加载附近可租车辆标记（绿色 callout 显示 bikeCode）
- 加载景点标记（蓝色 callout 显示景点名称）
- 底部悬浮操作栏：显示附近可用车辆数 + "扫码租车"按钮
- 点击车辆标记可跳转扫码确认页
- 扫码前检查登录状态

#### 7.4 扫码确认页 (`pages/scan/index.vue`)

- 显示车辆编码和所属区域信息
- 黄色提示条："确认后将开始计费，请仔细检查车辆状况"
- 点击"确认租车"：获取定位 → 调用 rentBike API → 成功后 redirectTo 骑行页

#### 7.5 骑行中页面 (`pages/riding/index.vue`)

- 大号实时计时器（每秒更新，基于 startTime 计算）
- 地图展示当前实时位置
- 订单信息卡片（订单编号、开始时间）
- "结束行程"按钮 → 确认弹窗 → 获取定位 → 调用 finishOrder API → redirectTo 订单详情页
- 支持 URL 传入 orderId 或自动查询骑行中订单

#### 7.6 订单列表 (`pages/order/index.vue`)

- Tab 筛选栏：全部 / 骑行中 / 已完成 / 已取消
- 订单卡片列表：显示订单编号、状态标签（彩色）、车辆编码、时间
- 已完成订单显示金额和时长
- 点击卡片跳转订单详情

#### 7.7 订单详情 (`pages/order-detail/index.vue`)

- 顶部状态横幅（蓝色=骑行中、绿色=已完成、红色=已取消）
- 订单信息卡片：订单编号、车辆编号、开始/结束时间
- 费用明细卡片（仅已完成）：骑行时长、订单金额
- 计费规则说明卡片

#### 7.8 故障上报 (`pages/repair/index.vue`)

- 车辆编号输入 + 扫码按钮
- 故障类型 Picker（6 种类型）
- 故障描述文本域
- 图片上传区域（最多 3 张，支持拍照/相册，使用 `uni.uploadFile` 上传到后端）
- 提交后显示成功 toast 并返回上一页

#### 7.9 个人中心 (`pages/mine/index.vue`)

- 渐变色用户头部：头像（昵称首字母）+ 昵称 + 登录状态
- 菜单列表：我的订单、故障上报、计费规则（弹窗展示）
- 退出登录按钮

---

### 8. 配置文件修改

- 修改 `manifest.json` — 添加微信定位权限声明 `scope.userLocation` 和 `requiredPrivateInfos`

---

## 新增/修改文件清单

### 后端新建文件（12 个）

```
server/src/main/java/com/bike/common/config/ResourceConfig.java
server/src/main/java/com/bike/module/order/entity/BikeOrder.java
server/src/main/java/com/bike/module/order/entity/RentDTO.java
server/src/main/java/com/bike/module/order/entity/FinishDTO.java
server/src/main/java/com/bike/module/order/service/BillingService.java
server/src/main/java/com/bike/module/app/controller/AppAuthController.java
server/src/main/java/com/bike/module/app/controller/AppBikeController.java
server/src/main/java/com/bike/module/app/controller/AppOrderController.java
server/src/main/java/com/bike/module/app/controller/AppRepairController.java
server/src/main/java/com/bike/module/app/controller/AppScenicController.java
server/src/main/java/com/bike/module/app/controller/AppFileController.java
```

### 后端修改文件（9 个）

```
server/src/main/resources/application.yml                          — +wechat, +billing 配置
server/.../common/config/WebMvcConfig.java                        — PermissionInterceptor 排除 /api/app/**
server/.../module/auth/entity/SysUser.java                        — +openid 字段
server/.../mapper/SysUserMapper.java                              — +findByOpenid()
server/.../mapper/BikeMapper.java                                 — +findByCode()
server/.../mapper/BikeRepairMapper.java                           — +insert()
server/.../mapper/BikeOrderMapper.java                            — 大幅扩展 CRUD 方法
server/src/main/resources/mapper/SysUserMapper.xml                — +openid 列 +findByOpenid 查询
server/src/main/resources/mapper/BikeMapper.xml                   — +findByCode 查询
server/src/main/resources/mapper/BikeRepairMapper.xml             — +insert 语句
server/src/main/resources/mapper/BikeOrderMapper.xml             — 完整重写，新增全部 CRUD SQL
```

### 小程序新建文件（7 个）

```
miniapp/api/auth.js
miniapp/api/bike.js
miniapp/api/order.js
miniapp/api/repair.js
miniapp/api/scenic.js
miniapp/api/file.js
miniapp/utils/auth.js
```

### 小程序修改文件（10 个）

```
miniapp/App.vue                     — +骑行订单检查
miniapp/manifest.json               — +定位权限声明
miniapp/pages/login/index.vue       — 完整微信登录实现 + 开发模式
miniapp/pages/index/index.vue       — 地图+车辆/景点标记+扫码
miniapp/pages/scan/index.vue        — 确认租车
miniapp/pages/riding/index.vue      — 实时计时+地图+结束行程
miniapp/pages/order/index.vue       — Tab筛选订单列表
miniapp/pages/order-detail/index.vue — 订单详情+费用明细
miniapp/pages/repair/index.vue      — 故障上报表单+图片上传
miniapp/pages/mine/index.vue        — 个人中心+菜单+退出
```

---

## 未完成 / 后续改进方向

### 阶段四：联调与测试

- [ ] **数据库迁移**：执行 ALTER TABLE 为 sys_user 添加 openid 列
- [ ] **后端启动测试**：`cd server && mvn spring-boot:run`，验证全部 `/api/app/**` 接口
- [ ] **登录流程联调**：使用开发模式登录（`POST /api/app/auth/login` with `{"code":"test123"}`），验证 token 返回
- [ ] **租车完整流程测试**：登录 → 附近车辆 → 扫码租车 → 骑行计时 → 结束行程 → 查看订单详情
- [ ] **计费逻辑测试**：测试不同时长下的计费结果（< 10min 免费、> 10min 超时计费、每日封顶）
- [ ] **故障上报测试**：提交故障 → 验证车辆状态变为"报修" → 管理后台处理报修 → 恢复车辆
- [ ] **边界场景测试**：并发租车、网络异常断开、未登录访问、token 过期

### 功能增强建议

- [ ] **微信 AppID 配置**：`manifest.json` 和 `application.yml` 中的 `wechat.appid` / `wechat.secret` 需替换为真实值
- [ ] **TabBar 图标**：`miniapp/static/` 下的 6 个 PNG 文件目前为 0 字节占位，需要替换为真实图标
- [ ] **高德地图 SDK 集成**：当前使用 uni-app 原生 `<map>` 组件（微信端为腾讯地图），如需使用高德地图需引入 `@amap/amap-wx` SDK 并在 manifest.json 配置高德 key
- [ ] **停车区域合规校验**：当前结束行程未校验是否在停车区域内，可增加 `DistanceUtils.isInArea()` 检查，不在区域内时提示额外费用或拒绝还车
- [ ] **订单取消功能**：当前仅有"骑行中→已完成"流程，可增加"骑行中→已取消"的取消订单接口
- [ ] **用户个人信息编辑**：个人中心可增加修改昵称、头像等功能
- [ ] **骑行轨迹记录**：骑行过程中定期记录 GPS 坐标，结束后在订单详情展示骑行轨迹路线
- [ ] **消息推送**：订单完成时通过微信订阅消息推送账单通知
- [ ] **并发控制**：当前租车流程无分布式锁，高并发下可能出现同一车辆被多人租用，可考虑 Redis 分布式锁或数据库乐观锁
- [ ] **管理后台查看小程序用户**：用户管理页面已支持按 `userType=1` 筛选小程序用户，可增加禁用/解禁小程序用户的功能
- [ ] **订单管理（管理后台）**：当前管理后台无订单管理页面，可新增订单列表、详情查看等功能
- [ ] **图片存储优化**：当前图片存储在本地 `./uploads/` 目录，生产环境应迁移到 OSS/S3 等对象存储服务

### 已知环境问题

- **Vite 8 与 Node 20 不兼容**：管理后台运行 `npm run dev` 需升级 Node 到 v22+ 或将 Vite 降级到 v5
- **数据库 openid 列**：需手动执行 ALTER TABLE SQL，否则小程序登录会报错

---

## 关键信息速查

| 项目 | 值 |
|------|-----|
| MySQL 容器 | `docker start bike-mysql` |
| 数据库 | `bike_app`，`root / 123456`，`localhost:3306` |
| 后端启动 | `cd server && mvn spring-boot:run`，端口 `8080` |
| 管理后台启动 | `cd admin && npm run dev`，端口 `5173`（需 Node 22+） |
| 管理后台登录 | `admin / 123456` |
| 小程序登录 | 开发模式下传任意 code 即可登录 |
| 后端编译验证 | `cd server && mvn compile` — 本次已完成，通过 |
| 计费规则 | 起步价 ¥2.00 / 前10分钟免费 / 超出 ¥0.50/分钟 / 每日封顶 ¥30.00 |
