# 景区共享单车租赁系统

## 项目概述

本项目为景区共享单车租赁系统，采用前后端分离架构，包含管理后台（Web 端）和用户端（小程序端）。聚焦核心租赁业务闭环，以最小可行性产品（MVP）为原则，实现从车辆管理、扫码租赁、骑行计费到订单结算的完整流程。

---

## 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| 后端框架 | Spring Boot | 业务逻辑处理 |
| ORM | MyBatis | 数据持久化 |
| 数据库 | MySQL | 核心关系型数据库 |
| 管理后台前端 | Vue3 + Element Plus | 后台管理界面 |
| 用户端前端 | uni-app | 跨端小程序 |
| 地图服务 | 高德地图小程序 SDK | 地图渲染与坐标系转换 |
| 权限控制 | RBAC 模型 | 角色权限管理 |

---

## 系统架构

```
┌──────────────────────────────────────────────────────┐
│                   景区共享单车系统                       │
├──────────────────┬───────────────────────────────────┤
│   管理后台 (Web)  │          用户端 (小程序)              │
│  Vue3+ElementPlus│           uni-app                  │
├──────────────────┼───────────────────────────────────┤
│              Spring Boot + MyBatis                    │
├──────────────────┴───────────────────────────────────┤
│                    MySQL                              │
└──────────────────────────────────────────────────────┘
```

---

## 项目结构

```
bike_app/
├── admin/                          # 管理后台前端 (Vue3)
│   ├── public/
│   ├── src/
│   │   ├── api/                    # 接口请求封装
│   │   ├── assets/                 # 静态资源
│   │   ├── components/             # 公共组件
│   │   ├── layout/                 # 布局组件
│   │   ├── router/                 # 路由配置
│   │   ├── store/                  # 状态管理 (Pinia)
│   │   ├── utils/                  # 工具函数
│   │   └── views/                  # 页面视图
│   │       ├── dashboard/          # 首页仪表盘
│   │       ├── system/             # 系统管理（用户/角色/菜单/日志）
│   │       ├── bike/               # 车辆管理
│   │       ├── scenic/             # 景点与区域管理
│   │       └── repair/             # 报修管理
│   ├── package.json
│   └── vite.config.js
│
├── server/                         # 后端服务 (Spring Boot)
│   ├── src/main/java/com/bike/
│   │   ├── BikeApplication.java    # 启动类
│   │   ├── common/                 # 公共模块
│   │   │   ├── config/             # 配置类（跨域、MyBatis、安全等）
│   │   │   ├── exception/          # 全局异常处理
│   │   │   ├── result/             # 统一响应封装
│   │   │   └── utils/              # 工具类（距离计算、二维码生成等）
│   │   ├── security/               # 权限与认证模块
│   │   │   ├── interceptor/        # 权限拦截器
│   │   │   └── annotation/         # 自定义权限注解
│   │   ├── module/
│   │   │   ├── auth/               # 认证模块（登录/登出）
│   │   │   ├── system/             # 系统管理模块（用户/角色/菜单/日志）
│   │   │   ├── bike/               # 车辆管理模块
│   │   │   ├── scenic/             # 景点与区域管理模块
│   │   │   ├── order/              # 订单与计费模块
│   │   │   └── repair/             # 报修管理模块
│   │   └── mapper/                 # MyBatis Mapper 接口
│   ├── src/main/resources/
│   │   ├── application.yml         # 主配置文件
│   │   ├── application-dev.yml     # 开发环境配置
│   │   ├── application-prod.yml    # 生产环境配置
│   │   └── mapper/                 # MyBatis XML 映射文件
│   └── pom.xml
│
├── miniapp/                        # 用户端小程序 (uni-app)
│   ├── pages/
│   │   ├── index/                  # 首页（地图展示）
│   │   ├── scan/                   # 扫码租车页
│   │   ├── riding/                 # 骑行中页面
│   │   ├── order/                  # 订单列表
│   │   ├── order-detail/           # 订单详情
│   │   ├── repair/                 # 故障上报
│   │   ├── mine/                   # 个人中心
│   │   └── login/                  # 登录页
│   ├── components/                 # 公共组件
│   ├── api/                        # 接口请求封装
│   ├── utils/                      # 工具函数（定位、距离计算等）
│   ├── store/                      # 状态管理
│   ├── static/                     # 静态资源
│   ├── pages.json                  # 页面路由配置
│   ├── manifest.json               # 应用配置
│   ├── App.vue
│   └── main.js
│
├── sql/                            # 数据库脚本
│   ├── schema.sql                  # 建表语句
│   └── init-data.sql               # 初始化数据
│
└── README.md                       # 本文件
```

