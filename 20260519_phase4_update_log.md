# 更新日志 — 20260519 阶段四：联调与测试

## 本次完成内容

### 1. 数据库迁移

- 执行 SQL 为 `sys_user` 表添加 `openid` 字段和唯一索引：

```sql
ALTER TABLE sys_user ADD COLUMN openid VARCHAR(64) DEFAULT '' COMMENT '微信openid' AFTER avatar;
ALTER TABLE sys_user ADD UNIQUE KEY uk_openid (openid);
```

- 该字段用于微信小程序登录时唯一标识用户，支持"查找或自动创建"逻辑。

---

### 2. 管理后台 Vite 降级 v5

**背景**：原 `vite: ^8.0.12` 与 Node.js v20.18.1 不兼容（Vite 8 要求 Node 22+），导致 `npm run dev` 无法启动。

#### 2.1 依赖版本变更

文件：`admin/package.json`

| 依赖 | 变更前 | 变更后 |
|------|--------|--------|
| `vite` | `^8.0.12` | `^5.4.21` |
| `@vitejs/plugin-vue` | `^6.0.6` | `^5.2.4` |

- 删除 `node_modules` 和 `package-lock.json`，重新 `npm install`

#### 2.2 vite.config.js 修复

文件：`admin/vite.config.js`

- 新增 `path` alias 配置：`'@' → path.resolve(__dirname, 'src')`，解决 Vite 降级后 `@/` 路径别名失效的问题
- 新增 `server.port: 5173` 明确指定端口

#### 2.3 Bug 修复：handleRepair 命名冲突

文件：`admin/src/views/repair/index.vue`

- 第 89 行从 `@/api/repair` 导入了 `handleRepair`，与第 134 行定义的本地函数 `handleRepair` 重名
- 修复：将 import 重命名为 `handleRepair as apiHandleRepair`，第 144 行调用处同步修改

---

### 3. 小程序 H5 模式支持

**背景**：miniapp 原为纯 HBuilderX 项目（无 `package.json`），无法通过命令行启动。为支持浏览器直接访问进行联调，添加了完整的 uni-app CLI + Vite H5 构建支持。

#### 3.1 项目结构调整

- 新建 `src/` 目录，将以下目录/文件移入：
  - `api/`、`App.vue`、`components/`、`main.js`、`manifest.json`、`pages/`、`pages.json`、`static/`、`store/`、`utils/`
- 这是因为 `@dcloudio/vite-plugin-uni` 要求标准 CLI 项目结构（源文件在 `src/` 下）

#### 3.2 新建文件

| 文件 | 说明 |
|------|------|
| `miniapp/package.json` | uni-app H5 依赖声明 |
| `miniapp/index.html` | H5 入口 HTML |

#### 3.3 package.json 依赖

```json
{
  "dependencies": {
    "vue": "^3.4.21"
  },
  "devDependencies": {
    "@dcloudio/vite-plugin-uni": "3.0.0-alpha-5010120260519001",
    "@dcloudio/uni-h5": "3.0.0-alpha-5010120260519001",
    "@dcloudio/uni-cli-shared": "3.0.0-alpha-5010120260519001",
    "@dcloudio/uni-shared": "3.0.0-alpha-5010120260519001",
    "@dcloudio/uni-components": "3.0.0-alpha-5010120260519001",
    "@dcloudio/uni-app": "3.0.0-alpha-5010120260519001",
    "vite": "5.2.8"
  }
}
```

> `vite` 版本固定为 `5.2.8`（uni-app 编译器要求的精确版本）。

#### 3.4 启动命令

```bash
cd miniapp
npm run dev:h5    # 开发模式，端口 5174
npm run build:h5  # 生产构建
```

---

### 4. TabBar 图标生成

文件：`miniapp/src/static/` 下 6 个 PNG 文件

| 文件 | 说明 |
|------|------|
| `home.png` / `home-active.png` | 首页 Tab 图标（灰色/蓝色） |
| `order.png` / `order-active.png` | 订单 Tab 图标（灰色/蓝色） |
| `mine.png` / `mine-active.png` | 我的 Tab 图标（灰色/蓝色） |

- 尺寸：81×81px，RGBA 格式，圆形纯色图标
- 原文件为 0 字节占位，已替换为有效 PNG

---

### 5. 联调测试

#### 5.1 测试数据

