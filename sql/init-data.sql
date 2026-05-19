-- ========================================
-- 景区共享单车租赁系统 - 初始化数据
-- ========================================

USE bike_app;

-- ----------------------------------------
-- 1. 初始管理员账户
-- 密码: 123456 (BCrypt加密)
-- ----------------------------------------
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `user_type`, `status`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', 0, 0);

-- ----------------------------------------
-- 2. 预设角色
-- ----------------------------------------
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`, `status`) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '拥有系统所有权限', 0),
(2, '运营管理员', 'OPERATOR', '负责车辆、景点、区域、报修管理', 0),
(3, '维修人员', 'REPAIRER', '负责报修处理', 0);

-- ----------------------------------------
-- 3. 用户角色关联
-- ----------------------------------------
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1);

-- ----------------------------------------
-- 4. 菜单数据
-- ----------------------------------------
-- 一级目录
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `icon`, `sort_order`) VALUES
(1, 0, '首页', 1, '/dashboard', 'dashboard/index', 'Odometer', 1),
(2, 0, '系统管理', 0, '/system', '', 'Setting', 2),
(3, 0, '车辆管理', 0, '/bike', '', 'Bicycle', 3),
(4, 0, '景点与区域', 0, '/scenic', '', 'Location', 4),
(5, 0, '报修管理', 0, '/repair', '', 'Tools', 5);

-- 二级菜单 - 系统管理
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `sort_order`) VALUES
(21, 2, '用户管理', 1, '/system/user', 'system/user/index', 'system:user:list', 'User', 1),
(22, 2, '角色管理', 1, '/system/role', 'system/role/index', 'system:role:list', 'UserFilled', 2),
(23, 2, '菜单管理', 1, '/system/menu', 'system/menu/index', 'system:menu:list', 'Menu', 3),
(24, 2, '操作日志', 1, '/system/log', 'system/log/index', 'system:log:list', 'Document', 4);

-- 二级菜单 - 车辆管理
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `sort_order`) VALUES
(31, 3, '车辆列表', 1, '/bike/list', 'bike/index', 'bike:list', 'List', 1);

-- 二级菜单 - 景点与区域
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `sort_order`) VALUES
(41, 4, '景点管理', 1, '/scenic/spot', 'scenic/spot/index', 'scenic:list', 'PictureFilled', 1),
(42, 4, '停车区域', 1, '/scenic/area', 'scenic/area/index', 'area:list', 'MapLocation', 2);

-- 二级菜单 - 报修管理
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `sort_order`) VALUES
(51, 5, '报修列表', 1, '/repair/list', 'repair/index', 'repair:list', 'WarningFilled', 1);

-- 按钮权限 - 系统管理
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `permission`, `sort_order`) VALUES
(211, 21, '用户新增', 2, '', 'system:user:add', 1),
(212, 21, '用户编辑', 2, '', 'system:user:edit', 2),
(213, 21, '用户删除', 2, '', 'system:user:delete', 3),
(221, 22, '角色新增', 2, '', 'system:role:add', 1),
(222, 22, '角色编辑', 2, '', 'system:role:edit', 2),
(223, 22, '角色删除', 2, '', 'system:role:delete', 3),
(231, 23, '菜单新增', 2, '', 'system:menu:add', 1),
(232, 23, '菜单编辑', 2, '', 'system:menu:edit', 2),
(233, 23, '菜单删除', 2, '', 'system:menu:delete', 3);

-- 按钮权限 - 车辆管理
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `permission`, `sort_order`) VALUES
(311, 31, '车辆新增', 2, '', 'bike:add', 1),
(312, 31, '车辆编辑', 2, '', 'bike:edit', 2),
(313, 31, '车辆删除', 2, '', 'bike:delete', 3),
(314, 31, '状态变更', 2, '', 'bike:status', 4);

-- 按钮权限 - 景点管理
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `permission`, `sort_order`) VALUES
(411, 41, '景点新增', 2, '', 'scenic:add', 1),
(412, 41, '景点编辑', 2, '', 'scenic:edit', 2),
(413, 41, '景点删除', 2, '', 'scenic:delete', 3);

-- 按钮权限 - 停车区域
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `permission`, `sort_order`) VALUES
(421, 42, '区域新增', 2, '', 'area:add', 1),
(422, 42, '区域编辑', 2, '', 'area:edit', 2),
(423, 42, '区域删除', 2, '', 'area:delete', 3);

-- 按钮权限 - 报修管理
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `permission`, `sort_order`) VALUES
(511, 51, '报修处理', 2, '', 'repair:handle', 1);

-- ----------------------------------------
-- 5. 超级管理员拥有所有菜单权限
-- ----------------------------------------
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `id` FROM `sys_menu`;