---

## 数据库设计

### ER 关系概览

```
sys_user ──┬── sys_user_role ── sys_role ── sys_role_menu ── sys_menu
           │
           └── bike_order

bike ── bike_repair
scenic_spot
parking_area
operation_log
```

### 核心表结构

#### 1. 车辆表 `bike`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| bike_code | VARCHAR(32) | 唯一标识码（对应二维码） |
| status | TINYINT | 状态：0-待租, 1-使用中, 2-报修, 3-报废 |
| lng | DECIMAL(10,6) | 当前经度 |
| lat | DECIMAL(10,6) | 当前纬度 |
| area_id | BIGINT | 所属停车区域 ID |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### 2. 景点表 `scenic_spot`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR(100) | 景点名称 |
| description | TEXT | 景点描述 |
| lng | DECIMAL(10,6) | 经度 |
| lat | DECIMAL(10,6) | 纬度 |
| cover_image | VARCHAR(255) | 封面图片 URL |
| create_time | DATETIME | 创建时间 |

#### 3. 停车区域表 `parking_area`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR(100) | 区域名称 |
| center_lng | DECIMAL(10,6) | 区域中心经度 |
| center_lat | DECIMAL(10,6) | 区域中心纬度 |
| radius | INT | 允许停车半径（米） |
| create_time | DATETIME | 创建时间 |

#### 4. 订单表 `bike_order`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| order_no | VARCHAR(32) | 订单编号 |
| user_id | BIGINT | 用户 ID |
| bike_id | BIGINT | 车辆 ID |
| start_time | DATETIME | 开始骑行时间 |
| end_time | DATETIME | 结束骑行时间 |
| start_lng | DECIMAL(10,6) | 起始经度 |
| start_lat | DECIMAL(10,6) | 起始纬度 |
| end_lng | DECIMAL(10,6) | 结束经度 |
| end_lat | DECIMAL(10,6) | 结束纬度 |
| duration | INT | 骑行时长（分钟） |
| amount | DECIMAL(8,2) | 订单金额（元） |
| status | TINYINT | 状态：0-骑行中, 1-已完成, 2-已取消 |

#### 5. 报修表 `bike_repair`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| bike_id | BIGINT | 车辆 ID |
| user_id | BIGINT | 上报用户 ID |
| description | TEXT | 故障描述 |
| images | VARCHAR(1000) | 图片 URL 列表（逗号分隔） |
| status | TINYINT | 状态：0-待处理, 1-处理中, 2-已完成 |
| admin_remark | VARCHAR(255) | 管理员处理备注 |
| create_time | DATETIME | 上报时间 |
| handle_time | DATETIME | 处理时间 |

#### 6. 系统用户表 `sys_user`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR(50) | 用户名 |
| password | VARCHAR(128) | 密码（加密存储） |
| nickname | VARCHAR(50) | 昵称 |
| phone | VARCHAR(20) | 手机号 |
| avatar | VARCHAR(255) | 头像 URL |
| user_type | TINYINT | 类型：0-后台管理员, 1-小程序用户 |
| status | TINYINT | 状态：0-正常, 1-禁用 |
| create_time | DATETIME | 创建时间 |

> 注：`sys_role`、`sys_menu`、`sys_user_role`、`sys_role_menu`、`operation_log` 等标准 RBAC 表详见 `sql/schema.sql`。

---

## 功能模块详细设计

### 模块一：基础信息管理（后台 CRUD）

**功能入口**：管理后台

#### 1.1 车辆管理
- 新增车辆：录入车辆信息，系统自动生成唯一 `bike_code` 并生成对应二维码图片
- 车辆列表：分页展示所有车辆，支持按状态、编码筛选
- 编辑车辆：修改车辆坐标、所属区域等信息
- 状态变更：管理员可手动调整车辆状态（待租/报修/报废）
- 二维码导出：支持批量导出车辆二维码用于打印张贴

#### 1.2 景点管理
- 景点 CRUD：录入/编辑/删除景区内基础景点信息
- 地图标注：录入景点时支持在地图上选点获取经纬度

#### 1.3 停车区域管理
- 区域 CRUD：设置停车区域的名称、中心坐标、允许停车半径
- 区域可视化：在管理后台地图上展示所有停车区域范围

---

### 模块二：核心租赁业务（状态流转）

