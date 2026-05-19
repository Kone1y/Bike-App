<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo">
        <span v-if="!isCollapse">共享单车管理</span>
        <span v-else>🚲</span>
      </div>
      <el-menu :default-active="route.path" :collapse="isCollapse" router background-color="#304156"
        text-color="#bfcbd9" active-text-color="#409eff" :collapse-transition="false">
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>首页</template>
        </el-menu-item>
        <el-sub-menu index="system">
          <template #title><el-icon><Setting /></el-icon>系统管理</template>
          <el-menu-item index="/system/user"><el-icon><User /></el-icon>用户管理</el-menu-item>
          <el-menu-item index="/system/role"><el-icon><UserFilled /></el-icon>角色管理</el-menu-item>
          <el-menu-item index="/system/menu"><el-icon><Menu /></el-icon>菜单管理</el-menu-item>
          <el-menu-item index="/system/log"><el-icon><Document /></el-icon>操作日志</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="bike">
          <template #title><el-icon><Bicycle /></el-icon>车辆管理</template>
          <el-menu-item index="/bike/list"><el-icon><List /></el-icon>车辆列表</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="scenic">
          <template #title><el-icon><Location /></el-icon>景点与区域</template>
          <el-menu-item index="/scenic/spot"><el-icon><PictureFilled /></el-icon>景点管理</el-menu-item>
          <el-menu-item index="/scenic/area"><el-icon><MapLocation /></el-icon>停车区域</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="repair">
          <template #title><el-icon><Tools /></el-icon>报修管理</template>
          <el-menu-item index="/repair/list"><el-icon><WarningFilled /></el-icon>报修列表</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" /><Expand v-else />
          </el-icon>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">{{ userStore.nickname }} <el-icon><ArrowDown /></el-icon></span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { logout } from '@/api/auth'
import { ElMessage } from 'element-plus'
import {
  Odometer, Setting, User, UserFilled, Menu, Document, Bicycle, List,
  Location, PictureFilled, MapLocation, Tools, WarningFilled,
  Fold, Expand, ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const handleCommand = async (command) => {
  if (command === 'logout') {
    await logout()
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.layout-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  background-color: #263445;
  white-space: nowrap;
}

.layout-header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #333;
}

.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  color: #333;
}

.layout-main {
  background-color: #f0f2f5;
}
</style>