通过 SQL 插入了测试数据：

```sql
-- 5 辆单车（BK001-BK004 可租，BK005 使用中）
INSERT INTO bike (bike_code, status, lng, lat, area_id) VALUES
('BK001', 0, 120.1550, 30.2740, 1),
('BK002', 0, 120.1560, 30.2750, 1),
('BK003', 0, 120.1530, 30.2730, 1),
('BK004', 0, 120.1540, 30.2760, 1),
('BK005', 1, 120.1570, 30.2745, 1);

-- 3 个景点
INSERT INTO scenic_spot (name, description, lng, lat) VALUES
('西湖断桥', '著名景点，白蛇传传说发源地', 120.1510, 30.2590),
('雷峰塔', '西湖十景之一', 120.1489, 30.2314),
('三潭印月', '西湖标志性景观', 120.1429, 30.2441);
```

#### 5.2 接口测试结果

| 接口 | 方法 | 结果 |
|------|------|------|
| `/api/app/auth/login` | POST | ✅ dev-mode mock 登录正常，返回 token |
| `/api/app/bike/nearby` | GET | ✅ 返回 4 辆可租车辆（排除 status≠0） |
| `/api/app/scenic/list` | GET | ✅ 返回 3 个景点 |
| `/api/app/bike/rent` | POST | ✅ 创建订单，车辆状态变为使用中 |
| `/api/app/order/riding` | GET | ✅ 返回当前骑行中订单 |
| `/api/app/order/finish` | POST | ✅ 计费正确，车辆恢复待租 |
| `/api/app/order/list` | GET | ✅ 返回用户订单列表 |
| `/api/app/order/{id}` | GET | ✅ 返回订单详情 |
| `/api/app/repair/submit` | POST | ✅ 提交故障，车辆状态变为报修 |

#### 5.3 完整业务流程验证

```
开发模式登录 → 获取 token → 查看附近车辆（4辆） → 扫码租车 BK001
  → 创建订单 → 查询骑行中订单 → 结束行程 → 计费 ¥2.00（基础费，<10min）
  → 订单状态变为已完成 → 车辆 BK001 恢复为待租 → 订单列表显示 1 条记录
  → 故障上报 BK003 → BK003 状态变为报修 → 可用车辆变为 3 辆
```

全部通过。

---

## 新增/修改文件清单

### 新建文件（3 个）

```
miniapp/package.json              — uni-app H5 依赖声明
miniapp/index.html                — H5 入口 HTML
miniapp/src/static/*.png × 6     — TabBar 图标（替换 0 字节占位）
```

### 修改文件（3 个）

```
admin/package.json                — Vite 降级 v5 + plugin-vue 降级
admin/vite.config.js              — +路径别名 @ + 端口配置
admin/src/views/repair/index.vue  — handleRepair 命名冲突修复
```

### 项目结构调整

```
miniapp/ 下的源文件从根目录移入 src/ 目录：
  api/, App.vue, components/, main.js, manifest.json,
  pages/, pages.json, static/, store/, utils/ → src/
```

---

## 已知问题

- **MySQL 时区不一致**：Docker 容器中 MySQL 使用 SYSTEM 时区（可能是 UTC），而 Java 使用系统时区（UTC+8），导致计费测试中时间差计算偏差。生产环境需统一 MySQL 时区为 `Asia/Shanghai`。
- **TabBar 图标为简易占位**：当前图标为纯色圆形极简样式，正式发布需替换为设计师提供的图标。
- **uni-app 版本为 alpha**：`@dcloudio` 依赖使用的是 alpha 版本，可能存在不稳定因素。正式项目建议锁定版本。

---

## 关键信息速查

| 项目 | 值 |
|------|-----|
| MySQL 容器 | `docker start bike-mysql` |
| 数据库 | `bike_app`，`root / 123456`，`localhost:3306` |
| 后端启动 | `cd server && mvn spring-boot:run`，端口 `8080` |
| 管理后台启动 | `cd admin && npm install && npm run dev`，端口 `5173` |
| 小程序 H5 启动 | `cd miniapp && npm install && npm run dev:h5`，端口 `5174` |
| 管理后台登录 | `admin / 123456` |
| 小程序登录 | 开发模式下传任意 code 即可登录 |
| Node 版本要求 | v20+（Vite 5 兼容） |