**核心状态机**：

```
              扫码租车                结束行程
  [待租] ──────────→ [使用中] ──────────→ [待租]
    ↑                    │                   ↑
    │              主动报修/超时             │
    │                    ↓                   │
    │               [报修] ───→ 管理员处理 ──┘
    │
    └────── 管理员报废 ──→ [报废]
```

#### 2.1 扫码租车流程
1. 用户打开小程序，定位获取当前位置
2. 扫描车身上的二维码，解析出 `bike_code`
3. 小程序向后端发起租车请求（携带 bike_code + 用户 token + 当前经纬度）
4. 后端校验：
   - 用户是否已登录
   - 该车辆是否存在且状态为「待租」
   - 用户是否有进行中的订单
5. 校验通过后，开启数据库事务：
   - 更新车辆状态为「使用中」
   - 创建订单记录，写入 `start_time`、起始坐标
6. 返回订单信息给小程序，用户进入骑行页面

#### 2.2 结算流程
1. 用户在小程序点击「结束行程」
2. 小程序获取当前经纬度，向后端发起结算请求
3. 后端校验：
   - 订单是否存在且状态为「骑行中」
   - 当前位置是否在合规停车区域内（利用 Haversine 公式计算与停车区域中心的球面距离）
4. 校验通过后，开启数据库事务：
   - 计算骑行时长 `duration = (endTime - startTime)` 分钟
   - 按计费规则计算金额：`amount = 基础费用 + (duration - 免费时长) * 单价/分钟`
   - 更新订单状态为「已完成」，写入结束时间、结束坐标、时长、金额
   - 将车辆状态恢复为「待租」
5. 返回订单结算信息给小程序

#### 2.3 计费规则（默认值，可在后台配置）
| 规则项 | 默认值 |
|--------|--------|
| 基础费用 | 2.00 元（起步价） |
| 免费时长 | 10 分钟 |
| 超出单价 | 0.50 元/分钟 |
| 每日封顶 | 30.00 元 |

---

### 模块三：地图与定位

#### 3.1 小程序端地图功能
- 首页地图：展示景区范围内所有「待租」状态的车辆标记点
- 景点展示：在地图上叠加景区景点信息
- 停车区域：以圆形区域标记合规停车点
- 实时定位：获取用户当前位置，展示在地图上

#### 3.2 距离计算（Haversine 公式）

```
R = 6371000 (地球平均半径，单位：米)

dLat = (lat2 - lat1) * π / 180
dLng = (lng2 - lng1) * π / 180

a = sin²(dLat/2) + cos(lat1 * π/180) * cos(lat2 * π/180) * sin²(dLng/2)
c = 2 * atan2(√a, √(1-a))
distance = R * c
```

用于判断：
- 用户是否在停车区域内（`distance ≤ parking_area.radius`）
- 用户附近可租赁的车辆列表排序

---

### 模块四：用户反馈与运维

#### 4.1 故障上报（小程序端）
- 骑行中或骑行结束后，用户可提交故障上报
- 表单字段：故障类型（下拉选择）、文字描述、图片上传（最多 3 张）
- 提交后后端创建报修记录，并自动将对应车辆状态更新为「报修」

#### 4.2 报修处理（管理后台）
- 报修列表：展示所有报修记录，支持按状态筛选
- 处理报修：管理员添加处理备注，将报修状态改为「已完成」
- 恢复车辆：报修完成后，管理员手动将车辆状态恢复为「待租」

---

### 模块五：系统管理（RBAC）

#### 5.1 用户管理
- 管理员账户的增删改查
- 小程序用户查看与管理（禁用/解禁）

#### 5.2 角色管理
- 预设角色：超级管理员、运营管理员、维修人员
- 角色的增删改查
- 角色关联菜单权限

#### 5.3 菜单管理
- 后台菜单的树形结构管理
- 按钮级别权限控制

#### 5.4 操作日志
- 记录管理后台的关键操作（增删改）
- 日志列表查询，包含操作人、操作时间、操作内容

---

## 接口设计概览

### 管理后台接口

| 模块 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 认证 | `/api/auth/login` | POST | 管理员登录 |
| 认证 | `/api/auth/logout` | POST | 登出 |
| 车辆 | `/api/bike/list` | GET | 车辆分页列表 |
| 车辆 | `/api/bike` | POST | 新增车辆 |
| 车辆 | `/api/bike/{id}` | PUT | 编辑车辆 |
| 车辆 | `/api/bike/{id}` | DELETE | 删除车辆 |
| 车辆 | `/api/bike/{id}/status` | PUT | 更新车辆状态 |
| 车辆 | `/api/bike/qrcode/{id}` | GET | 获取车辆二维码 |
| 景点 | `/api/scenic/list` | GET | 景点列表 |
| 景点 | `/api/scenic` | POST | 新增景点 |
| 景点 | `/api/scenic/{id}` | PUT | 编辑景点 |
| 景点 | `/api/scenic/{id}` | DELETE | 删除景点 |
| 区域 | `/api/area/list` | GET | 停车区域列表 |
| 区域 | `/api/area` | POST | 新增停车区域 |
| 报修 | `/api/repair/list` | GET | 报修列表 |
| 报修 | `/api/repair/{id}/handle` | PUT | 处理报修 |
| 系统 | `/api/system/user/*` | - | 用户管理 CRUD |
| 系统 | `/api/system/role/*` | - | 角色管理 CRUD |
| 系统 | `/api/system/menu/*` | - | 菜单管理 CRUD |
| 系统 | `/api/system/log/*` | - | 操作日志查询 |

### 小程序端接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/app/auth/login` | POST | 小程序用户登录（微信授权） |
| `/api/app/bike/nearby` | GET | 获取附近可租车辆 |
| `/api/app/bike/rent` | POST | 扫码租车 |
| `/api/app/order/finish` | POST | 结束行程/结算 |
| `/api/app/order/list` | GET | 我的订单列表 |
| `/api/app/order/{id}` | GET | 订单详情 |
| `/api/app/order/riding` | GET | 当前骑行中的订单 |
| `/api/app/repair/submit` | POST | 提交故障上报 |
| `/api/app/scenic/list` | GET | 景点列表 |
| `/api/app/area/list` | GET | 停车区域列表 |

---

## 开发计划

### 阶段一：基础搭建（预计 3 天）

- [ ] 初始化 Spring Boot 后端项目，配置 MyBatis + MySQL
- [ ] 搭建统一响应封装、全局异常处理
- [ ] 实现 RBAC 权限框架（用户、角色、菜单、拦截器）
- [ ] 编写数据库建表脚本并初始化基础数据
- [ ] 初始化 Vue3 管理后台项目（Vite + Element Plus + Pinia + Vue Router）
- [ ] 实现管理后台登录页面和基础布局框架
- [ ] 初始化 uni-app 小程序项目

### 阶段二：管理后台开发（预计 4 天）

- [ ] 系统管理页面：用户管理、角色管理、菜单管理
- [ ] 操作日志记录与查询页面
- [ ] 车辆管理页面（CRUD + 状态管理 + 二维码生成）
- [ ] 景点管理页面（CRUD + 地图选点）
- [ ] 停车区域管理页面（CRUD + 地图可视化）
- [ ] 报修管理页面（列表 + 处理）
- [ ] 首页仪表盘（车辆统计、订单统计、报修统计）

### 阶段三：小程序端开发（预计 5 天）

- [ ] 用户登录（微信授权登录对接）
- [ ] 首页地图集成（高德地图 SDK + 车辆标记 + 景点展示）
- [ ] 扫码租车功能（调用相机 + 解析二维码 + 发起租车）
- [ ] 骑行中页面（实时计时 + 地图轨迹展示）
- [ ] 结束行程 + 结算页面
- [ ] 订单列表与订单详情页面
- [ ] 故障上报页面（表单 + 图片上传）
- [ ] 个人中心页面

### 阶段四：联调与测试（预计 2 天）

- [ ] 前后端接口联调
- [ ] 租赁业务完整流程测试
- [ ] 边界场景测试（并发租车、网络异常、未在停车区域还车）
- [ ] Bug 修复与优化

---

## 部署说明（参考）

### 后端部署

```bash
# 编译打包
cd server
mvn clean package -DskipTests

# 运行
java -jar target/bike-server.jar --spring.profiles.active=prod
```

### 管理后台部署

```bash
cd admin
npm install
npm run build
# 将 dist 目录部署到 Nginx
```

### 小程序发布

```bash
cd miniapp
# 使用 HBuilderX 或微信开发者工具上传发布
```

---

## 开发环境要求

| 工具 | 版本要求 |
|------|----------|
| JDK | 17+ |
| Maven | 3.8+ |
| MySQL | 8.0+ |
| Node.js | 18+ |
| HBuilderX | 最新版（uni-app 开发） |
| 微信开发者工具 | 最新版（小程序调试） |
